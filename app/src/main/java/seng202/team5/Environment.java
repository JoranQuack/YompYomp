package seng202.team5;

import javafx.application.Platform;
import javafx.concurrent.Task;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import seng202.team5.gui.ScreenNavigator;
import seng202.team5.gui.WelcomeController;
import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.data.DatabaseService;
import seng202.team5.services.SetupService;

public class Environment {

    private final ScreenNavigator navigator;

    //background worker + setup
    private final ExecutorService setupExec;
    private final SetupService setupService;

    /**
     * Constructor for the Environment class. Initializes the environment
     * with a ScreenNavigator instance, filerepo, sqlrepo and db instance.
     *
     * @param navigator The ScreenNavigator instance for navigating between screens
     */
    public Environment(ScreenNavigator navigator) {
        this.navigator = navigator;

        DatabaseService dbService = new DatabaseService();
        SqlBasedTrailRepo sqlTrailRepo = new SqlBasedTrailRepo(dbService);
        FileBasedTrailRepo fileTrailRepo = new FileBasedTrailRepo("/datasets/DOC_Walking_Experiences_7994760352369043452.csv");
        this.setupService = new SetupService(sqlTrailRepo, fileTrailRepo);

        WelcomeController welcome = new WelcomeController(this, navigator);
        navigator.launchScreen(welcome);

        this.setupExec = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "setup-worker");
            thread.setDaemon(true);
            return thread;
        });

        runSetupInBackground(welcome);
    }

    /**
     * This method is used to start the setup of the applciation on the second thread
     * @param welcomeController
     */
    private void runSetupInBackground(WelcomeController welcomeController) {
        Task<Void> setupTask = new Task<>() {
            @Override protected Void call() throws Exception {
                updateMessage("Starting setup...");
                setupService.setupApplication();

                return null;
            }
        };

        // TODO: add setOnFailed methods
        /*
        This sub method prints to the console when the background worker proccess is complete
         */
        setupTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                System.out.println("Background worker completed");
            });
        });

        setupExec.submit(setupTask);
    }

    /**
     * This method needs to be called from the app shutdown hook to ensure background worker is shut down
     */
    public void shutdown() {
        setupExec.shutdownNow();
    }

    /**
     * Gets the screen navigator for this environment.
     *
     * @return The ScreenNavigator instance
     */
    public ScreenNavigator getNavigator() {
        return navigator;
    }
}
