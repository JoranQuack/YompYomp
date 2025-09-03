package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

public class SearchServiceTest {

    @Mock
    private SqlBasedTrailRepo mockTrailRepo;

    private SearchService searchService;
    private List<Trail> mockTrails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // mock trail data
        mockTrails = Arrays.asList(
                new Trail(1, "Alpine Trail", "Easy", "A beautiful alpine trail through the mountains",
                        "2 hours", "Walking", "thumb1.jpg", "http://example.com/trail1",
                        "2024-01-01", 123.45, 67.89),
                new Trail(2, "Forest Trail", "Medium", "A scenic forest trail with wildlife viewing",
                        "3 hours", "Walking", "thumb2.jpg", "http://example.com/trail2",
                        "2024-01-02", 234.56, 78.90),
                new Trail(3, "Mountain Peak Trail", "Hard", "Challenging trail to the mountain peak",
                        "5 hours", "Hiking", "thumb3.jpg", "http://example.com/trail3",
                        "2024-01-03", 345.67, 89.01),
                new Trail(4, "Coastal Walk", "Easy", "Easy coastal walk with ocean views",
                        "1.5 hours", "Walking", "thumb4.jpg", "http://example.com/trail4",
                        "2024-01-04", 456.78, 90.12),
                new Trail(5, "River Trail", "Medium", "Trail following the river through the valley",
                        "2.5 hours", "Walking", "thumb5.jpg", "http://example.com/trail5",
                        "2024-01-05", 567.89, 101.23));

        when(mockTrailRepo.getAllTrails()).thenReturn(mockTrails);

        searchService = new SearchService(mockTrailRepo);
        searchService.setMaxResults(20); // testing 20 results per page, but there are only 5 trails
    }

    @Test
    @DisplayName("Should return all of the trails (showing only 20 per page) if search query is empty")
    void testSearchTrailsEmptyQuery() {
        List<Trail> trails = searchService.getTrails("", 0);

        assertNotNull(trails);
        assertFalse(trails.isEmpty(), "Expected at least one trail in database");
        assertTrue(trails.size() <= 20, "Page should not exceed 20 trails");
        assertEquals(5, trails.size(), "Should return all 5 mock trails");
    }

    @Test
    @DisplayName("Should return trails independently of case")
    void testSearchTrailsCaseInsensitive() {
        List<Trail> lowerCase = searchService.getTrails("trail", 0);
        List<Trail> upperCase = searchService.getTrails("TRAIL", 0);
        List<Trail> mixedCase = searchService.getTrails("Trail", 0);

        assertEquals(lowerCase.size(), upperCase.size(), "Case-insensitive search should return same results");
        assertEquals(lowerCase.size(), mixedCase.size(),
                "Should not matter if whole word is different case or just a character");

        for (Trail trail : lowerCase) {
            assertTrue(trail.getName().toLowerCase().contains("trail"));
        }

        assertEquals(4, lowerCase.size(), "Should only find 4 trails with 'trail' in the name");
    }

    @Test
    @DisplayName("Should return empty list when no trails match search query")
    void testSearchTrailsNoMatches() {
        List<Trail> trails = searchService.getTrails("nonexistent", 0);

        assertNotNull(trails);
        assertTrue(trails.isEmpty(), "Should return empty list when no trails match");
    }

    @Test
    @DisplayName("Should return correct number of pages")
    void testGetNumberOfPages() {
        searchService.setMaxResults(2);

        int pages = searchService.getNumberOfPages("");
        assertEquals(3, pages, "need 3 pages for 5 with 2 per page");

        int pagesFiltered = searchService.getNumberOfPages("trail");
        assertEquals(2, pagesFiltered, "need 2 pages for 4 with 2 per page");

        int pagesNoMatch = searchService.getNumberOfPages("nonexistent");
        assertEquals(0, pagesNoMatch, "need 0 pages when no trails match");
    }

    @Test
    @DisplayName("Should return correct total number of trails")
    void testGetNumberOfTrails() {
        int totalTrails = searchService.getNumberOfTrails();
        assertEquals(5, totalTrails, "Should return total of 5 mock trails");
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void testPagination() {
        searchService.setMaxResults(2);

        List<Trail> page1 = searchService.getTrails("", 0);
        assertEquals(2, page1.size(), "First page should have 2 trails");

        List<Trail> page2 = searchService.getTrails("", 1);
        assertEquals(2, page2.size(), "Second page should have 2 trails");

        List<Trail> page3 = searchService.getTrails("", 2);
        assertEquals(1, page3.size(), "Third page should have 1 trail");

        assertNotEquals(page1.getFirst().getId(), page2.getFirst().getId(), "Pages should not overlap");
    }
}
