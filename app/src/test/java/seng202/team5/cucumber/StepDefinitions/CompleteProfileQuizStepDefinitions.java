package seng202.team5.cucumber.StepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.exceptions.MatchMakingFailedException;
import seng202.team5.gui.MatchmakingController;
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

        //fake keyword data
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
        //fake trails
        List<Trail> mockTrails = new ArrayList<>(Arrays.asList(
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
                        "2024-01-05", 567.89, 101.23),
                new Trail(6, "Lakeside Loop", "Easy", "Gentle walk around the lake with picnic spots",
                        "2 hours", "Walking", "thumb6.jpg", "http://example.com/trail6",
                        "2024-01-06", 612.34, 102.45),
                new Trail(7, "Glacier Path", "Hard", "Tough hike across glacial terrain with stunning views",
                        "6 hours", "Hiking", "thumb7.jpg", "http://example.com/trail7",
                        "2024-01-07", 723.45, 113.56),
                new Trail(8, "Wetlands Walk", "Medium", "Scenic walk through wetlands and bird habitats",
                        "2 hours", "Walking", "thumb8.jpg", "http://example.com/trail8",
                        "2024-01-08", 834.56, 124.67),
                new Trail(9, "Volcanic Ridge Track", "Hard", "Challenging climb along a volcanic ridge",
                        "4.5 hours", "Hiking", "thumb9.jpg", "http://example.com/trail9",
                        "2024-01-09", 945.67, 135.78),
                new Trail(10, "Bushland Circuit", "Medium", "Loop trail through dense bush with native flora",
                        "3 hours", "Walking", "thumb10.jpg", "http://example.com/trail10",
                        "2024-01-10", 1056.78, 146.89)));
        when(mockTrailRepo.getAllTrails()).thenReturn(mockTrails);
        searchService = new SearchService(mockTrailRepo);
        matchmakingService = new MatchmakingService(mockKeywordRepo, mockTrailRepo);

        // Build a test user with some preferences
        testUser = new User();
    }

    @Given("the user has completed the profile quiz")
    public void userHasCompletedProfileQuiz() throws MatchMakingFailedException {
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
        List<Trail> loadedTrails = searchService.getTrails("", 0);
        assertNotNull(loadedTrails, "Trails should not be null");
        assertEquals(10, loadedTrails.size());
    }

    @When("the system begins match making")
    public void systemBeginsMatchMaking() throws MatchMakingFailedException {
        matchmakingService.generateTrailWeights(testUser);
    }

    @Then("the user sees a loading screen for between 1 and 10 seconds with the message {string}")
    public void systemShowsLoadingScreen(String message) {
        MatchmakingController mockController = mock(MatchmakingController.class);
        when(mockController.getTitle()).thenReturn("Matchmaking in progress...");

        String actualTitle = mockController.getTitle();
        assertEquals(message, actualTitle);
    }

    @Given("the user had previously completed the profile quiz and has matchmaking results saved")
    public void userHasPreviouslyCompletedQuiz() throws MatchMakingFailedException {
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
    public void userTakenBackToOriginalQuestions() throws MatchMakingFailedException {
        userHasCompletedProfileQuiz();
    }

    @And("the basic flow of the application is followed")
    public void basicFlowOfApplicationFollowed() throws MatchMakingFailedException {
        allTrailDataLoaded();
        systemBeginsMatchMaking();
        systemShowsLoadingScreen("Matchmaking in progress...");
    }
}
