package seng202.team5.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.TrailLog;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlBasedTrailLogRepoTest {

    private SqlBasedTrailLogRepo sqlBasedTrailLogRepo;
    private DatabaseService databaseService;
    private String testDbPath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        testDbPath = tempDir.resolve("test.db").toString();
        databaseService = new DatabaseService(testDbPath);
        sqlBasedTrailLogRepo = new SqlBasedTrailLogRepo(databaseService);

        // Create a 'Trail' table to test with
        try (Connection connection = databaseService.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS trail (
                          id INTEGER PRIMARY KEY,
                          name TEXT,
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
                    "INSERT INTO trail (id) VALUES (99)");
            stmt.execute(
                    "INSERT INTO trail (id) VALUES (100)");
            stmt.execute(
                    "INSERT INTO trail (id) VALUES (101)");
            stmt.execute(
                    "INSERT INTO trail (id) VALUES (102)");
            stmt.execute(
                    "INSERT INTO trail (id) VALUES (103)");
            stmt.execute(
                    "INSERT INTO trail (id) VALUES (104)");
            stmt.execute(
                    "INSERT INTO trailLog (id, trailId, startDate, completionTime) VALUES (1, 99, '2025-10-09', 2)");
            stmt.execute(
                    "INSERT INTO trailLog (id, trailId, startDate, completionTime) VALUES (2, 100, '2025-10-09', 1)");

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
    @DisplayName("Should return all logs in the test table")
    void testGetAllTrails() {
        List<TrailLog> logs = sqlBasedTrailLogRepo.getAllTrailLogs();
        assertEquals(2, logs.size());
        assertEquals(99, logs.get(0).getTrailId());
        assertEquals(100, logs.get(1).getTrailId());
    }

    @Test
    @DisplayName("Should find correct trail from ID, and return empty when not found")
    void testFindByID() {
        assertTrue(sqlBasedTrailLogRepo.findById(9999).isEmpty());
        assertEquals(100, sqlBasedTrailLogRepo.findById(2).get().getTrailId());
    }

    @Test
    @DisplayName("Should upsert one row into table correctly")
    void testUpsert() {
        TrailLog log = new TrailLog(
                3, 101,
                LocalDate.of(2025, 10, 9), 120,
                "minutes", "Loop", 5,
                "Easy", "Nice easy hike around the lake"
        );


        sqlBasedTrailLogRepo.upsert(log);

        assertEquals(3, sqlBasedTrailLogRepo.countTrailLogs());
        assertTrue(sqlBasedTrailLogRepo.findById(3).isPresent());
        assertEquals(101, sqlBasedTrailLogRepo.findById(3).get().getTrailId());
    }

    @Test
    @DisplayName("Should upsert all of the list of logs")
    void testUpsertAll() throws MatchmakingFailedException {
        sqlBasedTrailLogRepo.upsertAll(List.of(
                new TrailLog(4, 102, LocalDate.of(2025, 10, 1), 90, "minutes", "Loop", 5, "Easy", "Smooth scenic track."),
                new TrailLog(5, 103, LocalDate.of(2025, 10, 2), 120, "minutes", "One way", 4, "Medium", "Mild incline with great views."),
                new TrailLog(6, 104, LocalDate.of(2025, 10, 3), 150, "minutes", "Loop", 3, "Hard", "Challenging terrain, rocky in places.")
        ));


        assertEquals(5, sqlBasedTrailLogRepo.countTrailLogs());
        assertTrue(sqlBasedTrailLogRepo.findById(5).isPresent());
    }


    @Test
    @DisplayName("Should delete a log and ensure it is gone")
    void testDeleteByID() {
        sqlBasedTrailLogRepo.deleteById(1);
        assertEquals(1, sqlBasedTrailLogRepo.countTrailLogs());
        assertTrue(sqlBasedTrailLogRepo.findById(1).isEmpty());
    }

    @Test
    @DisplayName("Should return the number of logs in database")
    void testCountLogs() {
        assertEquals(2, sqlBasedTrailLogRepo.countTrailLogs());
        sqlBasedTrailLogRepo.deleteById(1);
        assertEquals(1, sqlBasedTrailLogRepo.countTrailLogs());
    }

    @Test
    @DisplayName("Should return the next id that a log can be assigned to")
    void testGetNewTrailLogId() {
        // Assuming sqlBasedTrailLogRepo already contains logs with IDs 1 and 2
        assertEquals(3, sqlBasedTrailLogRepo.getNewTrailLogId());

        TrailLog log = new TrailLog(5, 102, LocalDate.of(2025, 10, 1), 90, "minutes", "Loop", 5, "Easy", "Smooth scenic track.");


        sqlBasedTrailLogRepo.upsert(log);

        assertEquals(6, sqlBasedTrailLogRepo.getNewTrailLogId());
    }

    @Test
    @DisplayName("Find trial log for given trial id")
    void testFindByTrailId() {
        assertEquals(2, sqlBasedTrailLogRepo.findByTrailId(100).get().getId());
    }
}


