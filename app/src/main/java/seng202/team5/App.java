package seng202.team5;

import seng202.team5.data.DatabaseService;
import seng202.team5.gui.FXAppEntry;

/**
 * Main application entry point.
 * Launches the application.
 */
public class App {
    /**
     * Application entry point.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        DatabaseService.addShutdownHook(); // Add shutdown hook for clean database closure
        FXAppEntry.launch(FXAppEntry.class, args);
    }
}