package seng202.team5.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.Trail;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlBasedTrailRepoTest {

    private SqlBasedTrailRepo sqlBasedTrailRepo;
    private DatabaseService databaseService;
    private String testDbPath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        testDbPath = tempDir.resolve("test.db").toString();
        databaseService = new DatabaseService(testDbPath);
        sqlBasedTrailRepo = new SqlBasedTrailRepo(databaseService);

        // Create a 'Trail' table to test with
        try (Connection connection = databaseService.getConnection();
                Statement stmt = connection.createStatement()) {
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS trail (
                          id INTEGER PRIMARY KEY,
                          name TEXT NOT NULL,
                          translation TEXT,
                          region TEXT,
                          description TEXT,
                          difficulty TEXT,
                          completionInfo TEXT,
                          minCompletionTimeMinutes INTEGER,
                          maxCompletionTimeMinutes INTEGER,
                          completionType TEXT,
                          timeUnit TEXT,
                          isMultiDay BOOL,
                          hasVariableTime BOOL,
                          thumbUrl TEXT,
                          webUrl TEXT,
                          cultureUrl TEXT,
                          userWeight REAL,
                          lat REAL,
                          lon REAL
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS trailCategory (
                          id INTEGER PRIMARY KEY,
                          trailId INTEGER,
                          categoryId INTEGER,
                          FOREIGN KEY (trailId) REFERENCES trail(id)
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS trailLog (
                          id INTEGER PRIMARY KEY,
                          trailId INTEGER,
                          startDate TEXT,
                          completionTime INTEGER,
                          timeUnit TEXT,
                          completionType TEXT,
                          rating INTEGER,
                          perceivedDifficulty TEXT,
                          notes TEXT,
                          FOREIGN KEY (trailId) REFERENCES trail(id)
                        )
                    """);

            stmt.execute("DELETE FROM trail");
            stmt.execute("DELETE FROM trailCategory");
            stmt.execute("DELETE FROM trailLog");
            stmt.execute(
                    "INSERT INTO trail (id, name, description, difficulty, userWeight) VALUES (1, 'Test1', 'Test Trail 1', 'Hard', 0.8)");
            stmt.execute(
                    "INSERT INTO trail (id, name, description, difficulty, userWeight) VALUES (2, 'Test2', 'Test Trail 2', 'Easy', 0.5)");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Get rid of tables after testing
        try (Connection conn = databaseService.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS trailCategory");
            stmt.execute("DROP TABLE IF EXISTS trailLog");
            stmt.execute("DROP TABLE IF EXISTS trail");
        }

        File dbFile = new File(testDbPath);
        if (dbFile.exists()) {
            dbFile.delete();
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
        sqlBasedTrailRepo.upsert(new Trail(3, "Test3", "Test Trail 3", "Medium", null, null, null, 0.0, 0.0));
        assertEquals(3, sqlBasedTrailRepo.countTrails());
        assertTrue(sqlBasedTrailRepo.findById(3).isPresent());
        assertEquals("Test3", sqlBasedTrailRepo.findById(3).get().getName());
    }

    @Test
    @DisplayName("Should upsert all of the list of trails")
    void testUpsertAll() throws SQLException, MatchmakingFailedException {
        sqlBasedTrailRepo.upsertAll(List.of(
                new Trail(4, "Test4", "Test Trail 4", "Easy", null, null, null, 0.0, 0.0),
                new Trail(5, "Test5", "Test Trail 5", "Medium", null, null, null, 0.0, 0.0),
                new Trail(6, "Test6", "Test Trail 6", "Medium", null, null, null, 0.0, 0.0)));
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

    @Test
    @DisplayName("Should return the correct recommended trails")
    void testGetRecommendedTrails() throws SQLException, MatchmakingFailedException {
        List<Trail> recommended = sqlBasedTrailRepo.getRecommendedTrails();
        assertEquals(2, recommended.size());
        assertTrue(recommended.get(0).getId() == 1);
    }

    @Test
    @DisplayName("Should return if trail is processed correctly")
    void testIsTrailProcessed() throws SQLException {
        Trail trail = new Trail(3, "Test3", "Test Trail 3", "Hard", null, null, null, 0.0, 0.0);
        trail.setCompletionType("one way");
        sqlBasedTrailRepo.upsert(trail);
        assertTrue(sqlBasedTrailRepo.isTrailProcessed(trail));
        assertFalse(sqlBasedTrailRepo
                .isTrailProcessed(new Trail(9999, "Test9999", "Test Trail 9999", "Hard", null, null, null, 0.0, 0.0)));
    }

    @Test
    @DisplayName("Should insert trails that don't exist, and ignore those that do")
    void testInsertIfNotExists() throws SQLException {
        sqlBasedTrailRepo.insertOrIgnore(new Trail(1, "Test1", "Test Trail 1", "Easy", null, null, null, 0.0, 0.0));
        assertEquals(2, sqlBasedTrailRepo.countTrails()); // Should remain 2 as ID 1 already exists

        sqlBasedTrailRepo.insertOrIgnore(new Trail(3, "Test3", "Test Trail 3", "Easy", null, null, null, 0.0, 0.0));
        assertEquals(3, sqlBasedTrailRepo.countTrails()); // Should be 3 as ID 3 is new
    }

    @Test
    @DisplayName("Should update the user weights of trails correctly")
    void testUpdateUserWeights() throws SQLException {
        Trail trail1 = new Trail(100, "Test1", "Test Trail 1", "Easy", null, null, null, 0.0, 0.0);
        Trail trail2 = new Trail(200, "Test2", "Test Trail 2", "Medium", null, null, null, 0.0, 0.0);
        sqlBasedTrailRepo.upsert(trail1);
        sqlBasedTrailRepo.upsert(trail2);
        trail1.setUserWeight(0.8);
        trail2.setUserWeight(0.5);
        sqlBasedTrailRepo.updateUserWeights(List.of(trail1, trail2));
        assertEquals(0.8, sqlBasedTrailRepo.findById(100).get().getUserWeight());
        assertEquals(0.5, sqlBasedTrailRepo.findById(200).get().getUserWeight());
    }

    @Test
    @DisplayName("Should clear all user weights")
    void testClearUserWeights() throws SQLException {
        assertEquals(0.8, sqlBasedTrailRepo.findById(1).get().getUserWeight());
        assertEquals(0.5, sqlBasedTrailRepo.findById(2).get().getUserWeight());

        sqlBasedTrailRepo.clearUserWeights();

        double weight1 = sqlBasedTrailRepo.findById(1).get().getUserWeight();
        double weight2 = sqlBasedTrailRepo.findById(2).get().getUserWeight();
        assertTrue(weight1 == 0.0, "User weight should be reset to 0.0");
        assertTrue(weight2 == 0.0, "User weight should be reset to 0.0");
    }

    @Test
    @DisplayName("Should insert all trails that don't exist, and ignore those that do")
    void testInsertOrIgnoreAll() throws SQLException, MatchmakingFailedException {
        List<Trail> trails = List.of(
                new Trail(1, "Test1", "Test Trail 1", "Easy", null, null, null, 0.0, 0.0), // ignored (exists)
                new Trail(3, "Test3", "Test Trail 3", "Medium", null, null, null, 0.0, 0.0), // inserted
                new Trail(4, "Test4", "Test Trail 4", "Hard", null, null, null, 0.0, 0.0) // inserted
        );

        sqlBasedTrailRepo.insertOrIgnoreAll(trails);
        assertEquals(4, sqlBasedTrailRepo.countTrails());
        assertTrue(sqlBasedTrailRepo.findById(3).isPresent());
        assertTrue(sqlBasedTrailRepo.findById(4).isPresent());
    }

    @Test
    @DisplayName("Should throw exception when inserting empty list with insertOrIgnoreAll")
    void testInsertOrIgnoreAllEmptyList() {
        assertThrows(MatchmakingFailedException.class, () -> {
            sqlBasedTrailRepo.insertOrIgnoreAll(List.of());
        });
    }

    @Test
    @DisplayName("Should check if trail name exists correctly")
    void testExistsByName() throws SQLException {
        assertTrue(sqlBasedTrailRepo.existsByName("Test1", null));
        assertTrue(sqlBasedTrailRepo.existsByName("test1", null)); // Case insensitive
        assertTrue(sqlBasedTrailRepo.existsByName("  Test1  ", null)); // Whitespace insensitive

        assertFalse(sqlBasedTrailRepo.existsByName("NonExistentTrail", null));

        // Exclusion
        assertFalse(sqlBasedTrailRepo.existsByName("Test1", 1));
        assertTrue(sqlBasedTrailRepo.existsByName("Test1", 2));
    }

    @Test
    @DisplayName("Should return correct new trail ID")
    void testGetNewTrailId() throws SQLException {
        assertEquals(3, sqlBasedTrailRepo.getNewTrailId());

        // Birth trail with ID 5 and make sure 6 is selected as tribute
        sqlBasedTrailRepo.upsert(new Trail(5, "Test5", "Test Trail 5", "Easy", null, null, null, 0.0, 0.0));
        assertEquals(6, sqlBasedTrailRepo.getNewTrailId());
    }
}
