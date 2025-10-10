package seng202.team5.cucumber.StepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.Trail;
import seng202.team5.models.User;
import seng202.team5.services.MatchmakingService;
import seng202.team5.services.SearchService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ViewAllTrailsStepDefinitions {
    private SearchService searchService;
    private SqlBasedKeywordRepo mockKeywordRepo;
    private SqlBasedTrailRepo mockTrailRepo;
    private User testUser;
    private Map<String, Integer> userWeights;
    private List<Trail> displayedTrails;

    @Before
    public void setUp() {
        // Mock the repos
        mockKeywordRepo = mock(SqlBasedKeywordRepo.class);
        mockTrailRepo = mock(SqlBasedTrailRepo.class);

        // fake keyword data
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

        when(mockKeywordRepo.getKeywords()).thenReturn(mockKeywords);
        // fake trails
        List<Trail> mockTrails = Arrays.asList(
                createMockTrail(1, "Alpine Trail", "Easy", "A beautiful alpine trail through the mountains",
                        "2 hours", "thumb1.jpg", "http://example.com/trail1", -43.5321, 172.6362),
                createMockTrail(2, "Forest Trail", "Medium", "A scenic forest trail with wildlife viewing",
                        "3 hours", "thumb2.jpg", "http://example.com/trail2", -43.5380, 172.6410),
                createMockTrail(3, "Mountain Peak Trail", "Hard", "Challenging trail to the mountain peak",
                        "5 hours", "thumb3.jpg", "http://example.com/trail3", -43.5450, 172.6500),
                createMockTrail(4, "Coastal Walk", "Easy", "Easy coastal walk with ocean views",
                        "1.5 hours", "thumb4.jpg", "http://example.com/trail4", -43.5250, 172.6200),
                createMockTrail(5, "River Trail", "Medium", "Trail following the river through the valley",
                        "2.5 hours", "thumb5.jpg", "http://example.com/trail5", -43.5300, 172.6450),
                createMockTrail(6, "Lakeside Loop", "Easy", "Loop trail around the serene lake",
                        "2 hours", "thumb6.jpg", "http://example.com/trail6", -43.5405, 172.6350),
                createMockTrail(7, "Glacier Path", "Hard", "Trail through icy glaciers, suitable for experienced hikers",
                        "6 hours", "thumb7.jpg", "http://example.com/trail7", -43.5500, 172.6550),
                createMockTrail(8, "Bushland Circuit", "Medium", "Circuit trail through native bush",
                        "3 hours", "thumb8.jpg", "http://example.com/trail8", -43.5270, 172.6280),
                createMockTrail(9, "Volcanic Ridge Track", "Hard", "Trail along volcanic ridges with dramatic views",
                        "4 hours", "thumb9.jpg", "http://example.com/trail9", -43.5480, 172.6480),
                createMockTrail(10, "Wetlands Walk", "Easy", "Easy walk through wetlands, great for birdwatching",
                        "1.5 hours", "thumb10.jpg", "http://example.com/trail10", -43.5260, 172.6220)
        );
        when(mockTrailRepo.getAllTrails()).thenReturn(mockTrails);
        searchService = new SearchService(mockTrailRepo, null);
    }

    private Trail createMockTrail(int id, String name, String difficulty, String description,
                                  String completionInfo, String thumbnailURL, String webpageURL,
                                  double lat, double lon) {
        return new Trail.Builder()
                .id(id)
                .name(name)
                .difficulty(difficulty)
                .description(description)
                .completionInfo(completionInfo)
                .thumbnailURL(thumbnailURL)
                .webpageURL(webpageURL)
                .lat(lat)
                .lon(lon)
                .build();
    }

    @Given("the user has loaded the application and is on the welcome screen")
    public void theUserHasLoadedTheApplicationAndIsOnTheWelcomeScreen() {
        // setUp() is run
    }

    @When("the user selects to skip profile set up")
    public void theUserSelectsToSkipProfileSetup() {
        testUser = null; // no user preferences, no matchmaking
    }

    @And("user selects the Trails button")
    public void theUserSelectsTheTrailsButton() {
        displayedTrails = searchService.getPage(0);
    }

    @Then("the system changes to the all-trails screen")
    public void theSystemChangesToTheAllTrailsScreen() {
        assertNotNull(displayedTrails);
    }

    @And("system displays a list of trails in alphabetical order")
    public void systemDisplaysAListOfTrailsInAlphabeticalOrder() {
        List<String> trailNames = displayedTrails.stream()
                .map(Trail::getName)
                .toList();

        // Make a copy and sort it alphabetically
        List<String> sortedNames = new ArrayList<>(trailNames);
        sortedNames.sort(String.CASE_INSENSITIVE_ORDER);

        // Assert that the displayed list matches the alphabetically sorted list
        assertEquals(sortedNames, trailNames);
    }

    @And("the dashboard screen of personalised recommended trails is shown")
    public void theDashboardScreenOfPersonalisedRecommendedTrailsIsShown() throws MatchmakingFailedException {
        new MatchmakingService(mockKeywordRepo, mockTrailRepo);
        MatchmakingService matchmakingService;

        // Build a test user with some preferences
        testUser = new User();
        testUser.setIsFamilyFriendly(true);
        testUser.setIsAccessible(false);
        testUser.setExperienceLevel(3);
        testUser.setGradientPreference(2);
        testUser.setBushPreference(4);
        testUser.setLakeRiverPreference(1);
        testUser.setCoastPreference(5);
        testUser.setMountainPreference(3);
        testUser.setWildlifePreference(4);
        testUser.setHistoricPreference(0);
        testUser.setWaterfallPreference(5);
        testUser.setReservePreference(2);

        matchmakingService = new MatchmakingService(mockKeywordRepo, mockTrailRepo);
        matchmakingService.setUserPreferences(testUser);
        displayedTrails = matchmakingService.getTrailsSortedByWeight();
        assertNotNull(displayedTrails);
    }
}
