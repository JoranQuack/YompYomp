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
                new Trail(1, "Alpine Trail", "A beautiful alpine trail through the mountains", "Easy",
                        "2 hours", "thumb1.jpg", "http://example.com/trail1", 0.0, 0.0),
                new Trail(2, "Forest Trail", "A scenic forest trail with wildlife viewing", "Medium",
                        "3 hours", "thumb2.jpg", "http://example.com/trail2", 0.0, 0.0),
                new Trail(3, "Mountain Peak Trail", "Challenging trail to the mountain peak", "Hard",
                        "5 hours", "thumb3.jpg", "http://example.com/trail3", 0.0, 0.0),
                new Trail(4, "Coastal Walk", "Easy coastal walk with ocean views", "Easy",
                        "1.5 hours", "thumb4.jpg", "http://example.com/trail4", 0.0, 0.0),
                new Trail(5, "River Trail", "Trail following the river through the valley", "Medium",
                        "2.5 hours", "thumb5.jpg", "http://example.com/trail5", 0.0, 0.0));

        when(mockTrailRepo.getAllTrails()).thenReturn(mockTrails);

        searchService = new SearchService(mockTrailRepo);
        searchService.setMaxResults(20); // testing 20 results per page, but there are only 5 trails
    }

    @Test
    @DisplayName("Should return all of the trails (showing only 20 per page) if search query is empty")
    void testSearchTrailsEmptyQuery() {
        searchService.updateSearch("");
        List<Trail> trails = searchService.getPage(0);

        assertNotNull(trails);
        assertFalse(trails.isEmpty(), "Expected at least one trail in database");
        assertTrue(trails.size() <= 20, "Page should not exceed 20 trails");
        assertEquals(5, trails.size(), "Should return all 5 mock trails");
    }

    @Test
    @DisplayName("Should return trails independently of case")
    void testSearchTrailsCaseInsensitive() {
        searchService.updateSearch("trail");
        List<Trail> lowerCase = searchService.getPage(0);

        searchService.updateSearch("TRAIL");
        List<Trail> upperCase = searchService.getPage(0);

        searchService.updateSearch("Trail");
        List<Trail> mixedCase = searchService.getPage(0);

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
        searchService.updateSearch("nonexistent");
        List<Trail> trails = searchService.getPage(0);

        assertNotNull(trails);
        assertTrue(trails.isEmpty(), "Should return empty list when no trails match");
    }

    @Test
    @DisplayName("Should return correct number of pages")
    void testGetNumberOfPages() {
        searchService.setMaxResults(2);

        searchService.updateSearch("");
        int pages = searchService.getNumberOfPages();
        assertEquals(3, pages, "need 3 pages for 5 with 2 per page");

        searchService.updateSearch("trail");
        int pagesFiltered = searchService.getNumberOfPages();
        assertEquals(2, pagesFiltered, "need 2 pages for 4 with 2 per page");

        searchService.updateSearch("nonexistent");
        int pagesNoMatch = searchService.getNumberOfPages();
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
        searchService.updateSearch("");

        List<Trail> page1 = searchService.getPage(0);
        assertEquals(2, page1.size(), "First page should have 2 trails");

        List<Trail> page2 = searchService.getPage(1);
        assertEquals(2, page2.size(), "Second page should have 2 trails");

        List<Trail> page3 = searchService.getPage(2);
        assertEquals(1, page3.size(), "Third page should have 1 trail");

        assertNotEquals(page1.getFirst().getId(), page2.getFirst().getId(), "Pages should not overlap");
    }
}
