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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ViewPersonalisedRecommendedTrailsStepDefinitions {
    private SearchService searchService;
    private MatchmakingService matchmakingService;
    private SqlBasedTrailRepo mockTrailRepo;
    private SqlBasedKeywordRepo mockKeywordRepo;
    private List<Trail> orderedTrails;
    private User testUser;
    private User currentUser;

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
                new Trail(1, "Alpine Trail", "Easy", "A beautiful alpine trail through the mountains",
                        "2 hours", "thumb1.jpg", "http://example.com/trail1", -43.5321, 172.6362),
                new Trail(2, "Forest Trail", "Medium", "A scenic forest trail with wildlife viewing",
                        "3 hours", "thumb2.jpg", "http://example.com/trail2", -43.5380, 172.6410),
                new Trail(3, "Mountain Peak Trail", "Hard", "Challenging trail to the mountain peak",
                        "5 hours", "thumb3.jpg", "http://example.com/trail3", -43.5450, 172.6500),
                new Trail(4, "Coastal Walk", "Easy", "Easy coastal walk with ocean views",
                        "1.5 hours", "thumb4.jpg", "http://example.com/trail4", -43.5250, 172.6200),
                new Trail(5, "River Trail", "Medium", "Trail following the river through the valley",
                        "2.5 hours", "thumb5.jpg", "http://example.com/trail5", -43.5300, 172.6450),
                new Trail(6, "Lakeside Loop", "Easy", "Loop trail around the serene lake",
                        "2 hours", "thumb6.jpg", "http://example.com/trail6", -43.5405, 172.6350),
                new Trail(7, "Glacier Path", "Hard", "Trail through icy glaciers, suitable for experienced hikers",
                        "6 hours", "thumb7.jpg", "http://example.com/trail7", -43.5500, 172.6550),
                new Trail(8, "Bushland Circuit", "Medium", "Circuit trail through native bush",
                        "3 hours", "thumb8.jpg", "http://example.com/trail8", -43.5270, 172.6280),
                new Trail(9, "Volcanic Ridge Track", "Hard", "Trail along volcanic ridges with dramatic views",
                        "4 hours", "thumb9.jpg", "http://example.com/trail9", -43.5480, 172.6480),
                new Trail(10, "Wetlands Walk", "Easy", "Easy walk through wetlands, great for birdwatching",
                        "1.5 hours", "thumb10.jpg", "http://example.com/trail10", -43.5260, 172.6220)
        );
        when(mockTrailRepo.getAllTrails()).thenReturn(mockTrails);
        searchService = new SearchService(mockTrailRepo, null);
        matchmakingService = new MatchmakingService(mockKeywordRepo, mockTrailRepo);

        // Build a test user with some preferences
        testUser = new User();
    }

    @And("{int} recommended trails are displayed")
    public void recommendedTrailsDisplayed(int count) {
        searchService.setMaxResults(count);
        assertEquals(count, searchService.getMaxResults());
    }

    @And("the trails are ordered by highest to lowest match")
    public void trailsOrderedByHighestToLowest() {
        matchmakingService = new MatchmakingService(mockKeywordRepo, mockTrailRepo);
        orderedTrails = matchmakingService.getTrailsSortedByWeight();

        // check ordering
        for (int i = 0; i < orderedTrails.size() - 1; i++) {
            double currentWeight = orderedTrails.get(i).getUserWeight();
            double nextWeight = orderedTrails.get(i + 1).getUserWeight();
            assertTrue(currentWeight >= nextWeight,
                    "Trail at index " + i + " should have weight >= next trail");
        }
    }

    @When("the user reloads up the application")
    public void userReloadsApplication() {
        // retrieve user data
        currentUser = testUser;
    }

    @And("user selects the \"Continue\" button on the start screen")
    public void userSelectsContinueButton() throws MatchmakingFailedException {
        // retrieve previously calculated userWeights
        matchmakingService.setUserPreferences(currentUser);
        Map<String, Integer> userWeights = matchmakingService.getUserWeights();
    }

    @Then("the user is shown the previously calculated personalised recommended trails screen directly")
    public void userShownCalculatedRecommendations() {
        orderedTrails = matchmakingService.getTrailsSortedByWeight();
        assertNotNull(orderedTrails);
    }
}
