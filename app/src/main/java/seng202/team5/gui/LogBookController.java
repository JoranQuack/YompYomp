package seng202.team5.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.concurrent.Task;
import javafx.application.Platform;
import seng202.team5.App;
import seng202.team5.gui.components.TrailCardComponent;
import seng202.team5.models.Trail;
import seng202.team5.services.LogService;
import seng202.team5.services.SearchService;

/**
 * Controller for the trails display screen.
 * Handles trail search, pagination, and card display with performance
 * optimizations.
 */
public class LogBookController extends Controller {

    private LogService logService;
    private String searchText;
    private final List<TrailCardComponent> logCardPool = new ArrayList<>();

    private boolean isUpdating = false;

    // FXML components
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchBarTextField;
    @FXML
    private FlowPane trailsContainer;
    @FXML
    private Label resultsLabel;
    @FXML
    private ChoiceBox<String> pageChoiceBox;

    /**
     * Creates controller with navigator.
     *
     * @param navigator Screen navigator
     */
    public LogBookController(ScreenNavigator navigator) {
        super(navigator);
    }

    /**
     * Initialises the trails view with default data.
     */
    @FXML
    private void initialize() {
        isUpdating = true;

        initializeLogService();
        if (logService == null) {
            handleInitializationFailure();
            return;
        }

        //setupUIComponents();
        isUpdating = false;

        // show loading stuff straight away
        //showLoadingState();

        // Load data asynchronously
        //loadInitialDataAsync();
    }

    /**
     * Initializes the search service.
     */
    private void initializeLogService() {
        this.logService = new LogService(App.getDatabaseService());
    }

    /**
     * Handles initialization failures by displaying an error message.
     */
    private void handleInitializationFailure() {
        resultsLabel.setText("No logs available");
        Label noResultsLabel = new Label(
                "There are no logs available, as you have not logged any trails yet. Please create a trail log and try again.");
        trailsContainer.getChildren().add(noResultsLabel);
        showAlert(Alert.AlertType.ERROR, "No logs available",
                "Failed to load logs, please add a trail log and try again.");
    }


    @Override
    protected String getFxmlFile() {
        return "/fxml/logbook.fxml";
    }

    @Override
    protected String getTitle() {
        return "Logbook Screen";
    }

    @Override
    protected boolean shouldShowNavbar(){
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return -1;
    }
}