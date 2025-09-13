package seng202.team5.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseService {
    private final String customDatabasePath;
    private static String currentSchemaVersion;

    public DatabaseService() {
        this.customDatabasePath = null;
        currentSchemaVersion = getCurrentSchemaVersion();
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
        if (customDatabasePath != null && !customDatabasePath.isEmpty()) {
            return customDatabasePath;
        }
        return AppDataManager.getAppData("database/main.db");
    }

    /**
     * Checks if the database file exists.
     *
     * @return true or false, what will it be?
     */
    public boolean databaseExists() {
        String databasePath = getDatabasePath();
        return new java.io.File(databasePath).exists();
    }

    /**
     * Deletes the database file if it exists.
     *
     * @return true on success, false otherwise (duh)
     */
    public boolean deleteDatabase() {
        System.out.println("Deleting database");
        String databasePath = getDatabasePath();
        java.io.File dbFile = new java.io.File(databasePath);
        if (dbFile.exists()) {
            return dbFile.delete();
        }
        return true;
    }

    /**
     * Creates the database and initialises it with the schema if it doesn't exist.
     *
     * @throws SQLException if error creating database or executing schema
     */
    public void createDatabaseIfNotExists() throws SQLException {
        if (!databaseExists()) {
            String databasePath = getDatabasePath();
            java.io.File dbFile = new java.io.File(databasePath);
            dbFile.getParentFile().mkdirs();

            // Create the database file and do schema things to it
            try (Connection connection = getConnection()) {
                executeSchemaScript(connection);
            }
        }
    }

    /**
     * Checks if the database schema is up to date.
     *
     * @return true if schema matches current version, false otherwise
     */
    public boolean isSchemaUpToDate() {
        if (!databaseExists()) {
            return false;
        }

        try (Connection connection = getConnection()) {
            // Check if schema_version table exists
            var metaData = connection.getMetaData();
            var tables = metaData.getTables(null, null, "schema_version", null);

            if (!tables.next()) {
                return false; // If there's no version table, it's outdated
            }

            // Check the version matches our current version
            try (var stmt = connection.prepareStatement("SELECT version FROM schema_version LIMIT 1")) {
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    return currentSchemaVersion.equals(rs.getString("version"));
                }
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Executes the schema script to create all the tables we need
     *
     * @param connection the database connection
     * @throws SQLException if error executing the schema
     */
    private void executeSchemaScript(Connection connection) throws SQLException {
        String schemaContent = readSchemaFile();

        try (Statement statement = connection.createStatement()) {
            String[] statements = schemaContent.split(";");
            for (String sql : statements) {
                sql = sql.trim();
                if (!sql.isEmpty() && !sql.startsWith("--")) {
                    statement.execute(sql);
                }
            }

            // Insert schema version
            statement.execute("CREATE TABLE IF NOT EXISTS schema_version (version TEXT PRIMARY KEY)");
            try (var preparedStmt = connection
                    .prepareStatement("INSERT OR REPLACE INTO schema_version (version) VALUES (?)")) {
                preparedStmt.setString(1, currentSchemaVersion);
                preparedStmt.execute();
            }
        }
    }

    /**
     * Get the current schema version from the schema file.
     *
     * @return the current schema version
     */
    private String getCurrentSchemaVersion() {
        String schema = readSchemaFile();
        String version = null;
        for (String line : schema.split("\n")) {
            if (line.startsWith("-- Schema version:")) {
                version = line.substring("-- Schema version:".length()).trim();
                break;
            }
        }
        return version;
    }

    /**
     * Reads the schema file from resources.
     *
     * @return the schema file content as a string
     */
    private String readSchemaFile() {
        try {
            java.io.InputStream inputStream = getClass().getResourceAsStream("/schema/schema.sql");
            if (inputStream == null) {
                throw new RuntimeException("Schema file not found in resources");
            }
            return new String(inputStream.readAllBytes());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error reading schema file", e);
        }
    }
}
