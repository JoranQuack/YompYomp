package seng202.team5.gui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.User;
import seng202.team5.services.MatchMakingService;

public class MatchmakingController extends Controller {
    private MatchMakingService matchMakingService;

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
        matchMakingService = new MatchMakingService(
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

                try {
                    // Run the matchmaking process
                    matchMakingService.generateTrailWeights(user);
                    return null;
                }
                catch (MatchMakingFailedException exception) {
                    showAlert(AlertType.ERROR, "Matchmaking failed", "Matchmaking failed, please close the application and try again");
                }
            }

            @Override
            protected void succeeded() {
                navigator.launchScreen(new DashboardController(navigator));
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                System.err.println("Matchmaking failed: " + exception.getMessage());
                exception.printStackTrace();
                navigator.launchScreen(new DashboardController(navigator));
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

}
