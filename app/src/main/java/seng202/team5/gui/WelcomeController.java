package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import seng202.team5.Environment;

public class WelcomeController extends Controller {
    /**
     * Creates controller with environment.
     *
     * @param Environment Application environment
     */
    public WelcomeController(Environment Environment, ScreenNavigator navigator) {
        super(Environment, navigator);
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
        setUpProfileButton.setOnAction(e -> {
            super.getEnvironment().getNavigator().launchScreen(
                    new SetupController(super.getEnvironment(), super.getEnvironment().getNavigator()));
        });
    }

    /**
     * onClicked action of setUpProfileButton
     */
    @FXML
    private void onSetUpProfileButtonClicked() {
        // TODO: link to profile setup screen
    }

    /**
     * onClicked action for skipButton
     */
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
