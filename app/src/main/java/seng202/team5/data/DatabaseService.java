package seng202.team5.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Paths;
import java.nio.file.Files;

public class DatabaseService {
    private final String customDatabasePath;

    public DatabaseService() {
        this.customDatabasePath = null;
    }

    // Testing constructor with custom path
    public DatabaseService(String databasePath) {
        this.customDatabasePath = databasePath;
    }

    /**
     * Creates a new database connection to the existing database.
     */
    public Connection getConnection() throws SQLException {
        String databasePath = getDatabasePath();
        String url = "jdbc:sqlite:" + databasePath;
        Connection connection = DriverManager.getConnection(url);

        // Enable foreign keys
        connection.createStatement().execute("PRAGMA foreign_keys = ON");

        return connection;
    }

    private String getDatabasePath() {
        // Use custom path if provided (for testing)
        if (customDatabasePath != null) {
            return customDatabasePath;
        }

        String projectRoot = System.getProperty("user.dir");
        String databasePath;

        // Check if running from root (./gradlew run) or app/
        if (Files.exists(Paths.get(projectRoot, "app", "data", "database", "main.db"))) {
            databasePath = Paths.get(projectRoot, "app", "data", "database", "main.db").toString();
        } else {
            databasePath = Paths.get(projectRoot, "data", "database", "main.db").toString();
        }

        return databasePath;
    }
}
