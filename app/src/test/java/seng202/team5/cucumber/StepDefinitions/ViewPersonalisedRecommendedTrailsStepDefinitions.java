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
    private Map<String, Integer> userWeights;

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
                new Trail(1, "Alpine Trail", "A beautiful alpine trail through the mountains", "Easy",
                        "2 hours", "thumb1.jpg", "http://example.com/trail1"),
                new Trail(2, "Forest Trail", "A scenic forest trail with wildlife viewing", "Medium",
                        "3 hours", "thumb2.jpg", "http://example.com/trail2"),
                new Trail(3, "Mountain Peak Trail", "Challenging trail to the mountain peak", "Hard",
                        "5 hours", "thumb3.jpg", "http://example.com/trail3"),
                new Trail(4, "Coastal Walk", "Easy coastal walk with ocean views", "Easy",
                        "1.5 hours", "thumb4.jpg", "http://example.com/trail4"),
                new Trail(5, "River Trail", "Trail following the river through the valley", "Medium",
                        "2.5 hours", "thumb5.jpg", "http://example.com/trail5"),
                new Trail(6, "Lakeside Loop", "Loop trail around the serene lake", "Easy",
                        "2 hours", "thumb6.jpg", "http://example.com/trail6"),
                new Trail(7, "Glacier Path", "Trail through icy glaciers, suitable for experienced hikers", "Hard",
                        "6 hours", "thumb7.jpg", "http://example.com/trail7"),
                new Trail(8, "Bushland Circuit", "Circuit trail through native bush", "Medium",
                        "3 hours", "thumb8.jpg", "http://example.com/trail8"),
                new Trail(9, "Volcanic Ridge Track", "Trail along volcanic ridges with dramatic views", "Hard",
                        "4 hours", "thumb9.jpg", "http://example.com/trail9"),
                new Trail(10, "Wetlands Walk", "Easy walk through wetlands, great for birdwatching", "Easy",
                        "1.5 hours", "thumb10.jpg", "http://example.com/trail10"));
        when(mockTrailRepo.getAllTrails()).thenReturn(mockTrails);
        searchService = new SearchService(mockTrailRepo);
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
        userWeights = matchmakingService.getUserWeights();
    }

    @Then("the user is shown the previously calculated personalised recommended trails screen directly")
    public void userShownCalculatedRecommendations() {
        orderedTrails = matchmakingService.getTrailsSortedByWeight();
        assertNotNull(orderedTrails);
    }
}
