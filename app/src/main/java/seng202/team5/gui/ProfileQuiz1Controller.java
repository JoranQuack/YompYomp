package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import seng202.team5.Environment;

public class ProfileQuiz1Controller extends Controller {

    public ProfileQuiz1Controller(Environment environment, ScreenNavigator navigator) {
        super(environment, navigator);
    }

    @FXML
    private CheckBox familyFriendlyCheckBox;
    @FXML
    private CheckBox accessibleCheckBox;
    @FXML
    private Slider experienceSlider;
    @FXML
    private Slider gradientSlider;
    @FXML
    private Button continueButton;
    @FXML
    private ProgressBar quizProgressBar;

    /**
     * Initializes the first screen of profile quiz
     */
    @FXML
    private void initialize() {
        familyFriendlyCheckBox.setSelected(false);
        accessibleCheckBox.setSelected(false);
        experienceSlider.setMin(1);
        experienceSlider.setMax(5);
        experienceSlider.setValue(3);
        gradientSlider.setMin(1);
        gradientSlider.setMax(5);
        gradientSlider.setValue(3);
        quizProgressBar.setProgress(0);
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/profile_quiz_1.fxml";
    }

    @Override
    protected String getTitle() {
        return "Profile Quiz Screen 1";
    }
}