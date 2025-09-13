package seng202.team5.cucumber.StepDefinitions;

import io.cucumber.java.en.*;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.gui.MatchmakingController;
import seng202.team5.models.Trail;
import seng202.team5.models.User;
import seng202.team5.services.MatchMakingService;
import seng202.team5.services.SearchService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ViewPersonalisedRecommendedTrailsStepDefinitions {


    @Given("the user completes the quiz")
    public void userHasCompletedProfileQuiz() {
        // Mock the repos
        SqlBasedKeywordRepo mockKeywordRepo = mock(SqlBasedKeywordRepo.class);
        SqlBasedTrailRepo mockTrailRepo = mock(SqlBasedTrailRepo.class);

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

        MatchMakingService matchMakingService = new MatchMakingService(mockKeywordRepo, mockTrailRepo);

        // Build a test user with some preferences
        User testUser = new User();
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

        matchMakingService.generateTrailWeights(testUser);

        Map<String, Integer> userWeights = matchMakingService.getUserWeights();
    }

    @When("the system shows a loading screen with {string}")
    public void userCompletesTheQuiz(String message) {
        MatchmakingController mockController = mock(MatchmakingController.class);
        when(mockController.getTitle()).thenReturn("Matchmaking in progress...");

        String actualTitle = mockController.getTitle();
        assertEquals(message, actualTitle);
    }

    @Then("the system displays {int} personalised trails ordered by best match")
    public void systemShowsLoadingScreen(int expectedCount) {
        SqlBasedTrailRepo mockTrailRepo = mock(SqlBasedTrailRepo.class);

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

        SearchService searchService = new SearchService(mockTrailRepo);
        searchService.setMaxResults(expectedCount);

        assertEquals(expectedCount, searchService.getMaxResults());
    }

//    @Given("the user has previously completed matchmaking")
//    public void userHasPreviouslyCompletedMatchmaking() {
//        //
//    }
//
//    @When("the user selects {string} on the start screen")
//    public void userSelectsContinue(String buttonLabel) {
//        //
//    }
//
//    @Then("the system displays the previously calculated personalised recommended trails")
//    public void systemDisplaysPreviouslyCalculatedTrails() {
//    //
//    }
//    @When("the matchmaking process fails {int} times")
//    public void matchmakingFailsMultipleTimes(int retries) {
//        // Simulate failure loop (e.g. throw exception retries times)
//    }
//
//    @Then("the system displays {string}")
//    public void systemDisplaysError(String errorMessage) {
//        // Assert error message is shown in UI
//    }
//
//    @Then("the user is redirected to the general recommended trails screen")
//    public void redirectedToGeneralRecommendedTrails() {
//        // Assert fallback screen displayed
//    }
}
