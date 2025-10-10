package seng202.team5.gui;

import com.google.gson.Gson;
import com.sun.javafx.webkit.WebConsoleListener;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import seng202.team5.App;
import seng202.team5.gui.components.LegendLabelComponent;
import seng202.team5.gui.components.TrailCardComponent;
import seng202.team5.gui.components.WeatherComponent;
import seng202.team5.models.Trail;
import seng202.team5.services.LogService;
import seng202.team5.services.RegionFinder;
import seng202.team5.utils.CompletionTimeParser;
import seng202.team5.services.TrailService;
import seng202.team5.utils.StringManipulator;
import seng202.team5.utils.TrailsProcessor;
import seng202.team5.services.WeatherService;
import seng202.team5.models.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Controller for the view trail screen
 */
public class ViewTrailController extends Controller {
    private final Trail trail;
    private WeatherService weatherService;
    private final TrailService trailService;
    private final LogService logService;

    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
     * @param trail     trail object to be displayed on screen
     */
    public ViewTrailController(ScreenNavigator navigator, Trail trail) {
        super(navigator);
        this.trail = trail;
        this.trailService = new TrailService(App.getTrailRepo());
        this.logService = new LogService(App.getTrailLogRepo(), App.getTrailRepo());
    }

    @FXML
    private Button editInfoButton;
    @FXML
    private HBox mapContainer;
    @FXML
    private CheckBox nearbyTrailsCheckbox;
    @FXML
    private TextField trailsRadiusTextField;
    @FXML
    private HBox trailCardHBox;
    @FXML
    private HBox legendContainer;
    @FXML
    private Hyperlink docHutsLink;
    @FXML
    private Hyperlink remoteHutsLink;
    @FXML
    private Button logTrailButton;
    @FXML
    private HBox weatherContainer;

    /**
     * Initialises the view trail screen with data retrieved from database
     */
    @FXML
    private void initialize() {
        setupFormFields();
        setupEventHandlers();
        setupLegend();
        Platform.runLater(this::initMap);
        setupWeather();
    }

    /**
     * Sets up the weather service and fetches weather data for the trail location
     */
    private void setupWeather() {
        weatherService = new WeatherService("https://api.openweathermap.org/data/2.5/weather",
                "https://api.openweathermap.org/data/2.5/forecast");

        new Thread(() -> {
            Weather weather = weatherService.getWeatherByCoords(trail.getLat(), trail.getLon());
            if (weather != null) {
                Platform.runLater(() -> weatherContainer.getChildren().add(new WeatherComponent(weather)));
            } else {
                Platform.runLater(() -> weatherContainer.getChildren().add(new Label("Weather unavailable")));
            }
        }).start();

        new Thread(() -> {
            List<Weather> forecast = weatherService.getFourDayForecast(trail.getLat(), trail.getLon());
            if (!forecast.isEmpty()) {
                for (Weather day : forecast) {
                    Platform.runLater(() -> weatherContainer.getChildren().add(new WeatherComponent(day)));
                }

            } else {
                Platform.runLater(() -> weatherContainer.getChildren().add(new Label("Forecast unavailable")));
            }
        }).start();
    }

    /**
     * Sets up all form fields and their initial values
     */
    private void setupFormFields() {
        initTrailCard();
        setupTrailRadiusFields();
        setupHutLinks();
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
        editInfoButton.setOnAction(e -> onEditInfoButtonClicked());
        logTrailButton.setOnAction(e -> onLogTrailClicked());
        if (isTrailLogged(trail)) {
            logTrailButton.setText("View Log");
        } else {
            logTrailButton.setText("Log Trail");
        }

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
        LegendLabelComponent easiestLegend = new LegendLabelComponent("Easiest");
        LegendLabelComponent easyLegend = new LegendLabelComponent("Easy");
        LegendLabelComponent intermediateLegend = new LegendLabelComponent("Intermediate");
        LegendLabelComponent advancedLegend = new LegendLabelComponent("Advanced");
        LegendLabelComponent expertLegend = new LegendLabelComponent("Expert");

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
        TrailCardComponent trailCard = new TrailCardComponent(App.getUserService().isGuest(), true, false,
                super.getNavigator());
        trailCard.setData(trail, null);
        trailCardHBox.getChildren().add(trailCard);
    }

    /**
     * Calls the function to display nearby trails on the map within a given radius
     *
     * @param radius the radius in km of nearby trails to be viewed
     */
    private void updateNearbyTrails(int radius) {
        List<Trail> nearby = TrailsProcessor.getNearbyTrails(trail, radius, trailService.getAllTrails());
        displayTrailsOnMap(nearby);
    }

    /**
     * Initialises the WebView loading in the appropriate html and initialising
     * important communicator
     * objects between Java and Javascript
     */
    private void initMap() {
        javaScriptBridge = new JavaScriptBridge(this);
        mapContainer.getChildren().clear();
        WebView trailMapWebView = new WebView();
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
                .printf(String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        setupJavaScriptBridge();
                        initializeMapView();
                    }
                });

        webEngine.load(Objects.requireNonNull(Controller.class.getResource("/html/map.html")).toExternalForm());
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
        String trailCompletionTime = CompletionTimeParser.formatTimeRange(trail.getMinCompletionTimeMinutes(),
                trail.getMaxCompletionTimeMinutes());
        javaScriptConnector.call("addMarker", trail.getLat(), trail.getLon(), trailJson, trailCompletionTime);
    }

    /**
     * displays the nearby trails as circle markers
     *
     * @param trails the set of nearby trails
     */
    private void displayTrailsOnMap(List<Trail> trails) {
        if (javaScriptConnector != null) {
            List<Map<String, Object>> trailsWithTime = new ArrayList<>();

            for (Trail trail : trails) {
                @SuppressWarnings("unchecked")
                Map<String, Object> trailMap = (Map<String, Object>) new Gson().fromJson(new Gson().toJson(trail),
                        Map.class);

                String completionTime = CompletionTimeParser.formatTimeRange(
                        trail.getMinCompletionTimeMinutes(), trail.getMaxCompletionTimeMinutes());
                trailMap.put("completionTime", completionTime);
                trailsWithTime.add(trailMap);
            }
            String trailsJson = new Gson().toJson(trailsWithTime);
            javaScriptConnector.call("displayTrails", trailsJson);
        }
    }

    /**
     * Handles showing/hiding nearby trails depending on checkbox state and radius.
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
     * Opens a page for a given trail
     *
     * @param trail the trail whose page will be opened
     */
    public void openTrailInfo(Trail trail) {
        super.getNavigator().launchScreen(
                new ViewTrailController(super.getNavigator(), trail));
    }

    private void setupHutLinks() {
        RegionFinder regionFinder = new RegionFinder();
        String region = trail.getRegion();
        String regionLower = trail.getRegion().toLowerCase();
        String difficulty = trail.getDifficulty().toLowerCase();

        Integer docRegionId = regionFinder.getDocRegionId(regionLower);
        if (docRegionId != null) {
            docHutsLink.setText("View DOC Huts in " + StringManipulator.capitaliseFirstLetter(region));
            docHutsLink.setOnAction(e -> super.getNavigator().openWebPage(
                    "https://www.doc.govt.nz/parks-and-recreation/places-to-stay/stay-in-a-hut/?region-id="
                            + docRegionId));
            docHutsLink.setVisible(true);
        } else {
            docHutsLink.setText("View DOC Huts");
            docHutsLink.setOnAction(e -> super.getNavigator().openWebPage(
                    "https://www.doc.govt.nz/parks-and-recreation/places-to-stay/stay-in-a-hut/"));
        }

        boolean isRemoteRegion = regionFinder.isRemoteHutRegion(region);
        if (isRemoteRegion && (difficulty.contains("advanced") || difficulty.contains("expert"))) {
            remoteHutsLink.setText("View Remote Huts");
            remoteHutsLink
                    .setOnAction(e -> super.getNavigator().openWebPage("https://www.remotehuts.co.nz/by-map.html"));
            remoteHutsLink.setVisible(true);
        } else {
            remoteHutsLink.setVisible(false);
        }
    }

    private boolean isTrailLogged(Trail trail) {
        return logService.isTrailLogged(trail.getId());
    }

    private void onLogTrailClicked() {
        super.getNavigator().launchScreen(new LogTrailController(super.getNavigator(), trail));
    }

    @FXML
    private void onEditInfoButtonClicked() {
        super.getNavigator().launchScreen(new ModifyTrailController(super.getNavigator(), trail));
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/view_trail_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "View Trail";
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
