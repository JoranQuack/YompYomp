package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng202.team5.Environment;
import seng202.team5.models.User;

public class ProfileQuiz1Controller extends Controller {

    private User user;

    public ProfileQuiz1Controller(Environment environment, ScreenNavigator navigator) {
        super(environment, navigator);
    }

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
        experienceSlider.setMin(1);
        experienceSlider.setMax(5);
        experienceSlider.setValue(3);
        gradientSlider.setMin(1);
        gradientSlider.setMax(5);
        gradientSlider.setValue(3);
        quizProgressBar.setProgress(0);
        continueButton.setOnAction(e -> onContinueButtonClicked());
    }

    @FXML
    private void onContinueButtonClicked() {
        //user
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