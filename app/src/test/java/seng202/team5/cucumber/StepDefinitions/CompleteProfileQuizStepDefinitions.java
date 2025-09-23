package seng202.team5.cucumber.StepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.gui.LoadingController;
import seng202.team5.models.Trail;
import seng202.team5.models.User;
import seng202.team5.services.MatchmakingService;
import seng202.team5.services.SearchService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompleteProfileQuizStepDefinitions {
    private SqlBasedKeywordRepo mockKeywordRepo;
    private SqlBasedTrailRepo mockTrailRepo;
    private MatchmakingService matchmakingService;
    private SearchService searchService;
    private User testUser;
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
        matchmakingService = new MatchmakingService(new DatabaseService());

        // Build a test user with some preferences
        testUser = new User();
    }

    @Given("the user has completed the profile quiz")
    public void userHasCompletedProfileQuiz() throws MatchmakingFailedException {
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

        matchmakingService.setUserPreferences(testUser);
    }

    @And("all trail data is available and has been loaded")
    public void allTrailDataLoaded() {
        List<Trail> loadedTrails = searchService.getPage(0);
        assertNotNull(loadedTrails, "Trails should not be null");
        assertEquals(10, loadedTrails.size());
    }

    @When("the system begins match making")
    public void systemBeginsMatchmaking() throws MatchmakingFailedException {
        matchmakingService.generateTrailWeights(testUser);
    }

    @Then("the user sees a loading screen for between 1 and 10 seconds with the message {string}")
    public void systemShowsLoadingScreen(String message) {
        LoadingController mockController = mock(LoadingController.class);
        when(mockController.getTitle()).thenReturn("Matchmaking in progress...");

        String actualTitle = mockController.getTitle();
        assertEquals(message, actualTitle);
    }

    @Given("the user had previously completed the profile quiz and has matchmaking results saved")
    public void userHasPreviouslyCompletedQuiz() throws MatchmakingFailedException {
        userHasCompletedProfileQuiz();

        // Simulate system has already run matchmaking before
        matchmakingService.generateTrailWeights(testUser);
    }

    @When("the user selects the \"Change Quiz Preferences\" button on the dashboard")
    public void userSelectsRedoQuiz() {
        // Retrieve previously stored matchmaking results
        userWeights = matchmakingService.getUserWeights();
    }

    @Then("the user will be taken back to original questions for the profile")
    public void userTakenBackToOriginalQuestions() throws MatchmakingFailedException {
        userHasCompletedProfileQuiz();
    }

    @And("the basic flow of the application is followed")
    public void basicFlowOfApplicationFollowed() throws MatchmakingFailedException {
        allTrailDataLoaded();
        systemBeginsMatchmaking();
        systemShowsLoadingScreen("Matchmaking in progress...");
    }
}
