package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button setUpProfileButton;
    @FXML
    private Button skipButton;
    @FXML
    private Button returningUserButton;

    /**
     * Initializes the welcome screen
     */
    @FXML
    private void initialize() {
        setUpProfileButton.setText("Set Up Profile");
        setUpProfileButton.setOnAction(e -> onSetUpProfileButtonClicked());
        skipButton.setText("Skip");
        skipButton.setOnAction(e -> onSkipButtonClicked());
        returningUserButton.setText("Returning User");
        // returningUserButton.setOnAction(e -> onReturningUserButtonClicked());
    }

    /**
     * onClicked action of setUpProfileButton
     */
    @FXML
    private void onSetUpProfileButtonClicked() {
        super.getEnvironment().getUser().setType("profiled");
        super.getNavigator()
                .launchScreen(new ProfileSetupGeneralController(super.getEnvironment(), super.getNavigator()));
    }

    @FXML
    private void onReturningUserButtonClicked() {
        // Shouldn't returning users be put straight into the dashboard? (bypass this
        // screen)
    }

    /**
     * onClicked action for skipButton
     */
    @FXML
    private void onSkipButtonClicked() {
        super.getEnvironment().getUser().setType("guest");
        super.getNavigator().launchScreen(new DashboardController(super.getEnvironment(), super.getNavigator()));
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
