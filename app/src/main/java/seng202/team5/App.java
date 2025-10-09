package seng202.team5;

import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedFilterOptionsRepo;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.gui.FXAppEntry;
import seng202.team5.services.SetupService;
import seng202.team5.services.UserService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main application entry point.
 * Launches the application.
 */
public class App {
    private static SetupService setupService;
    private static final DatabaseService databaseService = new DatabaseService();

    private static final SqlBasedFilterOptionsRepo filterOptionsRepo = new SqlBasedFilterOptionsRepo(databaseService);
    private static final SqlBasedKeywordRepo keywordRepo = new SqlBasedKeywordRepo(databaseService);
    private static final SqlBasedTrailLogRepo trailLogRepo = new SqlBasedTrailLogRepo(databaseService);
    private static final SqlBasedTrailRepo trailRepo = new SqlBasedTrailRepo(databaseService);

    private static final UserService userService = new UserService(trailRepo, databaseService);

    /**
     * Application entry point. It handles the starting of the executer and then
     * calls FXAppEntry to start the UI
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        setupApplication();
        FXAppEntry.launch(FXAppEntry.class, args);

    }

    /**
     * Resets the application data by clearing the database and re-running setup.
     */
    public static void resetApplication() {
        databaseService.deleteDatabase();
        setupApplication();
    }

    /**
     * Sets up the application by setting up the database and scraping images
     *
     * @param setupService
     * @param setupExec
     */
    public static void setupApplication() {
        setupService = new SetupService(trailRepo, databaseService);

        ExecutorService setupExec = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "setup-worker");
            thread.setDaemon(true);
            return thread;
        });

        runSetupInBackground(setupService, setupExec);
        // shutdown hook for when application closed
        Runtime.getRuntime().addShutdownHook(new Thread(setupExec::shutdown));
    }

    /**
     * This method is used to start the setup of the application on the second
     * thread
     */
    private static void runSetupInBackground(SetupService setupService, ExecutorService setupExec) {
        setupExec.execute(() -> {
            System.out.println("setup starting...");
            try {
                setupService.setupApplication();
                System.out.println("setup complete.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Gets the setup service instance
     *
     * @return the setup service instance
     */
    public static SetupService getSetupService() {
        return setupService;
    }

    /**
     * Gets the database service instance
     *
     * @return the database service instance
     */
    public static DatabaseService getDatabaseService() {
        return databaseService;
    }

    /**
     * Gets the user service instance
     *
     * @return the user service instance
     */
    public static UserService getUserService() {
        return userService;
    }

    /**
     * Gets the filter options repository instance
     *
     * @return the filter options repository instance
     */
    public static SqlBasedFilterOptionsRepo getFilterOptionsRepo() {
        return filterOptionsRepo;
    }

    /**
     * Gets the keyword repository instance
     *
     * @return the keyword repository instance
     */
    public static SqlBasedKeywordRepo getKeywordRepo() {
        return keywordRepo;
    }

    /**
     * Gets the trail log repository instance
     *
     * @return the trail log repository instance
     */
    public static SqlBasedTrailLogRepo getTrailLogRepo() {
        return trailLogRepo;
    }

    /**
     * Gets the trail repository instance
     *
     * @return the trail repository instance
     */
    public static SqlBasedTrailRepo getTrailRepo() {
        return trailRepo;
    }
}
