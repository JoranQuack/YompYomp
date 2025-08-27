package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng202.team5.Environment;

import java.util.ArrayList;
import java.util.List;


public class ProfileSetupGeneralController extends Controller {

    public ProfileSetupGeneralController(Environment environment, ScreenNavigator navigator) {
        super(environment, navigator);
    }

    @FXML
    private TextField usernameTextField;
    @FXML
    private ChoiceBox regionChoiceBox;
    @FXML
    private Button continueButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label regionLabel;

    /**
     * Initializes the first profile setup screen
     */
    @FXML
    private void initialize() {
        List<String> regionList = new ArrayList<>(List.of("Northland", "Auckland",
                "Waikato", "Bay of Plenty", "Gisborne", "Hawke's Bay", "Taranaki",
                "Manawatu-Whanganui", "Wellington", "Nelson", "Marlborough", "West Coast",
                "Canterbury", "Otago", "Southland"));

        usernameLabel.setText("Username");
        regionLabel.setText("Choose your region");
        usernameTextField.setPromptText("YompYomp User");
        regionChoiceBox.getItems().addAll(regionList);
        continueButton.setOnAction(e -> onContinueButtonClicked());
    }

    /**
     * Action method of continueButton
     */
    @FXML
    private void onContinueButtonClicked() {

        super.getEnvironment().getNavigator().launchScreen(
                new ProfileQuiz1Controller(super.getEnvironment(), super.getEnvironment().getNavigator())
        );
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
