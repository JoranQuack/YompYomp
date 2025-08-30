package seng202.team5.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        try {
            Connection connection = DriverManager.getConnection(url);
            // Enable foreign keys
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getDatabasePath() {
        // Use custom path if provided (for testing)
        if (customDatabasePath != null) {
            return customDatabasePath;
        }
        return AppDataManager.getAppData("database/main.db");
    }
}
