package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.CheckComboBox;
import seng202.team5.Environment;
import seng202.team5.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class of the general profile setup screen
 */
public class ProfileSetupGeneralController extends Controller {

    /**
     * Launches the screen with environment
     *
     * @param environment application environment
     */
    public ProfileSetupGeneralController(Environment environment, ScreenNavigator navigator) {
        super(environment, navigator);
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
        // TODO: use regions based on trail data in DB
        List<String> regionList = new ArrayList<>(List.of("Northland", "Auckland",
                "Waikato", "Bay of Plenty", "Gisborne", "Hawke's Bay", "Taranaki",
                "Manawatu-Whanganui", "Tasman", "Wellington", "Nelson", "Marlborough", "West Coast",
                "Canterbury", "Otago", "Southland"));

        usernameLabel.setText("Username");
        regionLabel.setText("Choose your region");
        usernameTextField.setPromptText("YompYomp User");
        regionCheckComboBox.getItems().addAll(regionList);
        familyFriendlyCheckBox.setSelected(false);
        accessibleCheckBox.setSelected(false);
        continueButton.setOnAction(e -> onContinueButtonClicked());
    }

    /**
     * Action method of continueButton
     * Sets attributes of User object and launches the quiz screen
     */
    @FXML
    private void onContinueButtonClicked() {
        setUserPreferences();
        super.getNavigator().launchScreen(new ProfileQuizController(super.getEnvironment(), super.getNavigator(), 1));
    }

    /**
     * Gets user input and sets attributes of User object
     */
    private void setUserPreferences() {
        User user = super.getEnvironment().getUser();
        if (usernameTextField.getText().isEmpty()) {
            user.setName("YompYomp User");
        } else {
            user.setName(usernameTextField.getText());
        }
        user.setRegion(regionCheckComboBox.getCheckModel().getCheckedItems());
        user.setIsFamilyFriendly(familyFriendlyCheckBox.isSelected());
        user.setIsAccessible(accessibleCheckBox.isSelected());
        // TODO: Save user preferences to database (and Environment?)
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/profile_setup_general.fxml";
    }

    @Override
    protected String getTitle() {
        return "Profile Setup Screen";
    }
}
