package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seng202.team5.data.DataService;
import seng202.team5.models.Trail;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class SearchServiceTest {

    private SearchService searchService;
    private DataService dataService;

    @BeforeEach
    void setUp() {
        searchService = new SearchService(dataService);
    }

//    @Test
//    @DisplayName("Should return all of the trails (showing only 20 per page) if search query is empty")
//    void testSearchTrailsEmptyQuery() {
//        List<Trail> trails = searchService.searchTrails("", 0);
//
//        assertNotNull(trails);
//        assertFalse(trails.isEmpty());
//        assertTrue(trails.size() <= 20);
//    }
//
//    @Test
//    @DisplayName("Should return trails independently of case")
//    void testSearchTrailsCaseInsensitive() {
//        List<Trail> lowerCase = searchService.searchTrails("trail", 0);
//        List<Trail> upperCase = searchService.searchTrails("TRAIL", 0);
//        List<Trail> mixedCase = searchService.searchTrails("Trail", 0);
//
//        assertEquals(lowerCase.size(), upperCase.size());
//        assertEquals(lowerCase.size(), mixedCase.size());
//
//        for (Trail trail : lowerCase) {
//            assertTrue(trail.getName().toLowerCase().contains("trail"));
//        }
//    } will fix
}
