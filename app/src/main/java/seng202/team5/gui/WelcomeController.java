package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import seng202.team5.models.Trail;

import java.util.List;

public class WelcomeController extends Controller {
    /**
     * Creates controller with environment.
     *
     * @param Environment Application environment
     */
    protected WelcomeController(seng202.team5.Environment Environment) {
        super(Environment);
    }

    @FXML
    private Label kiaOraLabel;
    @FXML
    private Label welcomeTextLabel;
    @FXML
    private Button setUpProfileButton;
    @FXML
    private Button skipButton;

    /**
     * Initializes the welcome screen
     */
    @FXML
    private void initialize() {
        setUpProfileButton.setText("Set Up Profile");
        skipButton.setText("Skip");
        // TODO: initialise onClicked for buttons here
    }

    @FXML
    private void onSetUpProfileButtonClicked() {
        // TODO: link to profile setup screen
    }

    @FXML
    private void onSkipButtonClicked() {
        // TODO: link to guest screen
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/welcome.fxml";
    }

    @Override
    protected String getTitle() {
        return "Welcome to YompYomp!";
    }
}
