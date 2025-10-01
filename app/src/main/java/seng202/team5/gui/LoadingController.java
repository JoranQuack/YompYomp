package seng202.team5.gui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import seng202.team5.App;
import seng202.team5.models.User;
import seng202.team5.services.MatchmakingService;
import seng202.team5.services.UserService;

public class LoadingController extends Controller {

    private User user;
    private boolean isSkip = false;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label statusLabel;

    /**
     * Constructs the controller with navigator
     *
     * @param navigator screen navigator
     */
    public LoadingController(ScreenNavigator navigator, User user) {
        super(navigator);
        this.user = user;
        if (user != null) {
            isSkip = false;
        } else {
            isSkip = true;
        }

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
        if (isSkip) {
            statusLabel.setText("Just a moment...");
        } else {
            statusLabel.setText("Matching you to your favourite trails...");
        }
    }

    /**
     * Starts the matchmaking process
     */
    private void startMatchmaking() {
        // Set progress indicator to indeterminate
        progressIndicator.setProgress(-1.0);

        final ScreenNavigator navigator = super.getNavigator();
        final UserService userService = super.getUserService();

        // Create a background task for the matchmaking process
        Task<Void> matchmakingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Wait for database setup before we can matchmake
                App.getSetupService().waitForDatabaseSetup();

                // If skipping, set user as guest and skip the matchmaking and saving
                if (isSkip) {
                    if (userService.getUser() == null) {
                        userService.setGuest(true);
                    }
                    return null;
                }

                // Save the user to the database
                userService.clearUser();
                userService.saveUser(user);

                // Create MatchmakingService AFTER database setup is complete
                MatchmakingService matchmakingService = new MatchmakingService(App.getDatabaseService());

                // Get user AFTER database setup is complete
                User user = getUserService().getUser();
                if (user == null) {
                    return null;
                }

                // Run the matchmaking process
                matchmakingService.generateTrailWeights(user);
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
                exception.printStackTrace();
                navigator.launchScreen(new DashboardController(navigator));
                exitThread();
            }
        };

        Thread backgroundThread = new Thread(matchmakingTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/loading.fxml";
    }

    @Override
    public String getTitle() {
        return "Getting the app ready...";
    }

    @Override
    protected boolean shouldShowNavbar() {
        return false;
    }

    @Override
    protected int getNavbarPageIndex() {
        return -1; // No navbar
    }

    /**
     * Handles cleanup and user notification when the matchmaking thread fails
     */
    private void exitThread() {
        Thread.currentThread().interrupt();
        showAlert(AlertType.ERROR, "Matchmaking Failed",
                "Matchmaking failed, please close the application and try again.");
        super.getNavigator().launchScreen(new DashboardController(super.getNavigator()));
    }

}
