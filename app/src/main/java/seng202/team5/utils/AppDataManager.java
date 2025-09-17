package seng202.team5.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main function is to get a path for the application data directory.
 */
public class AppDataManager {

    /**
     * Returns the path for the application data with the specified subdirectory.
     *
     * @param subDirectory the subdirectory path
     * @return the full path for the application data directory with subdirectory
     */
    public static String getAppData(String subDirectory) {
        String projectRoot = System.getProperty("user.dir");
        String path;

        // Check if running from root (./gradlew run) or app/
        Path rootReference = Paths.get(projectRoot, "app", "data", subDirectory);
        if (Files.exists(rootReference.getParent()) || Files.exists(rootReference)) {
            path = rootReference.toString();
        } else {
            path = Paths.get(projectRoot, "data", subDirectory).toString();
        }
        return path;
    }
}
