package seng202.team5.gui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.User;
import seng202.team5.services.MatchmakingService;

public class MatchmakingController extends Controller {
    private MatchmakingService matchMakingService;

    @FXML
    private ProgressIndicator progressIndicator;

    /**
     * Constructs the controller with navigator
     *
     * @param navigator screen navigator
     */
    public MatchmakingController(ScreenNavigator navigator) {
        super(navigator);
        DatabaseService databaseService = new DatabaseService();
        matchMakingService = new MatchmakingService(
                new SqlBasedKeywordRepo(databaseService),
                new SqlBasedTrailRepo(databaseService));

        // Use Platform.runLater to execute
        javafx.application.Platform.runLater(this::startMatchmaking);
    }

    /**
     * Sets up the UI components
     */
    @FXML
    private void initialize() {
        if (progressIndicator != null) {
            progressIndicator.setProgress(-1.0); // Indeterminate progress
        }
    }

    /**
     * Starts the matchmaking process
     */
    private void startMatchmaking() {
        // Set progress indicator to indeterminate
        progressIndicator.setProgress(-1.0);

        final ScreenNavigator navigator = super.getNavigator();
        final User user = super.getUserService().getUser();

        // Create a background task for the matchmaking process
        Task<Void> matchmakingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (user == null) {
                    return null;
                }


                    // Run the matchmaking process
                    matchMakingService.generateTrailWeights(user);
                    return null;


            }

            @Override
            protected void succeeded() {
                navigator.launchScreen(new DashboardController(navigator));
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                System.err.println("Matchmaking failed: " + exception.getMessage());
                exitThread();
//                showAlert(AlertType.ERROR, "Matchmaking Failed", exception.getMessage());

            }
        };

        Thread backgroundThread = new Thread(matchmakingTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/matchmaking.fxml";
    }

    @Override
    protected String getTitle() {
        return "Matchmaking In Progress";
    }

    /**
     * Handles cleanup and user notification when the matchmaking thread fails
     */
    private void exitThread() {
        Thread.currentThread().interrupt();
        showAlert(AlertType.ERROR, "Matchmaking Failed", "Matchmaking failed, please close the application and try again.");
        super.getNavigator().launchScreen(new DashboardController(super.getNavigator())); //TODO this should take user to guest dashboard screen
    }

}
