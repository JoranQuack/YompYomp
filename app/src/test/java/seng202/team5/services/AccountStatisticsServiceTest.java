package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import seng202.team5.models.User;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountStatisticsServiceTest {

    private AccountStatisticsService accountStatisticsService;

    @Mock
    private SqlBasedTrailLogRepo mockTrailLogRepo;

    @Mock
    private SqlBasedTrailRepo mockTrailRepo;

    @Mock
    private MatchmakingService mockMatchmakingService;

    @Mock
    private SqlBasedTrailLogRepo mockTrailLogRepo2;

    private List<Trail> testTrails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User testUser = createTestUser();
        List<TrailLog> testTrailLogs = createTestTrailLogs();
        testTrails = createTestTrails();

        // Mock repo behaviours
        when(mockTrailLogRepo.getAllTrailLogs()).thenReturn(testTrailLogs);
        when(mockTrailRepo.findById(1)).thenReturn(Optional.of(testTrails.get(0)));
        when(mockTrailRepo.findById(2)).thenReturn(Optional.of(testTrails.get(1)));
        when(mockTrailRepo.findById(3)).thenReturn(Optional.of(testTrails.get(2)));

        accountStatisticsService = new AccountStatisticsService(
                mockTrailRepo, mockTrailLogRepo, mockMatchmakingService, testUser);
    }

    private User createTestUser() {
        User user = new User();
        user.setExperienceLevel(3);
        user.setGradientPreference(2);
        user.setBushPreference(4);
        user.setReservePreference(1);
        user.setLakeRiverPreference(5);
        user.setCoastPreference(3);
        user.setMountainPreference(4);
        user.setWildlifePreference(2);
        user.setHistoricPreference(3);
        user.setWaterfallPreference(1);
        return user;
    }

    private List<TrailLog> createTestTrailLogs() {
        return Arrays.asList(
                new TrailLog(1, 1, LocalDate.now(), 120, "minutes", "completed", 4, "Easy", "Great trail"),
                new TrailLog(2, 2, LocalDate.now().minusDays(1), 180, "minutes", "completed", 5, "Medium",
                        "Challenging"),
                new TrailLog(3, 3, LocalDate.now().minusDays(2), 240, "minutes", "completed", 3, "Hard", "Tough hike"));
    }

    private List<Trail> createTestTrails() {
        Trail trail1 = new Trail.Builder()
                .id(1)
                .name("Mountain Boi")
                .difficulty("Easy")
                .description("Beautiful mountain views woooooow")
                .completionInfo("2 hours")
                .thumbnailURL("thumb1.jpg")
                .webpageURL("https://example.com/trail1")
                .region("Canterbury")
                .userWeight(0.8)
                .lat(-43.5321)
                .lon(172.6362)
                .build();

        Trail trail2 = new Trail.Builder()
                .id(2)
                .name("Forest Boi")
                .difficulty("Medium")
                .description("Peaceful forest walk and you won't even get lost")
                .completionInfo("3 hours")
                .thumbnailURL("thumb2.jpg")
                .webpageURL("https://example.com/trail2")
                .region("Otago")
                .userWeight(0.6)
                .lat(-43.5350)
                .lon(172.6400)
                .build();

        Trail trail3 = new Trail.Builder()
                .id(3)
                .name("Coastal Boi")
                .difficulty("Hard")
                .description("Scenic coastal walk but remember not to fall in")
                .completionInfo("4 hours")
                .thumbnailURL("thumb3.jpg")
                .webpageURL("https://example.com/trail3")
                .region("Canterbury")
                .userWeight(0.9)
                .lat(-43.5400)
                .lon(172.6500)
                .build();

        return Arrays.asList(trail1, trail2, trail3);
    }

    @Test
    @DisplayName("Should return correct number of logged trails")
    void testGetTotalLoggedTrails() {
        int totalTrails = accountStatisticsService.getTotalLoggedTrails();
        assertEquals(3, totalTrails);
    }

    @Test
    @DisplayName("Should return user preferences data sorted by value")
    void testGetUserPreferencesData() {
        Map<String, Integer> preferences = accountStatisticsService.getUserPreferencesData();

        assertNotNull(preferences);
        assertEquals(10, preferences.size());
        assertTrue(preferences.containsKey("Experience"));
        assertTrue(preferences.containsKey("Gradient"));
        assertTrue(preferences.containsKey("Forest"));

        List<Integer> values = new ArrayList<>(preferences.values());
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue(values.get(i) >= values.get(i + 1));
        }
    }

    @Test
    @DisplayName("Should return difficulty stats from trail logs")
    void testGetDifficultyStatistics() {
        Map<String, Object> difficultyStats = accountStatisticsService.getDifficultyStatistics();

        assertNotNull(difficultyStats);
        assertTrue(difficultyStats.containsKey("perceived"));

        @SuppressWarnings("unchecked")
        Map<String, Integer> perceivedDifficulties = (Map<String, Integer>) difficultyStats.get("perceived");

        assertEquals(1, perceivedDifficulties.get("Easy"));
        assertEquals(1, perceivedDifficulties.get("Medium"));
        assertEquals(1, perceivedDifficulties.get("Hard"));
    }

    @Test
    @DisplayName("Should calculate correct avg score")
    void testGetAverageMatchScore() {
        double averageScore = accountStatisticsService.getAverageMatchScore();

        assertEquals(76.67, averageScore, 0.01);
    }

    @Test
    @DisplayName("Should return 0.0 if logged trails is empty")
    void testGetAverageMatchScore_EmptyList() {
        AccountStatisticsService accountStatisticsService2 = new AccountStatisticsService(mockTrailRepo, mockTrailLogRepo2, mockMatchmakingService, null);
        double averageScore = accountStatisticsService2.getAverageMatchScore();
        assertEquals(0.0, averageScore, "If there are no logs it should return 0.0");
    }

    @Test
    @DisplayName("Should return regional stats, limited to top 6")
    void testGetRegionalStatistics() {
        Map<String, Integer> regionalStats = accountStatisticsService.getRegionalStatistics();

        assertNotNull(regionalStats);
        assertTrue(regionalStats.size() <= 6);
        assertTrue(regionalStats.containsKey("Canterbury"));
        assertTrue(regionalStats.containsKey("Otago"));

        assertEquals(2, regionalStats.get("Canterbury"));
        assertEquals(1, regionalStats.get("Otago"));

        List<Integer> values = new ArrayList<>(regionalStats.values());
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue(values.get(i) >= values.get(i + 1));
        }
    }

    @Test
    @DisplayName("Should return top category from categorised trails")
    void testGetTopCategory() throws MatchmakingFailedException {
        when(mockMatchmakingService.categoriseTrail(testTrails.get(0)))
                .thenReturn(Set.of("Mountain", "Scenic"));
        when(mockMatchmakingService.categoriseTrail(testTrails.get(1)))
                .thenReturn(Set.of("Forest", "Easy"));
        when(mockMatchmakingService.categoriseTrail(testTrails.get(2)))
                .thenReturn(Set.of("Coastal", "Scenic"));

        String topCategory = accountStatisticsService.getTopCategory();

        assertNotNull(topCategory);
        assertEquals("Scenic", topCategory);
    }

    @Test
    @DisplayName("Should handle both yays and nays in categorisation")
    void testGetTopCategoryWithMixedResults() throws MatchmakingFailedException {
        when(mockMatchmakingService.categoriseTrail(testTrails.get(0)))
                .thenReturn(Set.of("Mountain"));
        when(mockMatchmakingService.categoriseTrail(testTrails.get(1)))
                .thenThrow(new MatchmakingFailedException("Failed"));
        when(mockMatchmakingService.categoriseTrail(testTrails.get(2)))
                .thenReturn(Set.of("Coastal"));

        String topCategory = accountStatisticsService.getTopCategory();

        assertNotNull(topCategory);
        assertTrue(topCategory.equals("Mountain") || topCategory.equals("Coastal"));
    }
}