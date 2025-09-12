package seng202.team5;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = { "pretty", "html:build/reports/tests/cucumber/feature-report.html" },
        glue = "seng202.acceptancetesting.cucumber.StepDefinitions",
        features = "src/test/resources/features",
        snippets = CucumberOptions.SnippetType.CAMELCASE)

public class RunCucumberTest {
}

