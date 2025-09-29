package seng202.team5.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebEngine;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.services.ImageService;
import seng202.team5.services.TrailService;

import java.util.List;

import java.util.Objects;

/**
 * Controller for the view trail screen
 */
public class ViewTrailController extends Controller {

    private final ImageService imageService;
    private Trail trail;
    private TrailService trailService;
    private SqlBasedTrailRepo sqlBasedTrailRepo;

    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;

    /**
     * Launches the screen with navigator
     *
     * @param navigator     screen navigator
     * @param trail         trail object to be displayed on screen
     * @param sqlBasedTrailRepo sqlBasedTrailRepo
     */
    public ViewTrailController(ScreenNavigator navigator, Trail trail, SqlBasedTrailRepo sqlBasedTrailRepo) {
        super(navigator);
        this.imageService = new ImageService();
        this.trail = trail;
        this.sqlBasedTrailRepo = sqlBasedTrailRepo;
    }

    @FXML
    private Label trailNameLabel;
    @FXML
    private Label translationLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private Label matchLabel;
    @FXML
    private ProgressBar matchBar;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button editInfoButton;
    @FXML
    private HBox mapContainer;
    @FXML
    private ImageView trailThumbnail;
    @FXML
    private Button backButton;
    @FXML
    private CheckBox nearbyTrailsCheckbox;
    @FXML
    private TextField trailsRadiusTextField;
    @FXML
    private Label easiestColourLabel;
    @FXML
    private Label easyColourLabel;
    @FXML
    private Label intermediateColourLabel;
    @FXML
    private Label advancedColourLabel;
    @FXML
    private Label expertColourLabel;

    private WebView trailMapWebView;

    /**
     * Initialises the view trail screen with data retrieved from database
     */
    @FXML
    private void initialize() {
        setupFormFields();
        setupEventHandlers();
        setupLegend();

        javafx.application.Platform.runLater(this::initMap);
    }

    /**
     * Sets up all form fields and their initial values
     */
    private void setupFormFields() {
        setupTrailData();
        setupMatchInfo();
        setupTrailRadiusFields();
    }

    /**
     * Sets up data related to the trail
     */
    private void setupTrailData() {
        trailService = new TrailService();
        trailNameLabel.setText(trail.getName());
        regionLabel.setText(trail.getRegion());
        Image trailImage = imageService.loadTrailImage(trail.getThumbnailURL());
        trailThumbnail.setImage(trailImage);
        descriptionLabel.setText(trail.getDescription());
        if (!trail.getTranslation().isEmpty()) {
            translationLabel.setText(trail.getTranslation());
            translationLabel.setVisible(true);
        } else {
            translationLabel.setVisible(false);
        }
    }

    /**
     * Sets up data related to the matchmaking calculations
     */
    private void setupMatchInfo() {
        double weight = trail.getUserWeight();
        matchBar.setProgress(weight);
        int matchPercent = (int) Math.round(weight * 100);
        matchLabel.setText(matchPercent + "% match");
    }

    private void setupTrailRadiusFields() {
        // Allow only digits in the text field
        trailsRadiusTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // only digits allowed
                return change;
            }
            return null; // reject the change
        }));

        // Initially disable the text field if checkbox is not selected
        trailsRadiusTextField.setDisable(!nearbyTrailsCheckbox.isSelected());
    }

    /**
     * Sets up event handlers for form controls
     */
    private void setupEventHandlers() {
        backButton.setOnAction(e -> onBackButtonClicked());
        editInfoButton.setOnAction(e -> onEditInfoButtonClicked());

        nearbyTrailsCheckbox.setOnAction(e -> {
            boolean selected = nearbyTrailsCheckbox.isSelected();
            trailsRadiusTextField.setDisable(!selected); // disable if unchecked
            if (selected) {
                trailsRadiusTextField.setText("20");
                updateNearbyTrails(Integer.parseInt(trailsRadiusTextField.getText()));
            } else {
                // reset to just the current trail
                addLocation();
                removeTrailsFromMap();
                trailsRadiusTextField.clear();
            }
        });

        trailsRadiusTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (nearbyTrailsCheckbox.isSelected()) {
                int radius;
                if (newValue == null || newValue.trim().isEmpty()) {
                    radius = 0; // when no value has been input
                } else {
                    radius = Integer.parseInt(newValue);
                }
                updateNearbyTrails(radius);
            }
        });
    }

    /**
     * Sets up the legend for the map marker colours
     */
    private void setupLegend() {
        easiestColourLabel.setBackground(Background.fill(Paint.valueOf("008000")));
        easyColourLabel.setBackground(Background.fill(Paint.valueOf("8de45f")));
        intermediateColourLabel.setBackground(Background.fill(Paint.valueOf("ffff00")));
        advancedColourLabel.setBackground(Background.fill(Paint.valueOf("ffa500")));
        expertColourLabel.setBackground(Background.fill(Paint.valueOf("ff0000")));
    }

    private void updateNearbyTrails(int radius) {
        List<Trail> nearby = trailService.getNearbyTrails(trail, radius, sqlBasedTrailRepo.getAllTrails());
        displayTrailsOnMap(nearby);
    }

    /**
     * Initialises the WebView loading in the appropriate html and initialising
     * important communicator
     * objects between Java and Javascript
     */
    private void initMap() {
        javaScriptBridge = new JavaScriptBridge(this, sqlBasedTrailRepo);
            mapContainer.getChildren().clear();
            trailMapWebView = new WebView();
            trailMapWebView.setPrefHeight(-1);
            trailMapWebView.setPrefWidth(-1);
            HBox.setHgrow(trailMapWebView, Priority.ALWAYS);
            mapContainer.getChildren().add(trailMapWebView);
//        } else {
//            mapContainer.getChildren().clear();
//            trailMapWebView = new WebView();
//            trailMapWebView.setPrefHeight(-1);
//            trailMapWebView.setPrefWidth(-1);
//            HBox.setHgrow(trailMapWebView, Priority.ALWAYS);
//            mapContainer.getChildren().add(trailMapWebView);
//        }

        webEngine = trailMapWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceID) ->
                System.out.printf(String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
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

    private void initializeMapView() {
        javaScriptConnector.call("initMap", trail.getLat(), trail.getLon());
        addLocation();
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
     * displays the nearby trails as circle markers
     *
     * @param trails the set of nearby trails
     */
    private void displayTrailsOnMap(List<Trail> trails) {
        if (javaScriptConnector != null) {
            Gson gson = new GsonBuilder().create();
            String trailsJson = gson.toJson(trails);
            javaScriptConnector.call("displayTrails", trailsJson);
        }
    }

    /**
     * removes the markers on the map
     */
    private void removeTrailsFromMap() {
        if (javaScriptConnector != null) {
            javaScriptConnector.call("clearMarkers");
        }
    }

    /**
     * opens a page for a given trail
     *
     * @param trail the trail whose page will be opened
     */
    public void openTrailInfo(Trail trail) {
        Controller lastController = super.getNavigator().getLastController();
        super.getNavigator().launchScreen(new ViewTrailController(super.getNavigator(), trail, sqlBasedTrailRepo),
                lastController); // pass dashboard as last controller (or this??)
    }

    @FXML
    private void onBackButtonClicked() {
        String lastScreenName = super.getNavigator().getLastController().getTitle();
        Controller lastController;
        if (Objects.equals(lastScreenName, "Dashboard")) {
            lastController = new DashboardController(super.getNavigator());
        } else {
            lastController = new TrailsController(super.getNavigator());
        }
        super.getNavigator().launchScreen(lastController, null);
    }

    @FXML
    private void onEditInfoButtonClicked() {
        super.getNavigator().launchScreen(new ModifyTrailController(super.getNavigator(), trail,
                this, sqlBasedTrailRepo), null);
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/view_trail_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "View Trail Screen";
    }

    /**
     * Handles the case where the View Trail Screen fails to load
     * by displaying an alert to the user
     *
     * @param e the exception that occurred while loading the trail screen
     */
    @Override
    public void onLoadFailed(Exception e) {
        showAlert(Alert.AlertType.ERROR, "Trail Card Failed To Load",
                "Loading selected trail failed, please close the application and try again.");
    }
}
