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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SqlBasedFilterOptionsRepo functionality.
 */
public class SqlBasedFilterOptionsRepoTest {
    private SqlBasedFilterOptionsRepo filterOptionsRepo;
    private String testDbPath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        testDbPath = tempDir.resolve("test.db").toString();
        DatabaseService databaseService = new DatabaseService(testDbPath);
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

    @Test
    @DisplayName("Should correctly identify if a filterType has options")
    void testHasFilterOptions() {
        filterOptionsRepo.refreshAllFilterOptions();

        assertTrue(filterOptionsRepo.hasFilterOptions("difficulty"), "Difficulty should have options");
        assertTrue(filterOptionsRepo.hasFilterOptions("completionType"), "CompletionType should have options");
        assertTrue(filterOptionsRepo.hasFilterOptions("multiDay"), "MultiDay should have options");
    }

    @Test
    @DisplayName("Should get all the available types")
    void testGetAllFilterOptions() {
        filterOptionsRepo.refreshAllFilterOptions();

        List<String> allTypes = filterOptionsRepo.getAvailableFilterTypes();
        assertTrue(allTypes.contains("difficulty"), "Should contain difficulty");
        assertTrue(allTypes.contains("completionType"), "Should contain completionType");
        assertTrue(allTypes.contains("multiDay"), "Should contain multiDay");
        assertEquals(5, allTypes.size(), "Should only contain the 5 known types");
    }

    @Test
    @DisplayName("Should return the correct map for filter options")
    void testGetFilterOptionsMap() {
        filterOptionsRepo.refreshAllFilterOptions();

        Map<String, List<String>> optionsMap = filterOptionsRepo
                .getFilterOptionsMap(List.of("difficulty", "completionType", "multiDay"));

        assertTrue(optionsMap.containsKey("difficulty"), "Map should contain difficulty");
        assertTrue(optionsMap.containsKey("completionType"), "Map should contain completionType");
        assertTrue(optionsMap.containsKey("multiDay"), "Map should contain multiDay");
        assertEquals(3, optionsMap.size(), "Map should only contain the three known types");

        assertTrue(optionsMap.get("difficulty").contains("easy"), "Difficulty should contain easy");
        assertTrue(optionsMap.get("completionType").contains("walk"), "CompletionType should contain walk");
        assertTrue(optionsMap.get("multiDay").contains("Multi-day"), "MultiDay should contain Multi-day");
    }
}