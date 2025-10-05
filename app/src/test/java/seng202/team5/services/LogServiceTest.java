package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import seng202.team5.App;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.TrailLog;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogServiceTest {

    @Mock
    private SqlBasedTrailLogRepo mockLogRepo;
    @Mock
    private SqlBasedTrailRepo mockTrailRepo;

    private LogService logService;
    private List<TrailLog> mockLogs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockLogs = Arrays.asList(
                new TrailLog(1, 705130, LocalDate.of(2025, 10, 1), 120, "minutes", "One way", 4, "Easiest", "Nice trail, steady incline."),
                new TrailLog(2, 705131, LocalDate.of(2025, 10, 2), 90, "minutes", "Loop", 5, "Easy", "Great for beginners."),
                new TrailLog(3, 705132, LocalDate.of(2025, 10, 3), 200, "minutes", "Loop", 3, "Expert", "Challenging terrain."),
                new TrailLog(4, 705133, LocalDate.of(2025, 10, 4), 150, "minutes", "Loop", 4, "Advanced", "Beautiful views."),
                new TrailLog(5, 705134, LocalDate.of(2025, 10, 5), 2, "hours", "One way", 2, "Easy", "Turned back due to weather."),
                new TrailLog(6, 705135, LocalDate.of(2025, 10, 6), 110, "minutes", "Loop", 5, "Easy", "Smooth trail, very enjoyable."),
                new TrailLog(7, 705136, LocalDate.of(2025, 10, 7), 3, "hours", "Loop", 4, "Expert", "Tough climb but rewarding."),
                new TrailLog(8, 705137, LocalDate.of(2025, 10, 8), 1, "day", "Loop", 4, "Advanced", "Perfect morning hike."),
                new TrailLog(9, 705138, LocalDate.of(2025, 10, 9), 130, "minutes", "Loop", 3, "Easiest", "Some muddy sections."),
                new TrailLog(10, 705139, LocalDate.of(2025, 10, 10), 160, "minutes", "One way", 5, "Advanced", "One of the best trails so far.")
        );

        when(mockLogRepo.getAllTrailLogs()).thenReturn(mockLogs);

        logService = new LogService(App.getDatabaseService());
    }
}
