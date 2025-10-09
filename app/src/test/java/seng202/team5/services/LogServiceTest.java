package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LogServiceTest {

    @Mock
    private SqlBasedTrailLogRepo mockLogInterface;
    @Mock
    private SqlBasedTrailRepo mockTrailRepo;

    private LogService logService;
    private List<TrailLog> mockLogs;
    private List<Trail> mockTrails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockLogs = new java.util.ArrayList<>(Arrays.asList(
                new TrailLog(1, 703975, LocalDate.of(2025, 10, 1), 120, "minutes", "One way", 4, "Easiest",
                        "Nice trail, steady incline."),
                new TrailLog(2, 703976, LocalDate.of(2025, 10, 2), 90, "minutes", "Loop", 5, "Easy",
                        "Great for beginners."),
                new TrailLog(3, 703977, LocalDate.of(2025, 10, 3), 200, "minutes", "Loop", 3, "Expert",
                        "Challenging terrain."),
                new TrailLog(4, 703978, LocalDate.of(2025, 10, 4), 150, "minutes", "Loop", 4, "Advanced",
                        "Beautiful views."),
                new TrailLog(5, 703979, LocalDate.of(2025, 10, 5), 2, "hours", "One way", 2, "Easy",
                        "Turned back due to weather."),
                new TrailLog(6, 703980, LocalDate.of(2025, 10, 6), 110, "minutes", "Loop", 5, "Easy",
                        "Smooth trail, very enjoyable."),
                new TrailLog(7, 703981, LocalDate.of(2025, 10, 7), 3, "hours", "Loop", 4, "Expert",
                        "Tough climb but rewarding."),
                new TrailLog(8, 703982, LocalDate.of(2025, 10, 8), 1, "day", "Loop", 4, "Advanced",
                        "Perfect morning hike."),
                new TrailLog(9, 703940, LocalDate.of(2025, 10, 9), 130, "minutes", "Loop", 3, "Easiest",
                        "Some muddy sections."),
                new TrailLog(10, 703947, LocalDate.of(2025, 10, 10), 160, "minutes", "One way", 5, "Advanced",
                        "One of the best trails so far.")));

        mockTrails = Arrays.asList(
                new Trail(703975, "Port Hills Track", "Te Ara o ngā Maunga", "Canterbury", "Easiest",
                        "One way", "Well-marked trail with gentle incline", "Great introductory hike for beginners.",
                        "thumb_ph.jpg", "https://example.com/porthills", "https://example.com/culture/ph",
                        4.5, -43.5850, 172.6750),

                new Trail(703976, "Godley Head Loop", "Te Ara o Rāpaki", "Canterbury", "Easy",
                        "Loop", "Gentle loop along coastal cliffs", "Ideal for casual walkers and families.",
                        "thumb_gh.jpg", "https://example.com/godleyhead", "https://example.com/culture/gh",
                        4.7, -43.5861, 172.7822),

                new Trail(703977, "Mount Herbert Summit", "Te Tihi o Te Ahu Pātiki", "Canterbury", "Expert",
                        "Loop", "Steep climb with exposed sections",
                        "Longest trail on the list, for experienced hikers.",
                        "thumb_mh.jpg", "https://example.com/mtherbert", "https://example.com/culture/mh",
                        4.9, -43.7160, 172.6500),

                new Trail(703978, "Bridle Path", "Te Ara a Hine", "Canterbury", "Advanced",
                        "Loop", "Historic track between Lyttelton and Christchurch",
                        "Challenging gradient, popular training route.",
                        "thumb_bp.jpg", "https://example.com/bridlepath", "https://example.com/culture/bp",
                        4.4, -43.6000, 172.7300),

                new Trail(703979, "Rapaki Track", "Te Ara Rāpaki", "Canterbury", "Easy",
                        "One way", "Gradual incline with open views", "Turned back due to weather conditions.",
                        "thumb_rt.jpg", "https://example.com/rapaki", "https://example.com/culture/rt",
                        4.3, -43.6005, 172.6602),

                new Trail(703980, "Hinewai Reserve Loop", "Te Wao o Hinewai", "Banks Peninsula", "Easy",
                        "Loop", "Regenerating native forest walk", "Scenic and relaxing with clear signage.",
                        "thumb_hw.jpg", "https://example.com/hinewai", "https://example.com/culture/hw",
                        4.8, -43.7800, 172.9800),

                new Trail(703981, "Cass-Lagoon Saddle", "Te Ara o Kāwhiu", "Arthur’s Pass", "Expert",
                        "Loop", "Alpine hike with steep ascents", "Multi-day option available for trampers.",
                        "thumb_cl.jpg", "https://example.com/casslagoon", "https://example.com/culture/cl",
                        4.6, -42.9500, 171.7100),

                new Trail(703982, "Rakaia Gorge Walkway", "Te Ara o Rakaia", "Canterbury", "Advanced",
                        "Loop", "Follows the river terraces and native bush", "Excellent day hike for fit walkers.",
                        "thumb_rg.jpg", "https://example.com/rakaiagorge", "https://example.com/culture/rg",
                        4.5, -43.4800, 171.8300),

                new Trail(703940, "Victoria Park Track", "Te Ara o Whero", "Christchurch", "Easiest",
                        "Loop", "Short scenic trail through forest park", "Some muddy sections after rain.",
                        "thumb_vp.jpg", "https://example.com/victoriapark", "https://example.com/culture/vp",
                        4.2, -43.5790, 172.6400),

                new Trail(703947, "Packhorse Hut Route", "Te Ara o te Pōkai", "Canterbury", "Advanced",
                        "One way", "Steady climb to historic hut", "Excellent views and well-maintained track.",
                        "thumb_phr.jpg", "https://example.com/packhorse", "https://example.com/culture/phr",
                        4.6, -43.6400, 172.6400),
                new Trail(703924, "Milford Track", "", "", "", "", "", "", "", "", "", 0, 0, 0));

        when(mockLogInterface.getAllTrailLogs()).thenReturn(mockLogs);

        when(mockTrailRepo.getAllTrails()).thenReturn(mockTrails);

        when(mockLogInterface.findByTrailId(703975)).thenReturn(Optional.of(mockLogs.get(0)));
        when(mockLogInterface.findByTrailId(999999)).thenReturn(Optional.empty());

        when(mockTrailRepo.findById(anyInt())).thenAnswer(invocation -> {
            int id = invocation.getArgument(0);
            return mockTrails.stream().filter(t -> t.getId() == id)
                    .findFirst();
        });

        doAnswer(invocation -> {
            TrailLog newLog = invocation.getArgument(0);
            mockLogs.add(newLog);
            return null;
        }).when(mockLogInterface).upsert(any(TrailLog.class));

        doAnswer(invocationOnMock -> {
            int logId = invocationOnMock.getArgument(0);
            mockLogs.removeIf(t -> t.getId() == logId);
            return null;
        }).when(mockLogInterface).deleteById(anyInt());

        when(mockLogInterface.countTrailLogs()).thenAnswer(invocationOnMock -> mockLogs.size());

        logService = new LogService(mockLogInterface, mockTrailRepo);
        logService.setMaxResults(20);
    }

    @Test
    @DisplayName("Should return all of the logs showing only 20 per page if the search query is empty")
    void testSearchLogsEmptyQuery() {
        logService.setCurrentQuery("");
        List<TrailLog> logs = logService.getPage(0);

        assertNotNull(logs);
        assertFalse(logs.isEmpty(), "Expected at least one log in database");
        assertTrue(logs.size() <= 20, "Page size should not be greater than 20");
        assertEquals(10, logs.size(), "Should return the first 10 logs");
    }

    @Test
    @DisplayName("Should return logs independently of case")
    void testSearchLogsCaseInsensitive() {
        logService.setCurrentQuery("track");
        List<TrailLog> lowerCase = logService.getPage(0);

        logService.setCurrentQuery("TRACK");
        List<TrailLog> upperCase = logService.getPage(0);

        logService.setCurrentQuery("TraCk");
        List<TrailLog> mixedCase = logService.getPage(0);

        assertEquals(lowerCase, upperCase, "Case-insensitive search should return same results");
        assertEquals(mixedCase, upperCase, "Should not matter if there are any case differences");
    }

    @Test
    @DisplayName("Should return empty list when no logs match the search query")
    void testSearchLogsNoMatch() {
        logService.setCurrentQuery("doesntexist");
        List<TrailLog> logs = logService.getPage(0);

        assertNotNull(logs);
        assertTrue(logs.isEmpty(), "Expected no logs matching the search query");
    }

    @Test
    @DisplayName("Should return the correct number of pages")
    void testGetNumberOfPages() {
        logService.setMaxResults(4);
        logService.setCurrentQuery("");
        logService.updateLogs();
        int pages = logService.getNumberOfPages();
        assertEquals(3, pages, "Need 3 pages with 4 logs per page and 10 total");

        logService.setCurrentQuery("track");
        logService.updateLogs();
        int pagesFiltered = logService.getNumberOfPages();
        assertEquals(1, pagesFiltered, "There should only be one page for 3 filtered results");

        logService.setCurrentQuery("doesntexist");
        logService.updateLogs();
        int pagesNoMatch = logService.getNumberOfPages();
        assertEquals(0, pagesNoMatch, "There should be no pages for no results");

    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void testPagination() {
        logService.setMaxResults(4);
        logService.setCurrentQuery("");

        List<TrailLog> page1 = logService.getPage(0);
        assertEquals(4, page1.size(), "First page should have 4 logs");

        List<TrailLog> page2 = logService.getPage(1);
        assertEquals(4, page2.size(), "Second page should have 4 logs");

        List<TrailLog> page3 = logService.getPage(2);
        assertEquals(2, page3.size(), "Third page should have 1 log");

        assertNotEquals(page1.getFirst().getId(), page2.getFirst().getId(), "Pages should not overlap");
    }

    @Test
    @DisplayName("Should return all the logs in the database")
    void testGetAllLogs() {
        logService.setMaxResults(10);
        logService.setCurrentQuery("");
        List<TrailLog> logs = logService.getAllLogs();
        assertEquals(10, logs.size(), "Should return all 10 logs");
    }

    @Test
    @DisplayName("Should return log when trailId exists")
    void testGetLogByTrailIdFound() {
        Optional<TrailLog> result = logService.getLogByTrailId(703975);

        assertTrue(result.isPresent(), "Expected to find a log for trailId 703975");
        assertEquals(703975, result.get().getTrailId(), "Should return correct trial id");
        assertEquals(1, result.get().getId(), "Should return correct log id");
    }

    @Test
    @DisplayName("Should return empty when trailId does not exist")
    void testGetLogByTrailIdNotFound() {
        Optional<TrailLog> result = logService.getLogByTrailId(999999);

        assertFalse(result.isPresent(), "Expected no log for non-existing trailId");
    }

    @Test
    @DisplayName("addLog should call upsert and increase log count")
    void testAddLog() {
        int initialCount = logService.getAllLogs().size();
        TrailLog newLog = new TrailLog(99, 703876, LocalDate.now(), 60, "minutes", "Loop", 4, "Moderate",
                "Fun short hike");

        logService.addLog(newLog);

        assertEquals(initialCount + 1, logService.getAllLogs().size(), "Log list size should increase by one");
        assertTrue(mockLogs.contains(newLog), "New log should be present in mockLogs");
    }

    @Test
    @DisplayName("getTrail should return correct trail when id exists")
    void testGetTrailFound() {
        Optional<Trail> result = logService.getTrail(703975);

        assertTrue(result.isPresent(), "Expected to find trail 703975");
        assertEquals("Port Hills Track", result.get().getName());
        verify(mockTrailRepo).findById(703975);
    }

    @Test
    @DisplayName("getTrail should return empty when id does not exist")
    void testGetTrailNotFound() {
        Optional<Trail> result = logService.getTrail(999999);

        assertFalse(result.isPresent(), "Expected no trail for invalid id");
    }

    @Test
    @DisplayName("deleteLog should remove log and call deleteById")
    void testDeleteLog() {
        int initialCount = logService.getAllLogs().size();

        logService.deleteLog(2);

        assertEquals(initialCount - 1, logService.getAllLogs().size(), "Log list should decrease by one");
        assertTrue(logService.getAllLogs().stream().noneMatch(l -> l.getId() == 2),
                "Deleted log should no longer exist");
    }

    @Test
    @DisplayName("count logs should return current number of logs")
    void testCountLogs() {
        int count = logService.countLogs();
        assertEquals(mockLogs.size(), count, "Count should match size of logs list");
    }

    @Test
    @DisplayName("isTrailLogged should return true if trail has a log record")
    void testIsTrailLoggedTrue() {
        boolean result = logService.isTrailLogged(703975);
        assertTrue(result, "Trail 703975 should be logged");
    }

    @Test
    @DisplayName("isTrailLogged should return false if trail has no record of a log")
    void testIsTrailLoggedFalse() {
        boolean result = logService.isTrailLogged(999999);
        assertFalse(result, "Trail 999999 should not be logged");
    }

}
