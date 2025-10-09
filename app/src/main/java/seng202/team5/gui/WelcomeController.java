package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import seng202.team5.App;
import seng202.team5.gui.util.BackgroundImageUtil;
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

    @FXML
    private ImageView bgImage;

    @FXML
    private StackPane rootStackPane;

    /**
     * Initializes the welcome screen
     */
    @FXML
    private void initialize() {

        BackgroundImageUtil.setupCoverBehavior(bgImage, rootStackPane);

        App.getUserService().cleanupIncompleteProfiles();

        User existingUser = App.getUserService().getUser();
        if (existingUser != null && existingUser.isProfileComplete()) {
            titleLabel.setText("Welcome back, " + existingUser.getName() + "!");
            subtitleLabel.setText("Create a new profile or continue to the dashboard.");
            setUpProfileButton.setText("Create new profile");
            skipButton.setText("Continue");
        } else {
            App.getUserService().clearUser();
        }
    }

    /**
     * onClicked action of setUpProfileButton
     */
    @FXML
    private void onSetUpProfileButtonClicked() {
        super.getNavigator()
                .launchScreen(new ProfileSetupGeneralController(super.getNavigator()));
    }

    /**
     * onClicked action for skipButton
     */
    @FXML
    private void onSkipButtonClicked() {
        super.getNavigator().launchScreen(new LoadingController(super.getNavigator(), null));
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/welcome.fxml";
    }

    @Override
    protected String getTitle() {
        return "Welcome to YompYomp!";
    }

    @Override
    protected boolean shouldShowNavbar() {
        return false;
    }

    @Override
    protected int getNavbarPageIndex() {
        return -1; // No navbar
    }
}
