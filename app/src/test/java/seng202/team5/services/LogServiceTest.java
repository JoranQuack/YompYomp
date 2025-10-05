package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import seng202.team5.App;
import seng202.team5.data.ITrailLog;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogServiceTest {

    @Mock
    private ITrailLog mockLogInterface;
    //TODO needs to be updated to the ITrail when refactor has been done
    @Mock
    private SqlBasedTrailRepo mockTrailRepo;

    private LogService logService;
    private List<TrailLog> mockLogs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockLogs = Arrays.asList(
                new TrailLog(1, 703975, LocalDate.of(2025, 10, 1), 120, "minutes", "One way", 4, "Easiest", "Nice trail, steady incline."),
                new TrailLog(2, 703976, LocalDate.of(2025, 10, 2), 90, "minutes", "Loop", 5, "Easy", "Great for beginners."),
                new TrailLog(3, 703977, LocalDate.of(2025, 10, 3), 200, "minutes", "Loop", 3, "Expert", "Challenging terrain."),
                new TrailLog(4, 703978, LocalDate.of(2025, 10, 4), 150, "minutes", "Loop", 4, "Advanced", "Beautiful views."),
                new TrailLog(5, 703979, LocalDate.of(2025, 10, 5), 2, "hours", "One way", 2, "Easy", "Turned back due to weather."),
                new TrailLog(6, 703980, LocalDate.of(2025, 10, 6), 110, "minutes", "Loop", 5, "Easy", "Smooth trail, very enjoyable."),
                new TrailLog(7, 703981, LocalDate.of(2025, 10, 7), 3, "hours", "Loop", 4, "Expert", "Tough climb but rewarding."),
                new TrailLog(8, 703982, LocalDate.of(2025, 10, 8), 1, "day", "Loop", 4, "Advanced", "Perfect morning hike."),
                new TrailLog(9, 703940, LocalDate.of(2025, 10, 9), 130, "minutes", "Loop", 3, "Easiest", "Some muddy sections."),
                new TrailLog(10, 703947, LocalDate.of(2025, 10, 10), 160, "minutes", "One way", 5, "Advanced", "One of the best trails so far.")
        );

        when(mockLogInterface.getAllTrailLogs()).thenReturn(mockLogs);

        //TODO update to use the ITrail interface when refactor has been done
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
        logService.setCurrentQuery("trail");
        List<TrailLog> lowerCase = logService.getPage(0);

        logService.setCurrentQuery("TRAIL");
        List<TrailLog> upperCase = logService.getPage(0);

        logService.setCurrentQuery("TraIl");
        List<TrailLog> mixedCase = logService.getPage(0);

        assertEquals(lowerCase, upperCase, "Case-insensitive search should return same results");
        assertEquals(mixedCase, upperCase, "Should not matter if there are any case differences");
    }
}
