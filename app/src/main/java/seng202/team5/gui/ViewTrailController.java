package seng202.team5.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.gui.components.TrailCardComponent;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import seng202.team5.services.LogService;
import seng202.team5.services.SearchService;
import seng202.team5.services.TrailService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Controller for the view trail screen
 */
public class ViewTrailController extends Controller {
    private Trail trail;
    private TrailService trailService;
    private SearchService searchService;
    private SqlBasedTrailLogRepo trailLogRepo;
    private LogService logService;

    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;

    /**
     * Launches the screen with navigator
     *
     * @param navigator     screen navigator
     * @param trail         trail object to be displayed on screen
     * @param searchService searchService
     */
    public ViewTrailController(ScreenNavigator navigator, Trail trail, SearchService searchService) {
        super(navigator);
        this.trail = trail;
        this.searchService = searchService;
        this.trailLogRepo = new SqlBasedTrailLogRepo(new DatabaseService());
        this.logService = new LogService(new DatabaseService());
    }

    @FXML
    private Label translationLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button editInfoButton;
    @FXML
    private WebView trailMapView;
    @FXML
    private Button backButton;
    @FXML
    private CheckBox nearbyTrailsCheckbox;
    @FXML
    private TextField trailsRadiusTextField;
    @FXML
    private HBox trailCardHBox;

    /**
     * Initialises the view trail screen with data retrieved from database
     */
    @FXML
    private void initialize() {
        trailService = new TrailService();

        initTrailCard();

        descriptionLabel.setText(trail.getDescription());
        backButton.setOnAction(e -> onBackButtonClicked());
        editInfoButton.setOnAction(e -> onEditInfoButtonClicked());

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

        nearbyTrailsCheckbox.setOnAction(e -> {
            boolean selected = nearbyTrailsCheckbox.isSelected();
            trailsRadiusTextField.setDisable(!selected); // disable if unchecked
            if (selected) {
                trailsRadiusTextField.setText("20");
                List<Trail> nearby = trailService.getNearbyTrails(trail,
                        Integer.parseInt(trailsRadiusTextField.getText()), searchService.getAllTrails());
                displayTrailsOnMap(nearby);
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
                List<Trail> nearby = trailService.getNearbyTrails(trail, radius, searchService.getAllTrails());
                displayTrailsOnMap(nearby);
            }
        });

        if (!trail.getTranslation().isEmpty()) {
            translationLabel.setText(trail.getTranslation());
            translationLabel.setVisible(true);
        } else {
            translationLabel.setVisible(false);
        }

        javaScriptBridge = new JavaScriptBridge(this, searchService);
        initMap();
    }

    /**
     * Initialises the trail card at the top of the screen
     */
    private void initTrailCard() {
        TrailCardComponent trailCard = new TrailCardComponent(super.getUserService().isGuest(), false);
        trailCard.setData(trail, null);
        trailCard.setTrail(trail);

        trailCard.setOnBookmarkClickedHandler(clickedTrail -> {
            TrailLog newLog = new TrailLog(
                trailLogRepo.getNewTrailLogId(),
                clickedTrail.getId(),
                LocalDate.now(),
                null, null, null, null, null, null
            );

            LogTrailController logController = new LogTrailController(
                    super.getNavigator(),
                    clickedTrail,
                    newLog
            );
            super.getNavigator().launchScreen(logController);
        });

        trailCard.setOnBookmarkFillClickedHandler(clickedTrail -> {
            // TODO confirmation dialog
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Log");
            confirm.setHeaderText("Are you sure you want to delete this log?");
            confirm.setContentText("This action cannot be undone.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    logService.deleteLog(clickedTrail.getId());
                    trailCard.setBookmarked(false);
                }
            });

            List<TrailLog> logs = logService.getAllLogs();
            logs.stream()
                .filter(log -> log.getTrailId() == clickedTrail.getId())
                .findFirst()
                .ifPresent(log -> {
                    logService.deleteLog(log.getId());
                    trailCard.setBookmarked(false);
                });
        });

        trailCardHBox.getChildren().add(trailCard);
    }

    /**
     * Initialises the WebView loading in the appropriate html and initialising
     * important communicator
     * objects between Java and Javascript
     */
    private void initMap() {
        webEngine = trailMapView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(Controller.class.getResource("/html/map.html").toExternalForm());
        // Forwards console.log() output from any javascript to info log
        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceID) -> System.out
                .printf(String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        // set our bridge object
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        // get a reference to the js object that has a reference to the js methods we
                        // need to use in java
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                        // call the javascript function to initialise the map
                        javaScriptConnector.call("initMap", trail.getLat(), trail.getLon());

                        addLocation();
                    }
                });
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
        super.getNavigator().launchScreen(new ViewTrailController(super.getNavigator(), trail, searchService)); // pass dashboard as last controller (or this??)
    }

    @FXML
    private void onBackButtonClicked() {
        super.getNavigator().goBack();
    }

    @FXML
    private void onEditInfoButtonClicked() {
        super.getNavigator().launchScreen(new ModifyTrailController(super.getNavigator(), trail,
                searchService));
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
        showAlert(Alert.AlertType.ERROR, "Trail Card Failed To Load",
                "Loading selected trail failed, please close the application and try again.");
    }
}
