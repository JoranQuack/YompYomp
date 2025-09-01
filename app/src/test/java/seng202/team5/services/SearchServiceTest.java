package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Trail;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class SearchServiceTest {

    private SearchService searchService;
    private final String testDbPath = "src/test/resources/database/test.db"; //for the database aspect of this, need to implement

    @BeforeEach
    void setUp() {
        searchService = new SearchService();
    }

    @Test
    @DisplayName("Should return all of the trails (showing only 20 per page) if search query is empty")
    void testSearchTrailsEmptyQuery() {
        List<Trail> trails = searchService.getTrails("", 0);

        assertNotNull(trails);
        assertFalse(trails.isEmpty(), "Expected at least one trail in database");
        assertTrue(trails.size() <= 20, "Page should not exceed 20 trails");
    }

    @Test
    @DisplayName("Should return trails independently of case")
    void testSearchTrailsCaseInsensitive() {
        List<Trail> lowerCase = searchService.getTrails("trail", 0);
        List<Trail> upperCase = searchService.getTrails("TRAIL", 0);
        List<Trail> mixedCase = searchService.getTrails("Trail", 0);

        assertEquals(lowerCase.size(), upperCase.size(), "Case-insensitive search should return same results");
        assertEquals(lowerCase.size(), mixedCase.size(), "Should not matter if whole word is different case or just a character");

        for (Trail trail : lowerCase) {
            assertTrue(trail.getName().toLowerCase().contains("trail"));
        }
    }
}
