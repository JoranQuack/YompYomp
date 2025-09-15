package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import seng202.team5.models.User;

public class WelcomeController extends Controller {

    /**
     * Creates controller with navigator.
     *
     * @param navigator ScreenNavigator for navigation
     */
    public WelcomeController(ScreenNavigator navigator) {
        super(navigator);
    }

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Button setUpProfileButton;

    @FXML
    private Button skipButton;

    /**
     * Initializes the welcome screen
     */
    @FXML
    private void initialize() {
        User existingUser = super.getUserService().getUser();
        if (existingUser != null) {
            titleLabel.setText("Welcome back, " + existingUser.getName() + "!");
            subtitleLabel.setText("Create a new profile or continue to the dashboard.");
            skipButton.setText("Continue");
            setUpProfileButton.setText("Create new profile");
        }
    }

    /**
     * onClicked action of setUpProfileButton
     */
    @FXML
    private void onSetUpProfileButtonClicked() {
        User user = super.getUserService().getUser();
        if (user == null) {
            user = new User();
        }
        user.setType("profiled");
        super.getUserService().setUser(user);
        super.getNavigator()
                .launchScreen(new ProfileSetupGeneralController(super.getNavigator()));
    }

    /**
     * onClicked action for skipButton
     */
    @FXML
    private void onSkipButtonClicked() {
        User user = super.getUserService().getUser();
        if (user == null) {
            user = new User();
            user.setType("guest");
            super.getUserService().setUser(user);
        }
        super.getNavigator().launchScreen(new DashboardController(super.getNavigator()));
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
