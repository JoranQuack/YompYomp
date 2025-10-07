package seng202.team5.gui;

import com.google.gson.Gson;
import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import seng202.team5.App;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.models.User;
import seng202.team5.services.MatchmakingService;
import seng202.team5.services.RegionFinder;
import seng202.team5.utils.StringManipulator;
import seng202.team5.utils.TrailsProcessor;

import java.util.List;

/**
 * Controller for the modify trail screen
 */
public class ModifyTrailController extends Controller {

    private final Trail trail;
    private final SqlBasedTrailRepo sqlBasedTrailRepo;
    private final RegionFinder regionFinder;

    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;

    /**
     * Launches the screen with the navigator
     *
     * @param navigator         screen navigator
     * @param trail             the selected trail
     * @param sqlBasedTrailRepo the trail repository
     */
    public ModifyTrailController(ScreenNavigator navigator, Trail trail,
            SqlBasedTrailRepo sqlBasedTrailRepo) {
        super(navigator);
        this.trail = trail;
        this.regionFinder = new RegionFinder();
        this.sqlBasedTrailRepo = sqlBasedTrailRepo;
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
    private HBox mapContainer;
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
    private TextField regionTextField;
    @FXML
    private Label invalidNumberLabel;
    private WebView trailMapWebView;

    /**
     * Initialises the screen with components for user to input data
     * Prefills the boxes if user is updating an existing trail
     * Else leaves boxes blank
     */
    @FXML
    private void initialize() {
        setupFormFields();
        setupEventHandlers();
        javafx.application.Platform.runLater(this::initMap);
    }

    /**
     * Sets up all form fields and their initial values
     */
    private void setupFormFields() {
        difficultyComboBox.getItems().addAll(List.of("Easiest", "Easy", "Intermediate", "Advanced", "Expert"));
        trailTypeComboBox.getItems().addAll(List.of("One way", "Loop", "Return"));

        emptyFieldLabel.setText("");
        invalidNumberLabel.setVisible(false);

        if (trail != null) {
            initializeTextFields();
            updateLatLonFields(trail.getLat(), trail.getLon());
        }

        // Allow only digits in the text field
        latitudeTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            // Allow optional '-' at start, digits, optional decimal, up to 6 decimals
            if (newText.matches("-?\\d*(\\.\\d{0,6})?")) {
                return change;
            }
            return null; // reject invalid input
        }));

        longitudeTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            // Allow optional '-' at start, digits, optional decimal, up to 6 decimals
            if (newText.matches("-?\\d*(\\.\\d{0,6})?")) {
                return change;
            }
            return null; // reject invalid input
        }));
    }

    /**
     * Sets up event handlers for form controls
     */
    private void setupEventHandlers() {
        latitudeTextField.textProperty().addListener((obs, oldVal, newVal) -> updateMarkerFromFields());
        longitudeTextField.textProperty().addListener((obs, oldVal, newVal) -> updateMarkerFromFields());

        saveButton.setOnAction(e -> onSaveButtonClicked());
        backButton.setOnAction(e -> onBackButtonClicked());

        // For TextFields
        latitudeTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                latitudeTextField.setStyle("");
                mapContainer.setStyle(""); // clear map border as well
            }
        });
        longitudeTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                longitudeTextField.setStyle("");
                mapContainer.setStyle("");
            }
        });
        trailNameTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                trailNameTextField.setStyle("");
            }
        });
        // For TextArea
        trailDescriptionTextArea.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                trailDescriptionTextArea.setStyle("");
            }
        });
        // For ComboBox
        trailTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                trailTypeComboBox.setStyle("");
            }
        });
    }

    /**
     * Initialises the WebView and sets up the map with proper initialization flow
     */
    private void initMap() {
        javaScriptBridge = new JavaScriptBridge(this, sqlBasedTrailRepo);
        mapContainer.getChildren().clear();
        trailMapWebView = new WebView();
        trailMapWebView.setPrefHeight(-1);
        trailMapWebView.setPrefWidth(-1);
        HBox.setHgrow(trailMapWebView, Priority.ALWAYS);
        mapContainer.getChildren().add(trailMapWebView);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);

        clip.widthProperty().bind(mapContainer.widthProperty());
        clip.heightProperty().bind(mapContainer.heightProperty());

        trailMapWebView.setClip(clip);

        webEngine = trailMapWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceID) -> System.out
                .printf("Map WebView console log line: %d, message: %s%n", lineNumber, message));

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
            // Editing existing trail
            javaScriptConnector.call("initMap", trail.getLat(), trail.getLon());
            addLocation();
        } else {
            // Creating new trail
            javaScriptConnector.call("initMap", -44.0, 171.0); // New Zealand default
        }

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

        try {
            String region = regionFinder.findRegionForPoint(lat, lon);
            regionTextField.setText(region != null ? region : "Unknown Region");
        } catch (Exception e) {
            regionTextField.setText("Region lookup failed");
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
        regionTextField.setText(region);
    }

    @FXML
    private void onSaveButtonClicked() {
        if (userInputValidation()) {
            Trail updatedTrail = getUpdatedTrail();
            sqlBasedTrailRepo.upsert(updatedTrail);
            super.getNavigator().launchScreen(
                    new ViewTrailController(super.getNavigator(), updatedTrail, sqlBasedTrailRepo));
        } else {
            // Check if the error is specifically due to duplicate trail name
            if (!trailNameTextField.getText().isEmpty()) {
                String inputTrailName = trailNameTextField.getText().trim();
                Integer excludeId = (trail != null) ? trail.getId() : null;
                boolean nameExists = sqlBasedTrailRepo.existsByName(inputTrailName, excludeId);

                if (nameExists) {
                    emptyFieldLabel.setText("A trail with this name already exists! Please choose a different name.");
                    return;
                }
            }

            emptyFieldLabel.setText("Please make sure all required fields are filled!");
        }
    }

    @FXML
    private void onBackButtonClicked() {
        super.getNavigator().goBack();
    }

    @FXML
    private void onDeleteTrailClicked() {
        if (trail != null) {
            boolean confirmed = showAlert(
                    "Confirm Deletion",
                    "Are you sure you want to delete this trail?",
                    "This action cannot be undone.",
                    "Delete",
                    "Cancel",
                    "bg-red");

            if (confirmed) {
                sqlBasedTrailRepo.deleteById(trail.getId());
                super.getNavigator().launchScreen(new TrailsController(super.getNavigator(), sqlBasedTrailRepo));
            }
        }
    }

    /**
     * Prefills boxes with existing data of the trail
     */
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
        boolean isValid = true;

        if (latitudeTextField.getText().isEmpty()) {
            latitudeTextField.setStyle("-fx-border-color: red;");
            mapContainer.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            latitudeTextField.setStyle("");
            mapContainer.setStyle("");
        }
        if (longitudeTextField.getText().isEmpty()) {
            longitudeTextField.setStyle("-fx-border-color: red;");
            mapContainer.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            longitudeTextField.setStyle("");
            mapContainer.setStyle("");
        }
        if (trailNameTextField.getText().isEmpty()) {
            trailNameTextField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            trailNameTextField.setStyle("");
        }
        if (trailDescriptionTextArea.getText().isEmpty()) {
            trailDescriptionTextArea.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            trailDescriptionTextArea.setStyle("");
        }
        if (trailTypeComboBox.getValue() == null) {
            trailTypeComboBox.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            trailTypeComboBox.setStyle("");
        }

        // Check if trail name already exists (case and whitespace insensitive)
        if (!trailNameTextField.getText().isEmpty()) {
            String inputTrailName = trailNameTextField.getText().trim();
            Integer excludeId = (trail != null) ? trail.getId() : null;
            boolean nameExists = sqlBasedTrailRepo.existsByName(inputTrailName, excludeId);

            if (nameExists) {
                trailNameTextField.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        }

        return isValid;
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
        String difficulty = difficultyComboBox.getValue().toLowerCase();
        String trailType = trailTypeComboBox.getValue().toLowerCase();
        String completionTime = completionTimeTextField.getText().toLowerCase();
        String trailDescription = trailDescriptionTextArea.getText();
        String cultureUrl = cultureUrlTextField.getText();
        double latitude = Double.parseDouble(latitudeTextField.getText());
        double longitude = Double.parseDouble(longitudeTextField.getText());

        int trailId;
        String region;
        String thumbUrl;
        String webUrl;
        double userWeight;

        if (trail != null) {
            // Updating existing trail
            trailId = trail.getId();
            region = "";
            thumbUrl = trail.getThumbnailURL();
            webUrl = trail.getWebpageURL();
            userWeight = trail.getUserWeight();
        } else {
            // Creating new trail - temp values that will be recalculated
            trailId = sqlBasedTrailRepo.getNewTrailId();
            region = regionTextField.getText();
            thumbUrl = "";
            webUrl = "";
            userWeight = 0.5;
        }

        Trail newTrail = new Trail(trailId, trailName, translation, region, difficulty, trailType,
                completionTime, trailDescription, thumbUrl, webUrl, cultureUrl,
                userWeight, latitude, longitude);

        // Calculate user weight
        try {
            User user = super.getUserService().getUser();
            MatchmakingService matchmakingService = new MatchmakingService(App.getDatabaseService());
            matchmakingService.setUserPreferences(user);
            double calculatedWeight = matchmakingService.getUserWeightFromTrail(newTrail);
            newTrail.setUserWeight(calculatedWeight);
        } catch (Exception e) {
            // Keep the default weight
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

    @Override
    protected boolean shouldShowNavbar() {
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return 1; // Trails section
    }
}
