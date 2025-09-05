package seng202.team5.gui;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.services.SetupService;

/**
 * Class that starts the JavaFX application thread.
 */
public class FXAppEntry extends Application {

    private ExecutorService setupExec;

    /**
     * Creates the application with a {@link ScreenNavigator} for the
     * given {@link Stage} and handles background setup
     *
     * @param primaryStage The current fxml stage, handled by this JavaFX
     *                     Application class
     */
    @Override
    public void start(Stage primaryStage) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/YompYompIcon.png")));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("YompYomp");
        primaryStage.show();

        ScreenNavigator navigator = new ScreenNavigator(primaryStage);
        WelcomeController welcome = new WelcomeController(navigator);
        navigator.launchScreen(welcome);

        // Initialise background setup
        setupExec = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "setup-worker");
            thread.setDaemon(true);
            return thread;
        });

        runSetupInBackground(welcome);
    }

    /**
     * This method is used to start the setup of the application on the second
     * thread
     *
     * @param welcomeController the welcome controller to potentially update
     */
    private void runSetupInBackground(WelcomeController welcomeController) {
        Task<Void> setupTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Starting setup...");

                DatabaseService dbService = new DatabaseService();
                SqlBasedTrailRepo sqlTrailRepo = new SqlBasedTrailRepo(dbService);
                FileBasedTrailRepo fileTrailRepo = new FileBasedTrailRepo(
                        "/datasets/DOC_Walking_Experiences_7994760352369043452.csv");
                SetupService setupService = new SetupService(sqlTrailRepo, fileTrailRepo);

                setupService.setupApplication();
                return null;
            }
        };

        // Handle setup completion
        setupTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                System.out.println("Background worker completed");
            });
        });

        setupExec.submit(setupTask);
    }

    /**
     * This method is called when the application is shutting down
     */
    @Override
    public void stop() {
        if (setupExec != null) {
            setupExec.shutdownNow();
        }
    }
}
