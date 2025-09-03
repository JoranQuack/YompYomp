package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import seng202.team5.data.FileBasedKeywordRepo;
import seng202.team5.data.IKeyword;
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
    private List<Trail> mockTrails;

    @BeforeEach
    void setUp() {
        IKeyword keywordRepo = new FileBasedKeywordRepo("/resources/datasets/Categories_and_Keywords.csv");
        mockTrailRepo = mock(SqlBasedTrailRepo.class);
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

//        IKeyword fakeKeywordRepo = () -> {
//            Map<String, List<String>> map = new HashMap<>();
//            map.put("FamilyFriendly", Arrays.asList("children", "easy"));
//            map.put("Accessible", Arrays.asList("accessible", "abilities"));
//            map.put("Difficult", Arrays.asList("difficult", "challenging"));
//            map.put("Rocky", Arrays.asList("steep", "gorge"));
//            map.put("Reserve", Arrays.asList("reserve", "park"));
//            map.put("Wet", Arrays.asList("lake", "river"));
//            map.put("Forest", Arrays.asList("forest", "bush"));
//            map.put("Coast", Arrays.asList("coast", "beach"));
//            map.put("Wildlife", Arrays.asList("wildlife", "animal"));
//            map.put("Alpine", Arrays.asList("mountain", "hill"));
//            map.put("Historical", Arrays.asList("historic-site", "ruins"));
//            map.put("Waterfall", Arrays.asList("waterfall", "falls"));
//            return map;
//        };

        matchMakingService = new MatchMakingService(keywordRepo, mockTrailRepo);
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

    @Test
    @DisplayName("Should correctly map user preferences")
    void testUserWeightsPopulatedCorrectly() {
        User user  = makeTestUser();
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

        // Expected categories and weights based on descriptions and user weights
        // Max score = 5 + 0 + 3 + 2 + 4 + 1 + 5 + 0 + 4 + 2 + 3 + 0 = 29
        // Alpine Trail (Alpine:4) = 4/29 ~ 0.1379
        // Forest Trail (Forest:4, Wildlife:2) = (4 + 2)/29 ≈ 0.2069
        // Mountain Peak Trail (Alpine:4, Difficult:3) = (4 + 3)/29 ≈ 0.2414
        // Coastal Walk (Beach:0) = 0/29 = 0.0
        // River Trail (Wet:5) = 5/29 ~ 0.1724
        assertEquals(0.1379, weight1, 0.0001);
        assertEquals(0.2069, weight2, 0.0001);
        assertEquals(0.2414, weight3, 0.0001);
        assertEquals(0.0, weight4, 0.0001);
        assertEquals(0.1724, weight5, 0.0001);
    }

    @Test
    @DisplayName("Should return a partial match")
    void testPartialMatchTrail() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        Set<String> categories = new HashSet<>(Arrays.asList("Wet", "Forest", "Alpine"));

        double score = matchMakingService.scoreTrail(categories);
        // Matched weights: Wet(5) + Forest(4) + Alpine(4) = 13, Max score = 29, 13/29 ≈ 0.4483
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

        //trail contains all keywords in repo
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
