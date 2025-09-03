package seng202.team5.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Trail;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlBasedTrailRepoTest {

    private SqlBasedTrailRepo sqlBasedTrailRepo;
    private DatabaseService databaseService;
    private String testDbPath;

    @BeforeEach
    void setUp() throws Exception {
        try {
            testDbPath = TestPathHelper.getTestResourcePath("database/test.db");
            databaseService = new DatabaseService(testDbPath);
        } catch (Exception e) {
            // Fallback to in-memory database for CI environments
            System.err.println(
                    "Warning: Could not find test database file, using in-memory database. Error: " + e.getMessage());
            testDbPath = ":memory:";
            databaseService = new DatabaseService(testDbPath);
        }
        sqlBasedTrailRepo = new SqlBasedTrailRepo(databaseService);

        // Create a 'Trail' table to test with
        try (Connection connection = databaseService.getConnection();
                Statement stmt = connection.createStatement()) {
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS trail (
                          id INTEGER PRIMARY KEY,
                          name TEXT NOT NULL,
                          description TEXT,
                          difficulty TEXT,
                          completion_time TEXT,
                          type TEXT,
                          thumb_url TEXT,
                          web_url TEXT,
                          date_loaded_raw TEXT,
                          x REAL,
                          y REAL
                        )
                    """);
            stmt.execute("DELETE FROM trail");
            stmt.execute(
                    "INSERT INTO trail (id, name, description, difficulty) VALUES (1, 'Test1', 'Test Trail 1', 'Hard')");
            stmt.execute(
                    "INSERT INTO trail (id, name, description, difficulty) VALUES (2, 'Test2', 'Test Trail 2', 'Easy')");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Get rid of table after testing
        try (Connection conn = databaseService.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS trail");
        }
    }

    @Test
    @DisplayName("Should return all trails in the test table")
    void testGetAllTrails() throws SQLException {
        List<Trail> trails = sqlBasedTrailRepo.getAllTrails();
        assertEquals(2, trails.size());
        assertEquals("Test1", trails.get(0).getName());
        assertEquals(2, trails.get(1).getId());
    }

    @Test
    @DisplayName("Should find correct trail from ID, and return empty when not found")
    void testFindByID() throws SQLException {
        assertTrue(sqlBasedTrailRepo.findById(9999).isEmpty());
        assertEquals("Test2", sqlBasedTrailRepo.findById(2).get().getName());
    }

    @Test
    @DisplayName("Should upsert one row into table correctly")
    void testUpsert() throws SQLException {
        sqlBasedTrailRepo.upsert(new Trail(3, "Test3", "Test Trail 3", "Medium",
                null, null, null, null, null, 0, 0));
        assertEquals(3, sqlBasedTrailRepo.countTrails());
        assertTrue(sqlBasedTrailRepo.findById(3).isPresent());
        assertEquals("Test3", sqlBasedTrailRepo.findById(3).get().getName());
    }

    @Test
    @DisplayName("Should upsert all of the list of trails")
    void testUpsertAll() throws SQLException {
        sqlBasedTrailRepo.upsertAll(List.of(
                new Trail(4, "Test4", "Test Trail 4", "Easy",
                        null, null, null, null, null, 0, 0),
                new Trail(5, "Test5", "Test Trail 5", "Medium",
                        null, null, null, null, null, 0, 0),
                new Trail(6, "Test6", "Test Trail 6", "Medium",
                        null, null, null, null, null, 0, 0)));
        assertEquals(5, sqlBasedTrailRepo.countTrails());
        assertTrue(sqlBasedTrailRepo.findById(5).isPresent());
    }

    @Test
    @DisplayName("Should delete a trail and ensure it is gone")
    void testDeleteByID() throws SQLException {
        sqlBasedTrailRepo.deleteById(1);
        assertEquals(1, sqlBasedTrailRepo.countTrails());
        assertTrue(sqlBasedTrailRepo.findById(1).isEmpty());
    }

    @Test
    @DisplayName("Should return the number of trails in database")
    void testCountTrails() throws SQLException {
        assertEquals(2, sqlBasedTrailRepo.countTrails());
        sqlBasedTrailRepo.deleteById(1);
        assertEquals(1, sqlBasedTrailRepo.countTrails());
    }
}
