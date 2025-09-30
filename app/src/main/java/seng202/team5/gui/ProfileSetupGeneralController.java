package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.CheckComboBox;
import seng202.team5.models.User;
import seng202.team5.services.RegionFinder;
import seng202.team5.services.UserService;

import java.util.List;

/**
 * Controller class of the general profile setup screen
 */
public class ProfileSetupGeneralController extends Controller {

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
     */
    public ProfileSetupGeneralController(ScreenNavigator navigator) {
        super(navigator);
    }

    @FXML
    private TextField usernameTextField;
    @FXML
    private CheckComboBox<String> regionCheckComboBox;
    @FXML
    private Button continueButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private CheckBox familyFriendlyCheckBox;
    @FXML
    private CheckBox accessibleCheckBox;

    /**
     * Initializes the first profile setup screen
     */
    @FXML
    private void initialize() {
        super.getUserService().setGuest(false);
        User user = super.getUserService().getUser();

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
    }

    /**
     * Action method of continueButton
     * Sets attributes of User object and launches the quiz screen
     */
    @FXML
    private void onContinueButtonClicked() {
        boolean isNameValid = setUserPreferences();

        if (isNameValid) {
            super.getNavigator().launchScreen(new ProfileQuizController(super.getNavigator(), 1));
        }
    }

    /**
     * Gets user input and sets attributes of User object
     */
    private boolean setUserPreferences() {
        String name = usernameTextField.getText().trim();
        UserService userService = super.getUserService();

        if (!userService.isValidName(name)) {
            usernameTextField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            usernameLabel.setText("Invalid name. Please try again.");
            return false;
        } else {
            usernameTextField.setStyle("");
            usernameLabel.setText("Username");
        }

        User user = super.getUserService().getUser();
        if (usernameTextField.getText().isEmpty()) {
            user.setName("YompYomp User");
        } else {
            user.setName(usernameTextField.getText());
        }
        user.setRegion(regionCheckComboBox.getCheckModel().getCheckedItems());
        user.setIsFamilyFriendly(familyFriendlyCheckBox.isSelected());
        user.setIsAccessible(accessibleCheckBox.isSelected());

        userService.setUser(user);
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
