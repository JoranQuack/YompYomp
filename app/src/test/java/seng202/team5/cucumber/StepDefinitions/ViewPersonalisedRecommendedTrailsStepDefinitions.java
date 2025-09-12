package seng202.team5.cucumber.StepDefinitions;

import io.cucumber.java.en.*;

public class ViewPersonalisedRecommendedTrailsStepDefinitions {

    @Given("the user has completed the profile quiz")
    public void userHasCompletedProfileQuiz() {
        //TO DO
    }

    @When("the user completed the quiz")
    public void userCompletesTheQuiz() {
        //
    }

    @Then("the system shows a loading screen with {string}")
    public void systemShowsLoadingScreen(String message) {
        //
    }

    @Then("after matchmaking the system displays {int} personalised trails ordered by best match")
    public void systemDisplaysTrails(int count) {
        //
    }

    @Given("the user has previously completed matchmaking")
    public void userHasPreviouslyCompletedMatchmaking() {
        //
    }

    @When("the user selects {string} on the start screen")
    public void userSelectsContinue(String buttonLabel) {
        //
    }

    @Then("the system displays the previously calculated personalised recommended trails")
    public void systemDisplaysPreviouslyCalculatedTrails() {
    //
    }
    @When("the matchmaking process fails {int} times")
    public void matchmakingFailsMultipleTimes(int retries) {
        // Simulate failure loop (e.g. throw exception retries times)
    }

    @Then("the system displays {string}")
    public void systemDisplaysError(String errorMessage) {
        // Assert error message is shown in UI
    }

    @Then("the user is redirected to the general recommended trails screen")
    public void redirectedToGeneralRecommendedTrails() {
        // Assert fallback screen displayed
    }
}
