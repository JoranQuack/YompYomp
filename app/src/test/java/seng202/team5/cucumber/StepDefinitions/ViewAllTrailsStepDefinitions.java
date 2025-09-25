package seng202.team5.cucumber.StepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.gui.TrailsController;
import seng202.team5.models.Trail;
import seng202.team5.models.User;
import seng202.team5.services.MatchmakingService;
import seng202.team5.services.SearchService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ViewAllTrailsStepDefinitions {
    private MatchmakingService matchmakingService;
    private SearchService searchService;
    private SqlBasedKeywordRepo mockKeywordRepo;
    private SqlBasedTrailRepo mockTrailRepo;
    private User testUser;
    private Map<String, Integer> userWeights;
    private List<Trail> displayedTrails;

    @Before
    public void setUp(){
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
                new Trail(1, "Alpine Trail", "A beautiful alpine trail through the mountains", "Easy",
                        "2 hours", "thumb1.jpg", "http://example.com/trail1", 0.0, 0/0),
                new Trail(2, "Forest Trail", "A scenic forest trail with wildlife viewing", "Medium",
                        "3 hours", "thumb2.jpg", "http://example.com/trail2", 0.0, 0.0),
                new Trail(3, "Mountain Peak Trail", "Challenging trail to the mountain peak", "Hard",
                        "5 hours", "thumb3.jpg", "http://example.com/trail3", 0.0, 0.0),
                new Trail(4, "Coastal Walk", "Easy coastal walk with ocean views", "Easy",
                        "1.5 hours", "thumb4.jpg", "http://example.com/trail4", 0.0, 0.0),
                new Trail(5, "River Trail", "Trail following the river through the valley", "Medium",
                        "2.5 hours", "thumb5.jpg", "http://example.com/trail5", 0.0, 0.0),
                new Trail(6, "Lakeside Loop", "Loop trail around the serene lake", "Easy",
                        "2 hours", "thumb6.jpg", "http://example.com/trail6", 0.0, 0.0),
                new Trail(7, "Glacier Path", "Trail through icy glaciers, suitable for experienced hikers", "Hard",
                        "6 hours", "thumb7.jpg", "http://example.com/trail7", 0.0, 0.0),
                new Trail(8, "Bushland Circuit", "Circuit trail through native bush", "Medium",
                        "3 hours", "thumb8.jpg", "http://example.com/trail8", 0.0, 0.0),
                new Trail(9, "Volcanic Ridge Track", "Trail along volcanic ridges with dramatic views", "Hard",
                        "4 hours", "thumb9.jpg", "http://example.com/trail9", 0.0, 0.0),
                new Trail(10, "Wetlands Walk", "Easy walk through wetlands, great for birdwatching", "Easy",
                        "1.5 hours", "thumb10.jpg", "http://example.com/trail10", 0.0, 0.0));
        when(mockTrailRepo.getAllTrails()).thenReturn(mockTrails);
        searchService = new SearchService(mockTrailRepo);
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
        Collections.sort(sortedNames, String.CASE_INSENSITIVE_ORDER);

        // Assert that the displayed list matches the alphabetically sorted list
        assertEquals(sortedNames, trailNames);
    }

    @And("the dashboard screen of personalised recommended trails is shown")
    public void theDashboardScreenOfPersonalisedRecommendedTrailsIsShown() throws MatchmakingFailedException {
        matchmakingService = new MatchmakingService(mockKeywordRepo, mockTrailRepo);

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
