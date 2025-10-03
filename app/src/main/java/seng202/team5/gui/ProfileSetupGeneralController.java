package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import org.controlsfx.control.CheckComboBox;

import seng202.team5.gui.util.BackgroundImageUtil;
import seng202.team5.models.User;
import seng202.team5.services.RegionFinder;

import java.util.List;

/**
 * Controller class of the general profile setup screen
 */
public class ProfileSetupGeneralController extends Controller {
    private User user;

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
     */
    public ProfileSetupGeneralController(ScreenNavigator navigator) {
        super(navigator);
        user = new User();
    }

    @FXML
    private TextField usernameTextField;
    @FXML
    private CheckComboBox<String> regionCheckComboBox;
    @FXML
    private Button continueButton;
    @FXML
    private Button skipSetupButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private CheckBox familyFriendlyCheckBox;
    @FXML
    private CheckBox accessibleCheckBox;
    @FXML
    private StackPane rootPane;
    @FXML
    private ImageView bgImage;

    /**
     * Initializes the first profile setup screen
     */
    @FXML
    private void initialize() {
        System.out.println();
        BackgroundImageUtil.setupCoverBehavior(bgImage, rootPane);

        super.getUserService().setGuest(false);

        RegionFinder regionFinder = new RegionFinder();
        List<String> regionList = regionFinder.getRegionNames();
        regionCheckComboBox.getItems().addAll(regionList);

        if (user != null) {
            usernameTextField.setText(user.getName());
            regionCheckComboBox.getCheckModel().clearChecks();
            if (user.getRegion() != null) {
                for (String region : user.getRegion()) {
                    regionCheckComboBox.getCheckModel().check(region);
                }
            }
            familyFriendlyCheckBox.setSelected(user.isFamilyFriendly());
            accessibleCheckBox.setSelected(user.isAccessible());
        } else {
            usernameTextField.setPromptText("Guest User");
            familyFriendlyCheckBox.setSelected(false);
            accessibleCheckBox.setSelected(false);
        }

        continueButton.setOnAction(e -> onContinueButtonClicked());
        skipSetupButton.setOnAction(e -> onSkipSetupButtonClicked());
    }

    /**
     * Action method of continueButton
     * Sets attributes of User object and launches the quiz screen
     */
    @FXML
    private void onContinueButtonClicked() {
        boolean isNameValid = setUserPreferences();

        if (isNameValid) {
            super.getNavigator().launchScreen(new ProfileQuizController(super.getNavigator(), 1, user));
        }
    }

    /**
     * Action method of skipSetupButton
     * Skips profile setup quiz
     * Default to previous profile if one has been made prior, else continues as guest
     */
    @FXML
    private void onSkipSetupButtonClicked() {
        User prevUser = super.getUserService().getUser();
        if (prevUser != null && prevUser.getName() != null) {
            user = prevUser;
        } else {
            super.getUserService().setGuest(true);
            user = null;
        }
        super.getNavigator().launchScreen(new LoadingController(super.getNavigator(), user));
    }

    /**
     * Gets user input and sets attributes of User object
     *
     * @return true if name is valid, false otherwise
     */
    private boolean setUserPreferences() {
        String name = usernameTextField.getText();

        if (name == null || name.trim().isEmpty() || !super.getUserService().isValidName(name.trim())) {
            usernameTextField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            usernameLabel.setText("Invalid name. Please try again.");
            return false;
        }
        usernameTextField.setStyle("");
        usernameLabel.setText("Username");

        user.setName(name.trim());

        user.setRegion(regionCheckComboBox.getCheckModel().getCheckedItems());
        user.setIsFamilyFriendly(familyFriendlyCheckBox.isSelected());
        user.setIsAccessible(accessibleCheckBox.isSelected());
        return true;
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/profile_setup_general.fxml";
    }

    @Override
    protected String getTitle() {
        return "Profile Setup Screen";
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
