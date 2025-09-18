package seng202.team5.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class QueryHelperTest {

    private QueryHelper queryHelper;
    private DatabaseService databaseService;
    private String testDbPath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws SQLException {
        testDbPath = tempDir.resolve("test.db").toString();
        databaseService = new DatabaseService(testDbPath);
        queryHelper = new QueryHelper(databaseService);

        // Create a test table for tests
        try (Connection conn = databaseService.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS test_table (id INTEGER PRIMARY KEY, name TEXT, value INTEGER)");
            stmt.execute("DELETE FROM test_table"); // Clean slate for each test
            stmt.execute("INSERT INTO test_table (id, name, value) VALUES (1, 'Test1', 100)");
            stmt.execute("INSERT INTO test_table (id, name, value) VALUES (2, 'Test2', 200)");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up test table
        try (Connection conn = databaseService.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS test_table");
        }
    }

    @Test
    @DisplayName("Should execute then return list of results")
    void testExecuteQuery() {
        String sql = "SELECT id, name, value FROM test_table WHERE value > ?";

        List<TestRecord> results = queryHelper.executeQuery(
                sql,
                stmt -> stmt.setInt(1, 150),
                rs -> new TestRecord(rs.getInt("id"), rs.getString("name"), rs.getInt("value")));

        assertEquals(1, results.size());
        assertEquals("Test2", results.get(0).name());
        assertEquals(200, results.get(0).value());
    }

    @Test
    @DisplayName("Should execute query and return single optional result")
    void testExecuteQuerySingle() {
        String sql = "SELECT id, name, value FROM test_table WHERE id = ?";

        Optional<TestRecord> result = queryHelper.executeQuerySingle(
                sql,
                stmt -> stmt.setInt(1, 1),
                rs -> new TestRecord(rs.getInt("id"), rs.getString("name"), rs.getInt("value")));

        assertTrue(result.isPresent());
        assertEquals("Test1", result.get().name());
        assertEquals(100, result.get().value());
    }

    @Test
    @DisplayName("Should execute update and return affected row count")
    void testExecuteUpdate() {
        String sql = "UPDATE test_table SET value = ? WHERE id = ?";

        int affectedRows = queryHelper.executeUpdate(
                sql,
                stmt -> {
                    stmt.setInt(1, 500);
                    stmt.setInt(2, 1);
                });

        assertEquals(1, affectedRows);
    }

    @Test
    @DisplayName("Should execute count query and return integer result")
    void testExecuteCountQuery() {
        String sql = "SELECT COUNT(*) FROM test_table WHERE value > ?";

        int count = queryHelper.executeCountQuery(
                sql,
                stmt -> stmt.setInt(1, 150));

        assertEquals(1, count);
    }

    /**
     * Helper record class for testing, basically just used to hold test data as
     * records
     */
    private record TestRecord(int id, String name, int value) {
    }
}
