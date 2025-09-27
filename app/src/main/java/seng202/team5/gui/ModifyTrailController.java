package seng202.team5.gui;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.utils.StringManipulator;
import seng202.team5.utils.TrailsProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the modify trail screen
 */
public class ModifyTrailController extends Controller {

    private Trail trail;
    private Controller lastController;
    private DatabaseService databaseService;
    private SqlBasedTrailRepo sqlBasedTrailRepo;

    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;
//    private Geolocator geolocator;

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
     * @param lastController controller of last screen user interacted with
     */
    public ModifyTrailController(ScreenNavigator navigator, Trail trail, Controller lastController) {
        super(navigator);
        this.trail = trail;
        this.lastController = lastController;
        this.databaseService = new DatabaseService();
        this.sqlBasedTrailRepo = new SqlBasedTrailRepo(databaseService);
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
    private Label regionLabel;
    @FXML
    private ComboBox<String> regionComboBox;
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
    private Label invalidNumberLabel;

    /**
     * Initialises the screen with components for user to input data
     * Prefills the boxes if user is updating an existing trail
     * Else leaves boxes blank
     */
    @FXML
    private void initialize() {
        if (trail != null) {
            initializeTextFields();
            regionLabel.setVisible(false);
            regionComboBox.setVisible(false);
            latitudeTextField.setVisible(false);
            longitudeTextField.setVisible(false);
            latitudeLabel.setVisible(false);
            longitudeLabel.setVisible(false); // user can't change location of pre-existing trail
        } else {
            regionLabel.setVisible(true);
            regionComboBox.setVisible(true);
            latitudeTextField.textProperty().addListener((obs, oldVal, newVal) -> updateMarkerFromFields());
            longitudeTextField.textProperty().addListener((obs, oldVal, newVal) -> updateMarkerFromFields());
        }
        List<String> regionList = new ArrayList<>(List.of("Northland", "Auckland",
                "Waikato", "Bay of Plenty", "Gisborne", "Hawke's Bay", "Taranaki",
                "Manawatu-Whanganui", "Tasman", "Wellington", "Nelson", "Marlborough", "West Coast",
                "Canterbury", "Otago", "Southland"));
        regionComboBox.getItems().addAll(regionList);
        difficultyComboBox.getItems().addAll(List.of("Easy", "Intermediate", "Advanced"));
        trailTypeComboBox.getItems().addAll(List.of("One way", "Loop", "Return"));
        emptyFieldLabel.setText("");
        invalidNumberLabel.setVisible(false);
        saveButton.setOnAction(e -> onSaveButtonClicked());
        backButton.setOnAction(e -> onBackButtonClicked());

        // Map methods below
        javaScriptBridge = new JavaScriptBridge(this);
        initMap();
    }

    /**
     * Initialises the WebView loading in the appropriate html and initialising important communicator
     * objects between Java and Javascript
     */
    private void initMap() {
        webEngine = trailMapView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(Controller.class.getResource("/html/map.html").toExternalForm());
        //Forwards console.log() output from any javascript to info log
        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceID) ->
                System.out.printf(String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        // set our bridge object
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        // get a reference to the js object that has a reference to the js methods we need to use in java
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                        // call the javascript function to initialise the map
                        if (trail != null) {
                            javaScriptConnector.call("initMap", trail.getLat(), trail.getLon());
                            addLocation();
                        } else {
                            javaScriptConnector.call("initMap", -44.0, 171.0); // arbitrary location for now
                            javaScriptConnector.call("enableClick");
                        }
                    }
                });
    }

    /**
     * adds a location marker for the coordinates of the selected trail
     */
    @FXML
    private void addLocation() {
        javaScriptConnector.call("addMarker", trail.getLat(), trail.getLon());
    }

    /**
     * Updates the position of the location marker when user enters
     * longitude and latitude values for a new trail
     */
    private void updateMarkerFromFields() {
        try {
            double lat = Double.parseDouble(latitudeTextField.getText());
            double lon = Double.parseDouble(longitudeTextField.getText());
            if (javaScriptConnector != null) {
                javaScriptConnector.call("addMarker", lat, lon);
            }
            invalidNumberLabel.setVisible(false);
        } catch (NumberFormatException e) {
            invalidNumberLabel.setText("Please enter a valid digit for latitude and longitude");
            invalidNumberLabel.setTextFill(Color.RED);
            invalidNumberLabel.setVisible(true); // only show on invalid input
        }
    }

    /**
     * updates the latitude and longitude fields when the user interacts with the map
     * @param lat latitude value from the map
     * @param lon longitude value from the map
     */
    public void updateLatLonFields(double lat, double lon) {
        latitudeTextField.setText(String.format("%.2f", lat));
        longitudeTextField.setText(String.format("%.2f", lon));
        invalidNumberLabel.setVisible(false); // hide error when valid coords come from map
    }

    @FXML
    private void onSaveButtonClicked() {
        if (userInputValidation()) {
            sqlBasedTrailRepo.upsert(getUpdatedTrail());
            super.getNavigator().launchScreen(new ViewTrailController(super.getNavigator(), getUpdatedTrail()),
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
     * @return updatedTrail
     */
    private Trail getUpdatedTrail() {
        int trailId;
        String region;
        String thumbUrl;
        String webUrl;
        double userWeight;
        if (trail != null) {
            trailId = trail.getId();
            region = "";
            thumbUrl = trail.getThumbnailURL();
            webUrl = trail.getWebpageURL();
            userWeight = trail.getUserWeight();
        } else {
            trailId = -1;
            region = regionComboBox.getValue();
            thumbUrl = "";
            webUrl = "";
            userWeight = 0.5;
            // TODO: implement calculation for new trail
        }
        String trailName = trailNameTextField.getText();
        String translation = translationTextField.getText();
        String difficulty = difficultyComboBox.getValue();
        String trailType = trailTypeComboBox.getValue();
        String completionTime = completionTimeTextField.getText();
        String trailDescription = trailDescriptionTextArea.getText();
        String cultureUrl = cultureUrlTextField.getText();
        Double latitude =  Double.parseDouble(latitudeTextField.getText());
        Double longitude =  Double.parseDouble(longitudeTextField.getText());
        List<Trail> updatedTrail = TrailsProcessor.processTrails(List.of(new Trail(trailId, trailName, translation,
                region, difficulty, trailType, completionTime, trailDescription, thumbUrl, webUrl, cultureUrl, userWeight, latitude, longitude)));
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
