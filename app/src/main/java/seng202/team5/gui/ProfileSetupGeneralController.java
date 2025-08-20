package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import seng202.team5.Environment;

public class ProfileSetupGeneralController extends Controller {

    public ProfileSetupGeneralController(seng202.team5.Environment Environment, ScreenNavigator navigator) {
        super(Environment, navigator);
    }

    @FXML
    private TextField usernameTextField;
    @FXML
    private ChoiceBox regionChoiceBox;
    @FXML
    private Button continueButton;

    @Override
    protected String getFxmlFile() {
        return "/fxml/profile_setup_general.fxml";
    }

    @Override
    protected String getTitle() {
        return "Profile Setup Screen";
    }
}
