package seng202.team5.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.concurrent.Task;
import javafx.application.Platform;
import seng202.team5.App;
import seng202.team5.gui.components.TrailCardComponent;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import seng202.team5.services.LogService;

/**
 * Controller for the logged trail display screen.
 * Handles trail search, pagination, and card display with performance
 * optimisations.
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
    private FlowPane logContainer;
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
     * Initialises the logs view with default data.
     */
    @FXML
    private void initialize() {
        isUpdating = true;

        initializeLogService();
        if (logService.getAllLogs().isEmpty()) {
            handleInitializationFailure();
            return;
        }

        setupPageChoiceBoxListener();
        isUpdating = false;

        // show loading stuff straight away
        showLoadingState();

        // Load data asynchronously
        loadInitialDataAsync();
    }

    /**
     * Initialises the log service.
     */
    private void initializeLogService() {
        this.logService = new LogService(App.getDatabaseService());
    }

    /**
     * Handles initialisation failures by displaying an error message.
     */
    private void handleInitializationFailure() {
        resultsLabel.setText("No logs available");
        Label noResultsLabel = new Label(
                "There are no logs available, as you have not logged any trails yet. Please create a trail log and try again.");
        logContainer.getChildren().add(noResultsLabel);
    }

    /**
     * Sets up the page choice box listener for pagination.
     */
    private void setupPageChoiceBoxListener() {
        pageChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!isUpdating) {
                        onPageSelected();
                    }
                });
    }

    /**
     * Handles page selection changes by updating the displayed logs.
     */
    private void onPageSelected() {
        String selectedPage = pageChoiceBox.getValue();
        if (selectedPage != null) {
            int pageIndex = Integer.parseInt(selectedPage) - 1;
            List<TrailLog> logs = logService.getPage(pageIndex);
            updateLogsDisplay(logs);
        }
    }

    /**
     * Shows a loading state while logs are being fetched.
     */
    private void showLoadingState() {
        logContainer.getChildren().clear();
        Label loadingLabel = new Label("Loading trails...");
        logContainer.getChildren().add(loadingLabel);
        resultsLabel.setText("Loading...");
    }

    /**
     * Loads initial data asynchronously
     */
    private void loadInitialDataAsync() {
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (searchText != null) {
                    logService.setCurrentQuery(searchText);
                }
                logService.getPage(0);
                return null;
            }

            @Override
            protected void succeeded() {
                // JavaFX app thread
                Platform.runLater(() -> {
                    updateSearchDisplay();
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    logContainer.getChildren().clear();
                    Label errorLabel = new Label("Failed to load trails. Please try again.");
                    logContainer.getChildren().add(errorLabel);
                    resultsLabel.setText("Error loading trails");
                });
            }
        };

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    /**
     * Updates the displayed logs based on the current search, filter, sort, and
     * pagination settings.
     */
    private void updateSearchDisplay() {
        List<TrailLog> logs = logService.getPage(0);
        updateLogsDisplay(logs);
        resetPageChoiceBox();
    }

    /**
     * Updates the logs displayed in the UI, reusing TrailCardComponents from a
     * pool for performance.
     *
     * @param logs List of logs to display
     */
    private void updateLogsDisplay(List<TrailLog> logs) {
        logContainer.getChildren().clear();

        if (logs.isEmpty()) {
            // showNoResultsMessage();
            return;
        }
        Insets cardMargin = new Insets(10);

        for (int i = 0; i < logs.size(); i++) {
            TrailLog log = logs.get(i);
            Optional<Trail> trailOpt = logService.getTrail(log.getTrailId());
            if (trailOpt.isPresent()) {
                Trail trail = trailOpt.get();
                TrailCardComponent logCard = getOrCreateLogCard(i, cardMargin);

                logCard.setData(trail, log);
                logCard.setOnMouseClicked(e -> {
                    onLogCardClicked(log);
                });
                logContainer.getChildren().add(logCard);
            }
        }

        updateResultsLabel(logs.size());
    }

    /**
     * Retrieves a TrailCardComponent from the pool or creates a new one if needed.
     *
     * @param index      The index of the log card
     * @param cardMargin The margin to apply to the card
     * @return A TrailCardComponent
     */
    private TrailCardComponent getOrCreateLogCard(int index, Insets cardMargin) {
        if (index < logCardPool.size()) {
            return logCardPool.get(index);
        } else {
            TrailCardComponent logCard = new TrailCardComponent();
            logCardPool.add(logCard);
            VBox.setMargin(logCard, cardMargin);
            return logCard;
        }
    }

    /**
     * Updates the result label to show the number of trails currently displayed
     *
     * @param trailCount Number of trails currently displayed
     */
    private void updateResultsLabel(int trailCount) {
        resultsLabel.setText(trailCount + "/" + logService.countLogs() + " trails showing");
    }

    /**
     * Resets the page selection dropdown to the initial state.
     */
    private void resetPageChoiceBox() {
        isUpdating = true;

        pageChoiceBox.getItems().clear();
        int numPages = logService.getNumberOfPages();

        if (numPages > 0) {
            String[] pageItems = new String[numPages];
            for (int i = 0; i < numPages; i++) {
                pageItems[i] = String.valueOf(i + 1);
            }
            pageChoiceBox.getItems().addAll(pageItems);
            pageChoiceBox.setValue("1");
        }

        isUpdating = false;
    }

    /**
     * Handles the event when a user clicks on a log card.
     * Retrieves the corresponding trail associated with the log and navigates to
     * the trail details screen.
     *
     * @param log The TrailLog object representing the log card that was clicked.
     */
    @FXML
    private void onLogCardClicked(TrailLog log) {
        Optional<Trail> trailOpt = logService.getTrail(log.getTrailId());
        trailOpt.ifPresent(trail -> {
            super.getNavigator().launchScreen(
                    new LogTrailController(super.getNavigator(), trail, log));
        });
    }

    /**
     * Handles the event when the user clicks the search button and updates the
     * displayed logs.
     */
    @FXML
    private void onSearchButtonClicked() {
        logService.setCurrentQuery(searchBarTextField.getText());
        updateSearchDisplay();
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
    protected boolean shouldShowNavbar() {
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return 2;
    }

    @Override
    public void onLoadFailed(Exception e) {
        showAlert(
                "Log Book Failed To Load",
                "Error",
                "Logbook failed to load, please close and reload the application.",
                "OK",
                null,
                "error-button");

    }
}