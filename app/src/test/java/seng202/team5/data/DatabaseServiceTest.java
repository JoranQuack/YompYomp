package seng202.team5.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseServiceTest {

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
    void tearDown() throws SQLException {
        File dbFile = new File(testDbPath);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    @DisplayName("Should retrieve a database connection and make sure it is valid and opened")
    public void getConnectionTest() throws SQLException {
        Connection connection = databaseService.getConnection();

        assertNotNull(connection);
        assertFalse(connection.isClosed());
        assertTrue(connection.isValid(5));

        connection.close();
    }

    @Test
    @DisplayName("Should create a database connection using the default constructor")
    void testDefaultConstructor() throws SQLException {
        DatabaseService defaultService = new DatabaseService();

        // Make sure DB exists to avoid issues in CI
        defaultService.createDatabaseIfNotExists();

        Connection connection = defaultService.getConnection();

        assertNotNull(connection);
        assertFalse(connection.isClosed());

        connection.close();
    }

    @Test
    @DisplayName("Should create a database connection using a custom database path for testing")
    void testCustomDatabasePath() throws SQLException {
        DatabaseService customService = new DatabaseService(testDbPath);
        Connection connection = customService.getConnection();

        assertNotNull(connection, "Connection should not be null for valid database path");
        assertFalse(connection.isClosed());

        connection.close();
    }

    @Test
    @DisplayName("Should ensure foreign keys are enabled in the database connection")
    void testForeignKeysEnabled() throws SQLException {
        Connection connection = databaseService.getConnection();

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("PRAGMA foreign_keys");

        assertTrue(result.next());
        assertEquals(1, result.getInt(1));

        result.close();
        statement.close();
        connection.close();
    }

    @Test
    @DisplayName("Should handle invalid database path by throwing SQLException")
    void testInvalidDatabasePath() {
        DatabaseService invalidService = new DatabaseService("invalid/path/database.db");

        assertThrows(SQLException.class, () -> {
            invalidService.getConnection();
        });
    }

    @Test
    @DisplayName("Should handle null or empty database path *also gracefully*")
    void testNullDatabasePath() {
        DatabaseService nullPathService = new DatabaseService(null);

        assertDoesNotThrow(() -> {
            // Make sure DB exists to avoid issues in CI
            nullPathService.createDatabaseIfNotExists();

            Connection connection = nullPathService.getConnection();
            assertNotNull(connection);
            connection.close();
        });
    }

    @Test
    @DisplayName("Should handle empty database path *gracefullier*")
    void testEmptyDatabasePath() {
        DatabaseService emptyPathService = new DatabaseService("");

        assertDoesNotThrow(() -> {
            // Make sure DB exists to avoid issues in CI
            emptyPathService.createDatabaseIfNotExists();

            Connection connection = emptyPathService.getConnection();
            assertNotNull(connection);
            connection.close();
        });
    }

    @Test
    @DisplayName("Should allow multiple connections to the database at a time")
    void testMultipleConnections() throws SQLException {
        Connection connection1 = databaseService.getConnection();
        Connection connection2 = databaseService.getConnection();

        assertNotNull(connection1);
        assertNotNull(connection2);
        assertNotSame(connection1, connection2);

        connection1.close();
        connection2.close();
    }
}
