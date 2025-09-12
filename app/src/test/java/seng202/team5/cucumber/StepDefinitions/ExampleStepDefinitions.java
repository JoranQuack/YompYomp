package seng202.team5.cucumber.StepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class ExampleStepDefinitions {

    private String name;
    private String reversedName;

    @Before
    public void reset() {
        name = null;
        reversedName = null;
    }

    @Given("my name is {string}")
    public void my_name_is(String name) {
        this.name = name;
    }

    @When("I ask what my name is backwards")
//    public void get_reverse_name() {
//        this.reversedName = app.reverseName(name);
//    }

    @Then("my name reversed is {string}")
    public void name_check(String expectedName) {
        Assertions.assertEquals(expectedName, reversedName);
    }
}

