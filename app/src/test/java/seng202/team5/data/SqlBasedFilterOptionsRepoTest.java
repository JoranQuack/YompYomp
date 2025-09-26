package seng202.team5.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SqlBasedFilterOptionsRepo functionality.
 */
public class SqlBasedFilterOptionsRepoTest {
    private SqlBasedFilterOptionsRepo filterOptionsRepo;
    private DatabaseService databaseService;
    private String testDbPath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        testDbPath = tempDir.resolve("test.db").toString();
        databaseService = new DatabaseService(testDbPath);
        filterOptionsRepo = new SqlBasedFilterOptionsRepo(databaseService);

        // Use schema for test db
        databaseService.createDatabaseIfNotExists();

        // Add fake data
        try (Connection connection = databaseService.getConnection();
                Statement stmt = connection.createStatement()) {

            stmt.execute("""
                    INSERT INTO trail (id, name, difficulty, completionType, timeUnit, isMultiDay) VALUES
                    (1, 'Easy Trail', 'easy', 'walk', 'hours', 0),
                    (2, 'Hard Trail', 'expert', 'hike', 'days', 1),
                    (3, 'Medium Trail', 'intermediate', 'walk', 'hours', 0)
                    """);
        }
    }

    @AfterEach
    void tearDown() {
        File dbFile = new File(testDbPath);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    @DisplayName("Should refresh filter options from trail data hopefully")
    void testRefreshFilterOptions() {

        filterOptionsRepo.refreshAllFilterOptions();

        List<String> difficultyOptions = filterOptionsRepo.getFilterOptions("difficulty");
        assertTrue(difficultyOptions.contains("easy"));
        assertTrue(difficultyOptions.contains("expert"));
        assertTrue(difficultyOptions.contains("intermediate"));

        List<String> completionTypeOptions = filterOptionsRepo.getFilterOptions("completionType");
        assertTrue(completionTypeOptions.contains("walk"));
        assertTrue(completionTypeOptions.contains("hike"));

        List<String> multiDayOptions = filterOptionsRepo.getFilterOptions("multiDay");
        assertTrue(multiDayOptions.contains("Multi-day"));
        assertTrue(multiDayOptions.contains("Day walk"));
    }

    @Test
    @DisplayName("Should return empty list for unexisting filter types")
    void testGetFilterOptionsForNonExistentType() {
        filterOptionsRepo.refreshAllFilterOptions();

        List<String> options = filterOptionsRepo.getFilterOptions("nonexistent");
        assertTrue(options.isEmpty());
    }
}