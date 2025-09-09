package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.models.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchMakingServiceTest {
    private MatchMakingService matchMakingService;
    @Mock
    private SqlBasedTrailRepo mockTrailRepo;
    @Mock
    private SqlBasedKeywordRepo mockKeywordRepo;
    private List<Trail> mockTrails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Map<String, List<String>> mockKeywords = createMockKeywordData();
        when(mockKeywordRepo.getKeywords()).thenReturn(mockKeywords);

        mockTrailRepo = mock(SqlBasedTrailRepo.class);
        mockTrails = new ArrayList<>(Arrays.asList(
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
                        "2024-01-05", 567.89, 101.23)));
        when(mockTrailRepo.getAllTrails()).thenReturn(mockTrails);

        matchMakingService = new MatchMakingService(mockKeywordRepo, mockTrailRepo);
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
        user.setCoastPreference(0);
        user.setMountainPreference(4);
        user.setWildlifePreference(2);
        user.setHistoricPreference(3);
        user.setWaterfallPreference(0);
        return user;
    }

    /**
     *Calculated the expected weighted score for a trail, based on the given
     * strength contribution and coverage values. This helper function is used in tests
     * to independently verify the scoring logic in {@link MatchMakingService}.
     * @param strengthSum the total weighted sum of matched trail categories
     * @param matched the number of trail categories that match user preferences
     * @param total the total number of categories in the trail
     * @param maxScore the maximum possible weight sum across all user preferences
     * @return the expected weighted score (combination of user-weighted strength and category coverage)
     */
    private double expectedScore(double strengthSum, int matched, int total, double maxScore) {
        double strength = strengthSum/maxScore;
        double coverage = (double) matched/total;
        return MatchMakingService.STRENGTH_WEIGHT * strength + (1 - MatchMakingService.STRENGTH_WEIGHT) * coverage;
    }

    @Test
    @DisplayName("Should correctly map user preferences")
    void testUserWeightsPopulatedCorrectly() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        Map<String, Integer> userWeights = matchMakingService.getUserWeights();
        assertEquals(5, userWeights.get("FamilyFriendly"));
        assertEquals(0, userWeights.get("Accessible"));
        assertEquals(3, userWeights.get("Difficult"));
        assertEquals(2, userWeights.get("Rocky"));
        assertEquals(4, userWeights.get("Forest"));
        assertEquals(1, userWeights.get("Reserve"));
        assertEquals(5, userWeights.get("Wet"));
        assertEquals(0, userWeights.get("Beach"));
        assertEquals(4, userWeights.get("Alpine"));
        assertEquals(2, userWeights.get("Wildlife"));
        assertEquals(3, userWeights.get("Historical"));
        assertEquals(0, userWeights.get("Waterfall"));
    }

    @Test
    @DisplayName("Should categorise trail correctly based on description")
    void testCategoriseTrail() {
        Trail trail = mockTrails.getFirst();
        Set<String> categories = matchMakingService.categoriseTrail(trail);
        assertTrue(categories.contains("Alpine"));
        assertFalse(categories.contains("Wet"));
        assertFalse(categories.contains("Forest"));
        assertEquals(1, categories.size());

        trail = mockTrails.get(4);
        categories = matchMakingService.categoriseTrail(trail);
        assertTrue(categories.contains("Wet"));
        assertFalse(categories.contains("Alpine"));
        assertEquals(1, categories.size());
    }

    @Test
    @DisplayName("Should assign weights to trails correctly, according to their category")
    void testAssignWeightsToTrails() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);
        matchMakingService.assignWeightsToTrails();

        double weight1 = matchMakingService.getTrailWeight(1); // Alpine Trail
        double weight2 = matchMakingService.getTrailWeight(2); // Forest Trail
        double weight3 = matchMakingService.getTrailWeight(3); // Mountain Peak Trail
        double weight4 = matchMakingService.getTrailWeight(4); // Coastal Walk
        double weight5 = matchMakingService.getTrailWeight(5); // River Trail
        System.out.println(weight5);
        final double maxScore = matchMakingService.getMaxScore(); // Max score = 5 + 0 + 3 + 2 + 4 + 1 + 5 + 0 + 4 + 2 +
                                                                  // 3 + 0 = 29
        assertEquals(expectedScore(4.0, 1, 1, maxScore), weight1, 0.0001); // Alpine Trail (Alpine: 4)
        assertEquals(expectedScore((4.0 + 2.0), 2, 2, maxScore), weight2, 0.0001); // Forest Trail (Forest: 4, Wildlife: 2)
        assertEquals(expectedScore(4.0 + 3.0, 2, 2, maxScore), weight3, 0.0001); // Mountain Peak Trail (Alpine: 4, Difficult: 3)
        assertEquals(expectedScore(5.0, 2, 2, maxScore), weight4, 0.0001); // Coastal Walk (Beach: 0, FamilyFriendly: 5)
        assertEquals(expectedScore(5.0, 1, 1, maxScore), weight5, 0.0001); // River Trail (Wet: 5)
    }

    @Test
    @DisplayName("Should return recommended trails sorted by weight")
    void testGetTrailsSortedByWeight() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);
        matchMakingService.assignWeightsToTrails();

        List<Trail> sortedTrails = matchMakingService.getTrailsSortedByWeight();
        assertEquals(5, sortedTrails.size());
        assertEquals("Mountain Peak Trail", sortedTrails.getFirst().getName()); // 0.3931
        assertEquals("Forest Trail", sortedTrails.get(1).getName()); // 0.3656
        assertEquals("Coastal Walk", sortedTrails.get(2).getName()); // 0.3379 equal but alphabetical
        assertEquals("River Trail", sortedTrails.get(3).getName()); // 0.3379 ^^
        assertEquals("Alpine Trail", sortedTrails.getLast().getName()); // 0.3103
    }

    @Test
    @DisplayName("Should return paginated personalised trails")
    void testGetPersonalisedTrails() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);
        matchMakingService.assignWeightsToTrails();

        List<Trail> page0 = matchMakingService.getPersonalisedTrails(0);
        assertEquals(5, page0.size());
        assertEquals("Mountain Peak Trail", page0.getFirst().getName());
        assertEquals("Forest Trail", page0.get(1).getName());
        assertEquals("Coastal Walk", page0.get(2).getName()); // alphabetical again
        assertEquals("River Trail", page0.get(3).getName());
        assertEquals("Alpine Trail", page0.getLast().getName());

        List<Trail> page1 = matchMakingService.getPersonalisedTrails(1);
        assertEquals(0, page1.size());

        // Test with a smaller maxResults for multipage test
        MatchMakingService customMaxService = new MatchMakingService(mockKeywordRepo, mockTrailRepo);
        customMaxService.setMaxResults(2); // Simulate smaller page size
        customMaxService.setUserPreferences(user);
        customMaxService.assignWeightsToTrails();

        List<Trail> customPage0 = customMaxService.getPersonalisedTrails(0);
        assertEquals(2, customPage0.size());
        assertEquals("Mountain Peak Trail", customPage0.getFirst().getName());
        assertEquals("Forest Trail", customPage0.getLast().getName());

        List<Trail> customPage1 = customMaxService.getPersonalisedTrails(1);
        assertEquals(2, customPage1.size());
        assertEquals("Coastal Walk", customPage1.getFirst().getName());
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
        assertThrows(IllegalArgumentException.class, () -> matchMakingService.getPersonalisedTrails(-1));
    }

    @Test
    @DisplayName("Should return a partial match")
    void testPartialMatchTrail() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        Set<String> categories = new HashSet<>(Arrays.asList("Wet", "Forest", "Alpine"));

        double score = matchMakingService.scoreTrail(categories);
        // Matched weights: Wet(5) + Forest(4) + Alpine(4) = 13, Max score = 29, 13/29 ≈
        // 0.4483
        assertEquals(0.4483, score, 0.0001);
    }

    @Test
    @DisplayName("Should return the same score as partial match even with duplicate words")
    void testDuplicateKeywords() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        Set<String> categories = new HashSet<>(Arrays.asList("Wet", "Forest", "Alpine", "Wet", "Forest"));
        double score = matchMakingService.scoreTrail(categories);
        // Duplicates are ignored in a Set, so same as partial match: 13/29 ≈ 0.4483
        assertEquals(0.4483, score, 0.0001);
    }

    @Test
    @DisplayName("No match should return 0%")
    void testNoMatchTrail() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        Set<String> categories = new HashSet<>(Arrays.asList("Waterfall", "Beach"));
        double score = matchMakingService.scoreTrail(categories);
        // No matching categories, score = 0/29 = 0.0
        assertEquals(0.0, score, 0.0001);
    }

    @Test
    @DisplayName("Perfect match should return 100%")
    void TestPerfectMatch() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        // trail contains all keywords in repo
        Set<String> categories = new HashSet<>(Arrays.asList("FamilyFriendly", "Accessible", "Difficult", "Rocky",
                "Forest", "Reserve", "Wet", "Beach", "Alpine", "Wildlife", "Historical", "Waterfall"));
        double score = matchMakingService.scoreTrail(categories);
        // All categories match, max score = 29/29 = 1.0
        assertEquals(1.0, score, 0.0001);
    }

    @Test
    @DisplayName("Case-insensitive matching should still count keywords")
    void testCaseSensitivity() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        Trail trail = new Trail(6, "Case Test Trail", "Easy", "A FOREST trail with a RIVER nearby",
                "2 hours", "Walking", "thumb6.jpg", "http://example.com/trail6",
                "2024-01-06", 678.90, 112.34);
        Set<String> categories = matchMakingService.categoriseTrail(trail);
        assertTrue(categories.contains("Forest"));
        assertTrue(categories.contains("Wet"));
        assertEquals(2, categories.size());
    }

    @Test
    @DisplayName("Empty list should give match of 0%")
    void testNullOrEmptyKeywords() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        double nullScore = matchMakingService.scoreTrail(null);
        double emptyScore = matchMakingService.scoreTrail(new HashSet<>());
        assertEquals(0, nullScore, 0.0001);
        assertEquals(0, emptyScore, 0.0001);

    }
}
