package seng202.team5.cucumber.StepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.models.User;
import seng202.team5.services.MatchmakingService;
import seng202.team5.services.SearchService;

import java.util.*;


import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ViewHighlightedTrailsStepDefinitions {
    private MatchmakingService matchmakingService;
    private SearchService searchService;
    private SqlBasedKeywordRepo mockKeywordRepo;
    private SqlBasedTrailRepo mockTrailRepo;
    private List<Trail> displayedTrails;
    private User testUser;


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

        //Had to make the lat and the lon 0.0 (not sure if that is what we want ???)
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

    @When("the system loads up the application for the guest user")
    public void theSystemLoadsUpTheApplicationForTheGuestUser() {
        displayedTrails = searchService.getPage(0); // retrieves an arbitrary set of trails
    }

    @Then("the system directs the user from the start screen to the dashboard highlighted trails")
    public void theSystemDirectsTheUserFromTheStartScreenToTheDashboardHighlightedTrails() {
        assertNotNull(displayedTrails);
    }

    @When("the user navigates to the main.db file")
    public void userNavigatesToMaindbFile() {
        // user navigates to file
    }

    @And("user deletes the file resetting the application")
    public void userDeletesTheFileResettingTheApplication() {
        testUser = null;
    }

    @Then("the user can restart the application")
    public void userRestartsApplication() {
        matchmakingService = new MatchmakingService(mockKeywordRepo, mockTrailRepo);
        testUser = new User();
    }

    @And("follow the basic flow instructions to find the highlighted page")
    public void followTheBasicFlowInstructionsToFindTheHighlightedPage() {
        theSystemLoadsUpTheApplicationForTheGuestUser(); // user follows basic flow
    }
}
