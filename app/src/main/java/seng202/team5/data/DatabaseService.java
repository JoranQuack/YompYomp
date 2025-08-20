package seng202.team5.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.URL;

public class DatabaseService {
    private final String databasePath;

    /**
     * Constructor with default database location
     */
    public DatabaseService() {
        this("/data/database/main.db");
    }

    /**
     * Constructor that allows custom database path (testing)
     */
    public DatabaseService(String databasePath) {
        this.databasePath = databasePath;
    }

    /**
     * Creates a new database connection each time it's called.
     */
    public Connection getConnection() throws SQLException {
        URL dbResource = DatabaseService.class.getResource(databasePath);
        if (dbResource == null) {
            throw new SQLException("Database file not found in resources: " + databasePath);
        }

        String url = "jdbc:sqlite:" + dbResource.getPath();
        Connection connection = DriverManager.getConnection(url);

        // Enable foreign keys (will need this for SQLite)
        connection.createStatement().execute("PRAGMA foreign_keys = ON");

        return connection;
    }

    /**
     * Test connection method
     */
    public boolean testConnection() {
        try (Connection conn = getConnection();
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery("SELECT 1")) {
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
