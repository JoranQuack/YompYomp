package seng202.team5.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.gui.components.LegendLabelComponent;
import seng202.team5.gui.components.TrailCardComponent;
import seng202.team5.models.Trail;
import seng202.team5.services.TrailService;

import java.util.List;

/**
 * Controller for the view trail screen
 */
public class ViewTrailController extends Controller {
    private final Trail trail;
    private TrailService trailService;
    private final SqlBasedTrailRepo sqlBasedTrailRepo;

    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;

    /**
     * Launches the screen with navigator
     *
     * @param navigator         screen navigator
     * @param trail             trail object to be displayed on screen
     * @param sqlBasedTrailRepo sqlBasedTrailRepo
     */
    public ViewTrailController(ScreenNavigator navigator, Trail trail, SqlBasedTrailRepo sqlBasedTrailRepo) {
        super(navigator);
        this.trail = trail;
        this.sqlBasedTrailRepo = sqlBasedTrailRepo;
    }

    @FXML
    private Label translationLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button editInfoButton;
    @FXML
    private HBox mapContainer;
    @FXML
    private Button backButton;
    @FXML
    private CheckBox nearbyTrailsCheckbox;
    @FXML
    private TextField trailsRadiusTextField;
    @FXML
    private HBox trailCardHBox;
    @FXML
    private HBox legendContainer;
    @FXML
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
        trailService = new TrailService();
        initTrailCard();
        descriptionLabel.setText(trail.getDescription());
        if (!trail.getTranslation().isEmpty()) {
            translationLabel.setText(trail.getTranslation());
            translationLabel.setVisible(true);
        } else {
            translationLabel.setVisible(false);
        }
        setupTrailRadiusFields();
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
        nearbyTrailsCheckbox.setSelected(true);
        trailsRadiusTextField.setText("20");
    }

    /**
     * Sets up event handlers for form controls
     */
    private void setupEventHandlers() {
        backButton.setOnAction(e -> onBackButtonClicked());
        editInfoButton.setOnAction(e -> onEditInfoButtonClicked());

        nearbyTrailsCheckbox.setOnAction(e -> refreshNearbyTrails());
        trailsRadiusTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (nearbyTrailsCheckbox.isSelected()) {
                refreshNearbyTrails();
            }
        });

        // Checkbox toggled
        nearbyTrailsCheckbox.setOnAction(e -> {
            if (nearbyTrailsCheckbox.isSelected()) {
                trailsRadiusTextField.setText("20"); // reset to 20 on reselect
            }
            refreshNearbyTrails();
        });

        // Text field value changed
        trailsRadiusTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (nearbyTrailsCheckbox.isSelected()) {
                refreshNearbyTrails();
            }
        });
    }

    /**
     * Sets up the legend for the map marker colours
     */
    private void setupLegend() {
        LegendLabelComponent easiestLegend = new LegendLabelComponent("008000", "Easiest");
        LegendLabelComponent easyLegend = new LegendLabelComponent("8de45f", "Easy");
        LegendLabelComponent intermediateLegend = new LegendLabelComponent("ffff00", "Intermediate");
        LegendLabelComponent advancedLegend = new LegendLabelComponent("ffa500", "Advanced Colour");
        LegendLabelComponent expertLegend = new LegendLabelComponent("ff0000", "Expert");

        legendContainer.getChildren().add(easiestLegend);
        legendContainer.getChildren().add(easyLegend);
        legendContainer.getChildren().add(intermediateLegend);
        legendContainer.getChildren().add(advancedLegend);
        legendContainer.getChildren().add(expertLegend);
    }

    /**
     * Initialises the trail card at the top of the screen
     */
    private void initTrailCard() {
        TrailCardComponent trailCard = new TrailCardComponent(super.getUserService().isGuest(), true);
        trailCard.setData(trail);
        trailCardHBox.getChildren().add(trailCard);
    }

    /**
     * Calls the function to display nearby trails on the map within a given radius
     *
     * @param radius the radius in km of nearby trails to be viewed
     */
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

        webEngine = trailMapWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceID) -> System.out
                .printf(String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

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
        refreshNearbyTrails();
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
     * Handles showing/hiding nearby trails depending on checbox state and radius.
     */
    private void refreshNearbyTrails() {
        if (nearbyTrailsCheckbox.isSelected()) {
            int radius;
            String text = trailsRadiusTextField.getText();
            if (text == null || text.trim().isEmpty()) {
                radius = 0;
            } else {
                radius = Integer.parseInt(text);
            }
            trailsRadiusTextField.setDisable(false);
            updateNearbyTrails(radius);
        } else {
            addLocation();
            removeTrailsFromMap();
            trailsRadiusTextField.clear();
            trailsRadiusTextField.setDisable(true);
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
        super.getNavigator().launchScreen(new ViewTrailController(super.getNavigator(), trail, sqlBasedTrailRepo));
    }

    @FXML
    private void onBackButtonClicked() {
        super.getNavigator().goBack();
    }

    @FXML
    private void onEditInfoButtonClicked() {
        super.getNavigator().launchScreen(new ModifyTrailController(super.getNavigator(), trail,
                sqlBasedTrailRepo));
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/view_trail_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "View Trail Screen";
    }

    @Override
    protected boolean shouldShowNavbar() {
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return 1; // Trails section
    }

    /**
     * Handles the case where the View Trail Screen fails to load
     * by displaying an alert to the user
     *
     * @param e the exception that occurred while loading the trail screen
     */
    @Override
    public void onLoadFailed(Exception e) {
        showAlert("Trail Card Failed To Load",
                "Loading selected trail failed, please close the application and try again.",
                "OK", "Cancel", null, null);
    }
}
