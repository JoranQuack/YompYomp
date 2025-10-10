package seng202.team5.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                    "INSERT INTO trailLog (id, trailId, startDate, completionTime) VALUES (1, 99, '9/10/2025', 2)");
            stmt.execute(
                    "INSERT INTO trailLog (id, trailId, startDate, completionTime) VALUES (2, 100, '10/10/2025', 1)");
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
}
