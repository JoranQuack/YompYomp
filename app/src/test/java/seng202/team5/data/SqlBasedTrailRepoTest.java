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
    void testGetAllTrails() {
        List<Trail> trails = sqlBasedTrailRepo.getAllTrails();
        assertEquals(2, trails.size());
        assertEquals("Test1", trails.get(0).getName());
        assertEquals(2, trails.get(1).getId());
    }

    @Test
    @DisplayName("Should find correct trail from ID, and return empty when not found")
    void testFindByID() {
        assertTrue(sqlBasedTrailRepo.findById(9999).isEmpty());
        assertEquals("Test2", sqlBasedTrailRepo.findById(2).get().getName());
    }

    @Test
    @DisplayName("Should upsert one row into table correctly")
    void testUpsert() {
        Trail trail = new Trail.Builder()
                .id(3)
                .name("Test3")
                .description("Test Trail 3")
                .difficulty("Medium")
                .lat(0.0)
                .lon(0.0)
                .build();

        sqlBasedTrailRepo.upsert(trail);

        assertEquals(3, sqlBasedTrailRepo.countTrails());
        assertTrue(sqlBasedTrailRepo.findById(3).isPresent());
        assertEquals("Test3", sqlBasedTrailRepo.findById(3).get().getName());
    }


    @Test
    @DisplayName("Should upsert all of the list of trails")
    void testUpsertAll() throws MatchmakingFailedException {
        sqlBasedTrailRepo.upsertAll(List.of(
                new Trail.Builder().id(4).name("Test4").description("Test Trail 4").difficulty("Easy")
                        .lat(0.0).lon(0.0).build(),
                new Trail.Builder().id(5).name("Test5").description("Test Trail 5").difficulty("Medium")
                        .lat(0.0).lon(0.0).build(),
                new Trail.Builder().id(6).name("Test6").description("Test Trail 6").difficulty("Medium")
                        .lat(0.0).lon(0.0).build()
        ));

        assertEquals(5, sqlBasedTrailRepo.countTrails());
        assertTrue(sqlBasedTrailRepo.findById(5).isPresent());
    }


    @Test
    @DisplayName("Should delete a trail and ensure it is gone")
    void testDeleteByID() {
        sqlBasedTrailRepo.deleteById(1);
        assertEquals(1, sqlBasedTrailRepo.countTrails());
        assertTrue(sqlBasedTrailRepo.findById(1).isEmpty());
    }

    @Test
    @DisplayName("Should return the number of trails in database")
    void testCountTrails() {
        assertEquals(2, sqlBasedTrailRepo.countTrails());
        sqlBasedTrailRepo.deleteById(1);
        assertEquals(1, sqlBasedTrailRepo.countTrails());
    }

    //keeping this for now as we can use this in the new trail service when we merge with the testing branch

    @Test
    @DisplayName("Should return if trail is processed correctly")
    void testIsTrailProcessed() {
        Trail trail = new Trail.Builder()
                .id(3)
                .name("Test3")
                .description("Test Trail 3")
                .difficulty("Hard")
                .completionType("one way")
                .lat(0.0)
                .lon(0.0)
                .build();

        sqlBasedTrailRepo.upsert(trail);

        assertTrue(sqlBasedTrailRepo.isTrailProcessed(trail), "Trail 3 should be processed");

        Trail unprocessedTrail = new Trail.Builder()
                .id(9999)
                .name("Test9999")
                .description("Test Trail 9999")
                .difficulty("Hard")
                .lat(0.0)
                .lon(0.0)
                .build();

        assertFalse(sqlBasedTrailRepo.isTrailProcessed(unprocessedTrail), "Trail 9999 should not be processed");
    }


    @Test
    @DisplayName("Should insert trails that don't exist, and ignore those that do")
    void testInsertIfNotExists() {
        Trail existingTrail = new Trail.Builder()
                .id(1)
                .name("Test1")
                .description("Test Trail 1")
                .difficulty("Easy")
                .lat(0.0)
                .lon(0.0)
                .build();
        sqlBasedTrailRepo.insertOrIgnore(existingTrail);
        assertEquals(2, sqlBasedTrailRepo.countTrails(), "ID 1 already exists, count should remain 2");

        Trail newTrail = new Trail.Builder()
                .id(3)
                .name("Test3")
                .description("Test Trail 3")
                .difficulty("Easy")
                .lat(0.0)
                .lon(0.0)
                .build();
        sqlBasedTrailRepo.insertOrIgnore(newTrail);
        assertEquals(3, sqlBasedTrailRepo.countTrails(), "ID 3 is new, count should increase to 3");
    }


    @Test
    @DisplayName("Should update the user weights of trails correctly")
    void testUpdateUserWeights() {
        // Create initial trails
        Trail trail1 = new Trail.Builder()
                .id(100)
                .name("Test1")
                .description("Test Trail 1")
                .difficulty("Easy")
                .lat(0.0)
                .lon(0.0)
                .build();

        Trail trail2 = new Trail.Builder()
                .id(200)
                .name("Test2")
                .description("Test Trail 2")
                .difficulty("Medium")
                .lat(0.0)
                .lon(0.0)
                .build();

        sqlBasedTrailRepo.upsert(trail1);
        sqlBasedTrailRepo.upsert(trail2);

        Trail updatedTrail1 = new Trail.Builder().from(trail1).userWeight(0.8).build();
        Trail updatedTrail2 = new Trail.Builder().from(trail2).userWeight(0.5).build();

        sqlBasedTrailRepo.updateUserWeights(List.of(updatedTrail1, updatedTrail2));

        assertEquals(0.8, sqlBasedTrailRepo.findById(100).get().getUserWeight());
        assertEquals(0.5, sqlBasedTrailRepo.findById(200).get().getUserWeight());
    }


    @Test
    @DisplayName("Should clear all user weights")
    void testClearUserWeights() {
        assertEquals(0.8, sqlBasedTrailRepo.findById(1).get().getUserWeight());
        assertEquals(0.5, sqlBasedTrailRepo.findById(2).get().getUserWeight());

        sqlBasedTrailRepo.clearUserWeights();

        double weight1 = sqlBasedTrailRepo.findById(1).get().getUserWeight();
        double weight2 = sqlBasedTrailRepo.findById(2).get().getUserWeight();
        assertEquals(0.0, weight1, "User weight should be reset to 0.0");
        assertEquals(0.0, weight2, "User weight should be reset to 0.0");
    }

    @Test
    @DisplayName("Should insert all trails that don't exist, and ignore those that do")
    void testInsertOrIgnoreAll() throws MatchmakingFailedException {
        List<Trail> trails = List.of(
                new Trail.Builder()
                        .id(1)
                        .name("Test1")
                        .description("Test Trail 1")
                        .difficulty("Easy")
                        .lat(0.0)
                        .lon(0.0)
                        .build(),

                new Trail.Builder()
                        .id(3)
                        .name("Test3")
                        .description("Test Trail 3")
                        .difficulty("Medium")
                        .lat(0.0)
                        .lon(0.0)
                        .build(),

                new Trail.Builder()
                        .id(4)
                        .name("Test4")
                        .description("Test Trail 4")
                        .difficulty("Hard")
                        .lat(0.0)
                        .lon(0.0)
                        .build()
        );

        sqlBasedTrailRepo.insertOrIgnoreAll(trails);

        assertEquals(4, sqlBasedTrailRepo.countTrails());
        assertTrue(sqlBasedTrailRepo.findById(3).isPresent());
        assertTrue(sqlBasedTrailRepo.findById(4).isPresent());
    }


    @Test
    @DisplayName("Should throw exception when inserting empty list with insertOrIgnoreAll")
    void testInsertOrIgnoreAllEmptyList() {
        assertThrows(MatchmakingFailedException.class, () -> sqlBasedTrailRepo.insertOrIgnoreAll(List.of()));
    }

    @Test
    @DisplayName("Should check if trail name exists correctly")
    void testExistsByName() {
        assertTrue(sqlBasedTrailRepo.existsByName("Test1", null));
        assertTrue(sqlBasedTrailRepo.existsByName("test1", null)); // Case-insensitive
        assertTrue(sqlBasedTrailRepo.existsByName("  Test1  ", null)); // Whitespace insensitive

        assertFalse(sqlBasedTrailRepo.existsByName("NonExistentTrail", null));

        // Exclusion
        assertFalse(sqlBasedTrailRepo.existsByName("Test1", 1));
        assertTrue(sqlBasedTrailRepo.existsByName("Test1", 2));
    }

    @Test
    @DisplayName("Should return correct new trail ID")
    void testGetNewTrailId() {
        // Assuming sqlBasedTrailRepo already contains trails with IDs 1 and 2
        assertEquals(3, sqlBasedTrailRepo.getNewTrailId());

        Trail trail5 = new Trail.Builder()
                .id(5)
                .name("Test5")
                .description("Test Trail 5")
                .difficulty("Easy")
                .lat(0.0)
                .lon(0.0)
                .build();

        sqlBasedTrailRepo.upsert(trail5);

        assertEquals(6, sqlBasedTrailRepo.getNewTrailId());
    }

}
