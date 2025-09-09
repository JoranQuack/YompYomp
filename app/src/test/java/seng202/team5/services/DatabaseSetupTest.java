package seng202.team5.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seng202.team5.data.DatabaseService;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for database setup functionality.
 */
public class DatabaseSetupTest {

    private DatabaseService databaseService;
    private String testDbPath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        testDbPath = tempDir.resolve("test.db").toString();
        databaseService = new DatabaseService(testDbPath);
    }

    @AfterEach
    void tearDown() {
        File dbFile = new File(testDbPath);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    @DisplayName("Should create database when unexisting")
    void testCreateDatabaseIfNotExists() throws SQLException {
        assertFalse(databaseService.databaseExists());

        databaseService.createDatabaseIfNotExists();

        assertTrue(databaseService.databaseExists());
    }

    @Test
    @DisplayName("Should NOT create database when existing")
    void testCreateDatabaseIfNotExists_AlreadyExists() throws SQLException {
        databaseService.createDatabaseIfNotExists();
        assertTrue(databaseService.databaseExists());

        assertDoesNotThrow(() -> databaseService.createDatabaseIfNotExists());
        assertTrue(databaseService.databaseExists());
    }

    @Test
    @DisplayName("Should delete database just like dat")
    void testDeleteDatabase() throws SQLException {
        databaseService.createDatabaseIfNotExists();
        assertTrue(databaseService.databaseExists());

        assertTrue(databaseService.deleteDatabase());
        assertFalse(databaseService.databaseExists());
    }

    @Test
    @DisplayName("Should create all tables we want")
    void testCreateAllTables() throws SQLException {
        databaseService.createDatabaseIfNotExists();

        try (Connection connection = databaseService.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            String[] expectedTables = { "category", "keyword", "trail", "trail_category", "user" };

            for (String tableName : expectedTables) {
                try (ResultSet tables = metaData.getTables(null, null, tableName, null)) {
                    assertTrue(tables.next(), "Table " + tableName + " should exist");
                }
            }
        }
    }

    @Test
    @DisplayName("Should have correct columns in trail table")
    void testTrailTableColumns() throws SQLException {
        databaseService.createDatabaseIfNotExists();

        try (Connection connection = databaseService.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet columns = metaData.getColumns(null, null, "trail", null)) {
                boolean hasUserWeight = false;
                boolean hasId = false;
                boolean hasName = false;

                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME").toLowerCase();
                    if ("user_weight".equals(columnName)) {
                        hasUserWeight = true;
                    } else if ("id".equals(columnName)) {
                        hasId = true;
                    } else if ("name".equals(columnName)) {
                        hasName = true;
                    }
                }

                assertTrue(hasUserWeight, "Trail table should have user_weight column");
                assertTrue(hasId, "Trail table should have id column");
                assertTrue(hasName, "Trail table should have name column");
            }
        }
    }
}
