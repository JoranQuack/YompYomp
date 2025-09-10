package seng202.team5;

import seng202.team5.gui.FXAppEntry;
import seng202.team5.services.SetupService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main application entry point.
 * Launches the application.
 */
public class App {
    /**
     * Application entry point. It handles the starting of the executer and then
     * calls FXAppEntry to start the UI
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        ExecutorService setupExec = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "setup-worker");
            thread.setDaemon(true);
            return thread;
        });

        runSetupInBackground(new SetupService(), setupExec);
        // shutdown hook for when application closed
        Runtime.getRuntime().addShutdownHook(new Thread(setupExec::shutdown));

        FXAppEntry.launch(FXAppEntry.class, args);

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

}
