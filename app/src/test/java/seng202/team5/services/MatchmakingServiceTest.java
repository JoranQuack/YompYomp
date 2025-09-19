package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.Trail;
import seng202.team5.models.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchmakingServiceTest {
    private MatchmakingService matchmakingService;
    @Mock
    private SqlBasedTrailRepo mockTrailRepo;
    @Mock
    private SqlBasedKeywordRepo mockKeywordRepo;
    private List<Trail> mockTrails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Map<String, List<String>> mockKeywords = createMockKeywordData();
        when(mockKeywordRepo.getKeywords()).thenReturn(createMockKeywordData());

        mockTrailRepo = mock(SqlBasedTrailRepo.class);
        mockTrails = new ArrayList<>(Arrays.asList(
                //int id, String name, String description, String difficulty, String completionInfo,
                //            String thumbnailURL, String webpageURL
                new Trail(1, "Alpine Trail", "Easy", "A beautiful alpine trail through the mountains",
                        "2 hours", "thumb1.jpg", "http://example.com/trail1"),
                new Trail(2, "Forest Trail", "Medium","A scenic forest trail with wildlife viewing",
                        "3 hours", "thumb2.jpg", "http://example.com/trail2"),
                new Trail(3, "Mountain Peak Trail", "Hard", "Challenging trail to the mountain peak",
                        "5 hours", "thumb3.jpg", "http://example.com/trail3"),
                new Trail(4, "Coastal Walk", "Easy", "Easy coastal walk with ocean views",
                        "1.5 hours", "thumb4.jpg", "http://example.com/trail4"),
                new Trail(5, "River Trail", "Medium", "Trail following the river through the valley",
                        "2.5 hours", "thumb5.jpg", "http://example.com/trail5")));
        when(mockTrailRepo.getAllTrails()).thenReturn(mockTrails);

        matchmakingService = new MatchmakingService(mockKeywordRepo, mockTrailRepo);
    }

    /**
     * Creates mock keyword data for testing that matches the expected categories
     */
    private Map<String, List<String>> createMockKeywordData() {
        Map<String, List<String>> mockKeywords = new HashMap<>();
        mockKeywords.put("FamilyFriendly", Arrays.asList("children", "easy"));
        mockKeywords.put("Accessible", Arrays.asList("accessible", "abilities"));
        mockKeywords.put("Difficult", Arrays.asList("difficult", "challenging"));
        mockKeywords.put("Rocky", Arrays.asList("steep", "gorge"));
        mockKeywords.put("Forest", Arrays.asList("forest", "bush"));
        mockKeywords.put("Reserve", Arrays.asList("reserve", "park"));
        mockKeywords.put("Wet", Arrays.asList("lake", "river"));
        mockKeywords.put("Beach", Arrays.asList("coast", "beach"));
        mockKeywords.put("Alpine", Arrays.asList("mountain", "alpine"));
        mockKeywords.put("Wildlife", Arrays.asList("wildlife", "animal"));
        mockKeywords.put("Historical", Arrays.asList("historic", "ruins"));
        mockKeywords.put("Waterfall", Arrays.asList("waterfall", "falls"));
        return mockKeywords;
    }

    private User makeTestUser() {
        User user = new User();
        user.setIsFamilyFriendly(true);
        user.setIsAccessible(false);
        user.setExperienceLevel(3);
        user.setGradientPreference(2);
        user.setBushPreference(4);
        user.setReservePreference(1);
        user.setLakeRiverPreference(5);
        user.setCoastPreference(1);
        user.setMountainPreference(4);
        user.setWildlifePreference(2);
        user.setHistoricPreference(3);
        user.setWaterfallPreference(1);
        return user;
    }

    /**
     * Calculated the expected weighted score for a trail, based on the given
     * strength contribution and coverage values. This helper function is used in
     * tests
     * to independently verify the scoring logic in {@link MatchmakingService}.
     *
     * @param strengthSum the total weighted sum of matched trail categories
     * @param matched     the number of trail categories that match user preferences
     * @param total       the total number of categories in the trail
     * @param maxScore    the maximum possible weight sum across all user
     *                    preferences
     * @return the expected weighted score (combination of user-weighted strength
     *         and category coverage)
     */
    private double expectedScore(double strengthSum, int matched, int total, double maxScore) {
        double strength = strengthSum / maxScore;
        double coverage = (double) matched / total;
        return MatchmakingService.STRENGTH_WEIGHT * strength + (1 - MatchmakingService.STRENGTH_WEIGHT) * coverage;
    }

    @Test
    @DisplayName("Should correctly map user preferences")
    void testUserWeightsPopulatedCorrectly() throws MatchmakingFailedException {
        User user = makeTestUser();
        matchmakingService.setUserPreferences(user);

        Map<String, Integer> userWeights = matchmakingService.getUserWeights();
        assertEquals(5, userWeights.get("FamilyFriendly"));
        assertEquals(0, userWeights.get("Accessible"));
        assertEquals(3, userWeights.get("Difficult"));
        assertEquals(2, userWeights.get("Rocky"));
        assertEquals(4, userWeights.get("Forest"));
        assertEquals(1, userWeights.get("Reserve"));
        assertEquals(5, userWeights.get("Wet"));
        assertEquals(1, userWeights.get("Beach"));
        assertEquals(4, userWeights.get("Alpine"));
        assertEquals(2, userWeights.get("Wildlife"));
        assertEquals(3, userWeights.get("Historical"));
        assertEquals(1, userWeights.get("Waterfall"));
    }

    @Test
    @DisplayName("Should categorise trail correctly based on description")
    void testCategoriseTrail() throws MatchmakingFailedException {
        Trail trail = mockTrails.getFirst();
        Set<String> categories = matchmakingService.categoriseTrail(trail);
        assertTrue(categories.contains("Alpine"));
        assertFalse(categories.contains("Wet"));
        assertFalse(categories.contains("Forest"));
        assertEquals(1, categories.size());

        trail = mockTrails.get(4);
        categories = matchmakingService.categoriseTrail(trail);
        assertTrue(categories.contains("Wet"));
        assertFalse(categories.contains("Alpine"));
        assertEquals(1, categories.size());
    }

    @Test
    @DisplayName("Should assign weights to trails correctly, according to their category")
    void testAssignWeightsToTrails() throws MatchmakingFailedException {
        User user = makeTestUser();
        matchmakingService.setUserPreferences(user);
        matchmakingService.assignWeightsToTrails();

        double weight1 = matchmakingService.getTrailWeight(1); // Alpine Trail
        double weight2 = matchmakingService.getTrailWeight(2); // Forest Trail
        double weight3 = matchmakingService.getTrailWeight(3); // Mountain Peak Trail
        double weight4 = matchmakingService.getTrailWeight(4); // Coastal Walk
        double weight5 = matchmakingService.getTrailWeight(5); // River Trail
        final double maxScore = matchmakingService.getMaxScore(); // Max score = 5 + 0 + 3 + 2 + 4 + 1 + 5 + 1 + 4 + 2 +
                                                                  // 3 + 1 = 31
        assertEquals(expectedScore(4.0, 1, 1, maxScore), weight1, 0.0001); // Alpine Trail (Alpine: 4)
        assertEquals(expectedScore((4.0 + 2.0), 2, 2, maxScore), weight2, 0.0001); // Forest Trail (Forest: 4, Wildlife: 2)
        assertEquals(expectedScore((4.0 + 3.0), 2, 2, maxScore), weight3, 0.0001); // Mountain Peak Trail (Alpine: 4, Difficult: 3)
        assertEquals(expectedScore((5.0 + 1.0), 2, 2, maxScore), weight4, 0.0001); // Coastal Walk (Beach: 1, FamilyFriendly: 5)
        assertEquals(expectedScore(5.0, 1, 1, maxScore), weight5, 0.0001); // River Trail (Wet: 5);
    }

    @Test
    @DisplayName("Should return recommended trails sorted by weight")
    void testGetTrailsSortedByWeight() throws MatchmakingFailedException {
        User user = makeTestUser();
        matchmakingService.setUserPreferences(user);
        matchmakingService.assignWeightsToTrails();

        List<Trail> sortedTrails = matchmakingService.getTrailsSortedByWeight();

        assertEquals(5, sortedTrails.size());
        assertEquals("Mountain Peak Trail", sortedTrails.getFirst().getName()); // 0.3806
        assertEquals("Coastal Walk", sortedTrails.get(1).getName()); // 0.3548 alphabetically first
        assertEquals("Forest Trail", sortedTrails.get(2).getName()); // 0.3548
        assertEquals("River Trail", sortedTrails.get(3).getName()); // 0.3290
        assertEquals("Alpine Trail", sortedTrails.getLast().getName()); // 0.3032

    }

    @Test
    @DisplayName("Should return paginated personalised trails")
    void testGetPersonalisedTrails() throws MatchmakingFailedException {
        User user = makeTestUser();
        matchmakingService.setUserPreferences(user);
        matchmakingService.assignWeightsToTrails();

        List<Trail> page0 = matchmakingService.getPersonalisedTrails(0);
        assertEquals(5, page0.size());
        assertEquals("Mountain Peak Trail", page0.getFirst().getName());
        assertEquals("Coastal Walk", page0.get(1).getName()); // alphabetical again
        assertEquals("Forest Trail", page0.get(2).getName());
        assertEquals("River Trail", page0.get(3).getName());
        assertEquals("Alpine Trail", page0.getLast().getName());

        List<Trail> page1 = matchmakingService.getPersonalisedTrails(1);
        assertEquals(0, page1.size());

        // Test with a smaller maxResults for multipage test
        MatchmakingService customMaxService = new MatchmakingService(mockKeywordRepo, mockTrailRepo);
        customMaxService.setMaxResults(2); // Simulate smaller page size
        customMaxService.setUserPreferences(user);
        customMaxService.assignWeightsToTrails();

        List<Trail> customPage0 = customMaxService.getPersonalisedTrails(0);
        assertEquals(2, customPage0.size());
        assertEquals("Mountain Peak Trail", customPage0.getFirst().getName());
        assertEquals("Coastal Walk", customPage0.getLast().getName());

        List<Trail> customPage1 = customMaxService.getPersonalisedTrails(1);
        assertEquals(2, customPage1.size());
        assertEquals("Forest Trail", customPage1.getFirst().getName());
        assertEquals("River Trail", customPage1.getLast().getName());

        List<Trail> customPage2 = customMaxService.getPersonalisedTrails(2);
        assertEquals(1, customPage2.size());
        assertEquals("Alpine Trail", customPage2.getFirst().getName());

        List<Trail> customPage3 = customMaxService.getPersonalisedTrails(3);
        assertEquals(0, customPage3.size());
    }

    @Test
    @DisplayName("Should throw exception for invalid pagination")
    void testInvalidPagination() {
        assertThrows(IllegalArgumentException.class, () -> matchmakingService.getPersonalisedTrails(-1));
    }

    @Test
    @DisplayName("Should return a partial match")
    void testPartialMatchTrail() throws MatchmakingFailedException {
        User user = makeTestUser();
        matchmakingService.setUserPreferences(user);

        Set<String> categories = new HashSet<>(Arrays.asList("Wet", "Forest", "Alpine"));

        double score = matchmakingService.scoreTrail(categories);
        // Matched weights: Wet(5) + Forest(4) + Alpine(4) = 13, Max score = 29, 13/29 ≈
        // 0.4483
        // 3 matched categories / 3 total categories for the trail
        // 0.8 * 13/29 + 0.2 * 3/3 ≈
        // 0.5586
        assertEquals(expectedScore(5.0 + 4.0 + 4.0, 3, 3, matchmakingService.getMaxScore()), score, 0.0001);
    }

    @Test
    @DisplayName("Should return the same score as partial match even with duplicate words")
    void testDuplicateKeywords() throws MatchmakingFailedException {
        User user = makeTestUser();
        matchmakingService.setUserPreferences(user);

        Set<String> categories = new HashSet<>(Arrays.asList("Wet", "Forest", "Alpine", "Wet", "Forest"));
        double score = matchmakingService.scoreTrail(categories);
        // Duplicates are ignored in a Set, so same as partial match: 0.5586
        assertEquals(expectedScore(5.0 + 4.0 + 4.0, 3, 3, matchmakingService.getMaxScore()), score, 0.0001);
    }

    @Test
    @DisplayName("No match should return 0%")
    void testNoMatchTrail() throws MatchmakingFailedException {
        User user = makeTestUser();
        matchmakingService.setUserPreferences(user);

        Set<String> categories = new HashSet<>(Arrays.asList("Gorge", "Biking"));
        double score = matchmakingService.scoreTrail(categories);
        // No matching categories, score = 0/29 * 0.8 + 0 * 0.2 = 0.0
        assertEquals(0.0, score, 0.0001);
    }

    @Test
    @DisplayName("Perfect match should return 100%")
    void TestPerfectMatch() throws MatchmakingFailedException {
        User user = makeTestUser();
        matchmakingService.setUserPreferences(user);

        // trail contains all keywords in repo
        Set<String> categories = new HashSet<>(Arrays.asList("FamilyFriendly", "Accessible", "Difficult", "Rocky",
                "Forest", "Reserve", "Wet", "Beach", "Alpine", "Wildlife", "Historical", "Waterfall"));
        double score = matchmakingService.scoreTrail(categories);
        // All categories match, max score = 29/29 * 0.8 + 12/12 * 0.2= 1.0
        assertEquals(1.0, score, 0.0001);
    }

    @Test
    @DisplayName("Case-insensitive matching should still count keywords")
    void testCaseSensitivity() throws MatchmakingFailedException {
        User user = makeTestUser();
        matchmakingService.setUserPreferences(user);

        Trail trail = new Trail(6, "Case Test Trail", "Easy", "A FOREST trail with a RIVER nearby",
                "2 hours", "thumb6.jpg", "http://example.com/trail6");
        Set<String> categories = matchmakingService.categoriseTrail(trail);
        assertTrue(categories.contains("Forest"));
        assertTrue(categories.contains("Wet"));
        assertEquals(2, categories.size());
    }

    @Test
    @DisplayName("Empty list should give match of 0%")
    void testNullOrEmptyKeywords() throws MatchmakingFailedException {
        User user = makeTestUser();
        matchmakingService.setUserPreferences(user);

        double nullScore = matchmakingService.scoreTrail(null);
        double emptyScore = matchmakingService.scoreTrail(new HashSet<>());
        assertEquals(0, nullScore, 0.0001);
        assertEquals(0, emptyScore, 0.0001);

    }
}
