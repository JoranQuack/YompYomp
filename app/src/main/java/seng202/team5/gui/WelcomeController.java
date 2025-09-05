package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
        User user = super.getUserService().getUser();
        if (user == null) {
            user = new User();
        }
        user.setType("profiled");
        super.getUserService().setUser(user);
        super.getNavigator()
                .launchScreen(new ProfileSetupGeneralController(super.getNavigator()));
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
        User user = super.getUserService().getUser();
        if (user == null) {
            user = new User();
        }
        user.setType("guest");
        super.getUserService().setUser(user);
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
