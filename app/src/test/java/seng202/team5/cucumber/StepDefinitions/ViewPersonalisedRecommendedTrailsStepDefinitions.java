package seng202.team5.cucumber.StepDefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import seng202.team5.models.Trail;
import seng202.team5.services.MatchMakingService;
import seng202.team5.services.SearchService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ViewPersonalisedRecommendedTrailsStepDefinitions {
    private SearchService searchService;
    private MatchMakingService matchMakingService;
    private List<Trail> orderedTrails;


    @And("{int} recommended trails are displayed")
    public void recommendedTrailsDisplayed(int count) {
        searchService.setMaxResults(count);
        assertEquals(count, searchService.getMaxResults());
    }

    @And("the trails are ordered by highest to lowest match")
    public void trailsOrderedByHighestToLowest() {
        //TODO need test user here
        orderedTrails = matchMakingService.getTrailsSortedByWeight();

        //check ordering
        for (int i = 0; i < orderedTrails.size() - 1; i++) {
            double currentWeight = orderedTrails.get(i).getUserWeight();
            double nextWeight = orderedTrails.get(i + 1).getUserWeight();
            assertTrue(currentWeight >= nextWeight,
                    "Trail at index " + i + " should have weight >= next trail");
        }
    }

    @When("the user reloads up the application")
    public void userReloadsApplication() {

    }

    @And("user selects the \"Continue\" button on the start screen")
    public void userSelectsContinueButton() {

    }

    @Then("the user is shown the previously calculated personalised recommended trails screen directly")
    public void userShownCalculatedRecommendations() {
        orderedTrails = matchMakingService.getTrailsSortedByWeight();
        assertNotNull(orderedTrails);
    }

    @When("there is an error during matchmaking calculations")
    public void errorDuringMatchmakingCalculations() {

    }






}
