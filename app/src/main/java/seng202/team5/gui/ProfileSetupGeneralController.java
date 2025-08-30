package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng202.team5.Environment;
import seng202.team5.models.User;

import java.util.ArrayList;
import java.util.List;


public class ProfileSetupGeneralController extends Controller {

    public ProfileSetupGeneralController(Environment environment, ScreenNavigator navigator) {
        super(environment, navigator);
    }

    @FXML
    private TextField usernameTextField;
    @FXML
    private ChoiceBox<String> regionChoiceBox;
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
        List<String> regionList = new ArrayList<>(List.of("All", "Northland", "Auckland",
                "Waikato", "Bay of Plenty", "Gisborne", "Hawke's Bay", "Taranaki",
                "Manawatu-Whanganui", "Tasman", "Wellington", "Nelson", "Marlborough", "West Coast",
                "Canterbury", "Otago", "Southland"));

        usernameLabel.setText("Username");
        regionLabel.setText("Choose your region");
        usernameTextField.setPromptText("YompYomp User");
        regionChoiceBox.getItems().addAll(regionList);
        regionChoiceBox.setValue("All");
        familyFriendlyCheckBox.setSelected(false);
        accessibleCheckBox.setSelected(false);
        continueButton.setOnAction(e -> onContinueButtonClicked());
    }

    /**
     * Action method of continueButton
     */
    @FXML
    private void onContinueButtonClicked() {
        setUserPreferences();
        super.getEnvironment().getNavigator().launchScreen(
                new ProfileQuizController(super.getEnvironment(), super.getEnvironment().getNavigator(), 1)
        );
    }

    private void setUserPreferences() {
        User user = super.getEnvironment().getUser();
        if (usernameTextField.getText().isEmpty()) {
            user.setName("YompYomp User");
        } else {
            user.setName(usernameTextField.getText());
        }
        user.setRegion(regionChoiceBox.getSelectionModel().getSelectedItem());
        user.setIsFamilyFriendly(familyFriendlyCheckBox.isSelected());
        user.setIsAccessible(accessibleCheckBox.isSelected());
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
