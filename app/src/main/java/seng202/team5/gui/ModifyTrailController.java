package seng202.team5.gui;

import com.google.gson.Gson;
import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import seng202.team5.App;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.services.MatchmakingService;
import seng202.team5.services.RegionFinder;
import seng202.team5.services.SearchService;
import seng202.team5.utils.StringManipulator;
import seng202.team5.utils.TrailsProcessor;

import java.util.List;

/**
 * Controller for the modify trail screen
 */
public class ModifyTrailController extends Controller {

    private Trail trail;
    private Controller lastController;
    private SqlBasedTrailRepo sqlBasedTrailRepo;
    private SearchService searchService;
    private RegionFinder regionFinder;

    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;

    /**
     * Launches the screen with navigator
     *
     * @param navigator      screen navigator
     * @param trail          the selected trail
     * @param lastController controller of last screen user interacted with
     * @param searchService  searchService
     */
    public ModifyTrailController(ScreenNavigator navigator, Trail trail, Controller lastController,
            SearchService searchService) {
        super(navigator);
        this.trail = trail;
        this.lastController = lastController;
        this.searchService = searchService;
        this.regionFinder = new RegionFinder();
        this.sqlBasedTrailRepo = new SqlBasedTrailRepo(App.getDatabaseService());
    }

    @FXML
    private TextField trailNameTextField;
    @FXML
    private TextField translationTextField;
    @FXML
    private ComboBox<String> difficultyComboBox;
    @FXML
    private ComboBox<String> trailTypeComboBox;
    @FXML
    private TextField completionTimeTextField;
    @FXML
    private TextArea trailDescriptionTextArea;
    @FXML
    private TextField cultureUrlTextField;
    @FXML
    private WebView trailMapView;
    @FXML
    private Label emptyFieldLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField latitudeTextField;
    @FXML
    private TextField longitudeTextField;
    @FXML
    private Label latitudeLabel;
    @FXML
    private Label longitudeLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private Label invalidNumberLabel;

    /**
     * Initialises the screen with components for user to input data
     * Prefills the boxes if user is updating an existing trail
     * Else leaves boxes blank
     */
    @FXML
    private void initialize() {
        setupFormFields();
        setupEventHandlers();
        initMap();
    }

    /**
     * Sets up all form fields and their initial values
     */
    private void setupFormFields() {
        // Setup combo boxes
        difficultyComboBox.getItems().addAll(List.of("Easiest", "Easy", "Intermediate", "Advanced", "Expert"));
        trailTypeComboBox.getItems().addAll(List.of("One way", "Loop", "Return"));

        // Setup labels
        emptyFieldLabel.setText("");
        invalidNumberLabel.setVisible(false);

        // Prefill fields if editing existing trail
        if (trail != null) {
            initializeTextFields();
            updateLatLonFields(trail.getLat(), trail.getLon());
        }
    }

    /**
     * Sets up event handlers for form controls
     */
    private void setupEventHandlers() {
        // Text field listeners for coordinate updates
        latitudeTextField.textProperty().addListener((obs, oldVal, newVal) -> updateMarkerFromFields());
        longitudeTextField.textProperty().addListener((obs, oldVal, newVal) -> updateMarkerFromFields());

        // Button actions
        saveButton.setOnAction(e -> onSaveButtonClicked());
        backButton.setOnAction(e -> onBackButtonClicked());
    }

    /**
     * Initialises the WebView and sets up the map with proper initialization flow
     */
    private void initMap() {
        javaScriptBridge = new JavaScriptBridge(this, searchService);
        webEngine = trailMapView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        // Setup console logging
        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceID) -> System.out
                .printf("Map WebView console log line: %d, message: %s%n", lineNumber, message));

        // Load the map and setup the initialization callback
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                setupJavaScriptBridge();
                initializeMapView();
            }
        });

        webEngine.load(Controller.class.getResource("/html/map.html").toExternalForm());
    }

    /**
     * Sets up the JavaScript bridge for communication between Java and JavaScript
     */
    private void setupJavaScriptBridge() {
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("javaScriptBridge", javaScriptBridge);
        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
    }

    /**
     * Initializes the map view with appropriate coordinates and enables interaction
     */
    private void initializeMapView() {
        if (trail != null) {
            // Editing existing trail - show current location and enable clicking for
            // updates
            javaScriptConnector.call("initMap", trail.getLat(), trail.getLon());
            addLocation();
        } else {
            // Creating new trail - start with default location
            javaScriptConnector.call("initMap", -44.0, 171.0); // New Zealand default
        }

        // Enable clicking for both new and existing trails
        javaScriptConnector.call("enableClick");
    }

    /**
     * adds a location marker for the coordinates of the selected trail
     */
    @FXML
    private void addLocation() {
        Gson gson = new Gson();
        String trailJson = gson.toJson(trail); // convert Trail object to JSON string
        javaScriptConnector.call("addMarker", trail.getLat(), trail.getLon(), trailJson);
    }

    /**
     * Updates the map marker position when user enters coordinates in text fields
     */
    private void updateMarkerFromFields() {
        if (javaScriptConnector == null) {
            return; // Map not ready yet
        }

        try {
            String latText = latitudeTextField.getText().trim();
            String lonText = longitudeTextField.getText().trim();

            if (latText.isEmpty() || lonText.isEmpty()) {
                return; // Don't update if fields are empty
            }

            double lat = Double.parseDouble(latText);
            double lon = Double.parseDouble(lonText);

            // Validate coordinate ranges
            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                showCoordinateError("Coordinates out of valid range (Lat: -90 to 90, Lon: -180 to 180)");
                return;
            }

            javaScriptConnector.call("addMarker", lat, lon);
            invalidNumberLabel.setVisible(false);

        } catch (NumberFormatException e) {
            showCoordinateError("Please enter valid numbers for latitude and longitude");
        }
    }

    /**
     * Shows coordinate validation error message
     */
    private void showCoordinateError(String message) {
        invalidNumberLabel.setText(message);
        invalidNumberLabel.setTextFill(Color.RED);
        invalidNumberLabel.setVisible(true);
    }

    /**
     * Updates the latitude and longitude fields when the user interacts with the
     * map
     * Also updates the region information
     *
     * @param lat latitude value from the map
     * @param lon longitude value from the map
     */
    public void updateLatLonFields(double lat, double lon) {
        latitudeTextField.setText(String.format("%.6f", lat));
        longitudeTextField.setText(String.format("%.6f", lon));
        invalidNumberLabel.setVisible(false);

        // Update region information
        try {
            String region = regionFinder.findRegionForPoint(lat, lon);
            regionLabel.setText(region != null ? region : "Unknown Region");
        } catch (Exception e) {
            regionLabel.setText("Region lookup failed");
        }
    }

    /**
     * Updates region based on lat/lon fields when user enters
     */
    @FXML
    private void onCoordsChanged() {
        double lat = Double.parseDouble(latitudeTextField.getText());
        double lon = Double.parseDouble(longitudeTextField.getText());
        String region = regionFinder.findRegionForPoint(lat, lon);
        regionLabel.setText(region);
    }

    @FXML
    private void onSaveButtonClicked() {
        if (userInputValidation()) {
            sqlBasedTrailRepo.upsert(getUpdatedTrail());
            super.getNavigator().launchScreen(
                    new ViewTrailController(super.getNavigator(), getUpdatedTrail(), searchService),
                    lastController.getNavigator().getLastController());
        } else {
            emptyFieldLabel.setText("Please make sure all required fields are filled!");
            emptyFieldLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void onBackButtonClicked() {
        super.getNavigator().launchScreen(lastController, lastController.getNavigator().getLastController());
    }

    /**
     * Prefills boxes with existing data of the trail
     */
    @FXML
    private void initializeTextFields() {
        Trail foundTrail = sqlBasedTrailRepo.findById(trail.getId()).get();
        trailNameTextField.setText(foundTrail.getName());
        difficultyComboBox.setValue(StringManipulator.capitaliseFirstLetter(foundTrail.getDifficulty()));
        trailTypeComboBox.setValue(StringManipulator.capitaliseFirstLetter(foundTrail.getCompletionType()));
        trailDescriptionTextArea.setText(foundTrail.getDescription());
        completionTimeTextField.setText(foundTrail.getCompletionInfo());
        cultureUrlTextField.setText(foundTrail.getCultureUrl());
        translationTextField.setText(foundTrail.getTranslation());
    }

    /**
     * Validates user input
     *
     * @return whether inputs are valid
     */
    private boolean userInputValidation() {
        if (trail == null) {
            if (latitudeTextField.getText().isEmpty() || longitudeTextField.getText().isEmpty()) {
                return false; // user must choose a location by entering coordinates or selecting them on map
            }
        }
        if (trailNameTextField.getText().isEmpty() || difficultyComboBox.getValue() == null ||
                trailTypeComboBox.getValue() == null || completionTimeTextField.getText().isEmpty() ||
                trailDescriptionTextArea.getText().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Returns Trail object of trail to be updated/added to database
     *
     * @return updatedTrail
     */
    private Trail getUpdatedTrail() {
        // Get form values
        String trailName = trailNameTextField.getText();
        String translation = translationTextField.getText();
        String difficulty = difficultyComboBox.getValue();
        String trailType = trailTypeComboBox.getValue();
        String completionTime = completionTimeTextField.getText();
        String trailDescription = trailDescriptionTextArea.getText();
        String cultureUrl = cultureUrlTextField.getText();
        double latitude = Double.parseDouble(latitudeTextField.getText());
        double longitude = Double.parseDouble(longitudeTextField.getText());

        // Set trail-specific properties
        int trailId;
        String region;
        String thumbUrl;
        String webUrl;
        double userWeight;

        if (trail != null) {
            // Updating existing trail
            trailId = trail.getId();
            region = ""; // Keep existing region handling
            thumbUrl = trail.getThumbnailURL();
            webUrl = trail.getWebpageURL();
            userWeight = trail.getUserWeight();
        } else {
            // Creating new trail - temp values that will be recalculated
            trailId = -1;
            region = regionLabel.getText();
            thumbUrl = "";
            webUrl = "";
            userWeight = 0.5;
        }

        Trail newTrail = new Trail(trailId, trailName, translation, region, difficulty, trailType,
                completionTime, trailDescription, thumbUrl, webUrl, cultureUrl,
                userWeight, latitude, longitude);

        // Calculate user weight
        if (trail == null) {
            try {
                MatchmakingService matchmakingService = new MatchmakingService(App.getDatabaseService());
                double calculatedWeight = matchmakingService.getUserWeightFromTrail(newTrail);
                newTrail.setUserWeight(calculatedWeight);
            } catch (Exception e) {
                // Keep the default weight
            }
        }

        List<Trail> updatedTrail = TrailsProcessor.processTrails(List.of(newTrail));
        return updatedTrail.getFirst();
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/modify_trail_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "Modify Trail Screen";
    }
}
