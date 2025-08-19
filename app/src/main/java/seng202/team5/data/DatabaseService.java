package seng202.team5.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.URL;

public class DatabaseService {
    private static DatabaseService instance;
    private Connection connection;

    private DatabaseService() {
        connect();
    }

    /**
     * Returns the instance of the DatabaseService.
     *
     * @return the instance of the DatabaseService
     */
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    /**
     * Establishes a connection to the database.
     */
    private void connect() {
        try {
            URL dbResource = DatabaseService.class.getResource("/data/database/main.db");
            if (dbResource == null) {
                throw new SQLException("Database file not found in resources");
            }

            String url = "jdbc:sqlite:" + dbResource.getPath();
            connection = DriverManager.getConnection(url);

            // Enable foreign keys (recommended for SQLite)
            connection.createStatement().execute("PRAGMA foreign_keys = ON");

            System.out.println("Database connection established.");

        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }

    /**
     * Returns the database connection.
     *
     * @return the database connection
     */
    public Connection getConnection() {
        try {
            // Check if connection is still valid
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Connection validation failed: " + e.getMessage());
            connect();
        }
        return connection;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Shutdown hook for closing database connection.
     */
    public static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (instance != null) {
                instance.closeConnection();
            }
        }));
    }

    public boolean testConnection() {
        try (var conn = getConnection();
        var stmt = conn.createStatement();
        var rs = stmt.executeQuery("SELECT 1")) {
            return rs.next();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
