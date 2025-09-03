package seng202.team5.data;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helper class for resolving test resource paths between our local development
 * and CI/CD
 */
public class TestPathHelper {

    /**
     * Gets the absolute path to a test resource file.
     *
     * @param resourcePath the relative path to the resource from
     *                     src/test/resources/
     * @return the absolute path to the resource
     */
    public static String getTestResourcePath(String resourcePath) {
        // Try ClassLoader first
        try {
            URL resource = TestPathHelper.class.getClassLoader().getResource(resourcePath);
            if (resource != null) {
                String path = resource.getPath();
                // Windows paths that start with / but contain :
                if (path.startsWith("/") && path.contains(":")) {
                    path = path.substring(1);
                }
                return path;
            }
        } catch (Exception e) {
            System.err.println("ClassLoader resource lookup failed for " + resourcePath + ": " + e.getMessage());
        }

        String userDir = System.getProperty("user.dir");

        Path testResourcePath = Paths.get(userDir, "app", "src", "test", "resources", resourcePath);
        if (testResourcePath.toFile().exists()) {
            return testResourcePath.toString();
        }

        testResourcePath = Paths.get(userDir, "src", "test", "resources", resourcePath);
        if (testResourcePath.toFile().exists()) {
            return testResourcePath.toString();
        }

        testResourcePath = Paths.get(userDir, "build", "resources", "test", resourcePath);
        if (testResourcePath.toFile().exists()) {
            return testResourcePath.toString();
        }

        testResourcePath = Paths.get(userDir, "app", "build", "resources", "test", resourcePath);
        if (testResourcePath.toFile().exists()) {
            return testResourcePath.toString();
        }

        if (resourcePath.contains("database") && resourcePath.endsWith(".db")) {
            try {
                File tempFile = File.createTempFile("test-db", ".db");
                tempFile.deleteOnExit();
                System.err.println("Created temporary database file: " + tempFile.getAbsolutePath());
                return tempFile.getAbsolutePath();
            } catch (Exception e) {
                System.err.println("Could not create temp file, using in-memory database");
                return ":memory:";
            }
        }

        // If all hope seems to have been lost, return original path and let SQLite
        // handle it
        System.err.println("Could not resolve test resource path, returning original: " + resourcePath);
        return resourcePath;
    }
}
