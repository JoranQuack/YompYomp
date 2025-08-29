package seng202.team5.gui;

import javafx.fxml.FXML;
import seng202.team5.Environment;
import seng202.team5.models.Question;
import seng202.team5.models.User;
import javafx.scene.control.*;
//import java.util.Arrays;

public class ProfileQuizController extends Controller {

    private int quizId;

    public ProfileQuizController(Environment environment, ScreenNavigator navigator, int quizId) {
        super(environment, navigator);
        this.quizId = quizId;
    }

    @FXML
    private Label questionLabel1;
    @FXML
    private Label questionLabel2;
    @FXML
    private Slider slider1;
    @FXML
    private Slider slider2;
    @FXML
    private Label minLabel1;
    @FXML
    private Label minLabel2;
    @FXML
    private Label maxLabel1;
    @FXML
    private Label maxLabel2;
    @FXML
    private Label medLabel1;
    @FXML
    private Label medLabel2;
    @FXML
    private Button continueButton;
    @FXML
    private ProgressBar progressBar;

    @FXML
    private void initialize() {
        setSliders(slider1);
        setSliders(slider2);
        setProgressBar();
        continueButton.setOnAction(e -> onContinueButtonClicked());
    }

    @FXML
    private void setSliders(Slider slider) {
        slider.setSnapToTicks(true);
        slider.setBlockIncrement(1);
        slider.setMin(1);
        slider.setMax(5);
        slider.setValue(3);
    }

    @FXML
    private void setProgressBar() {
        double progress = (quizId - 1) / 10.0;
        progressBar.setProgress(progress);
    }

    @FXML
    private void setQuestionLabel() {
        switch (quizId) {
            case 1:
                questionLabel1.setText(Question.ONE.question);
                setSliderLabels(minLabel1, medLabel1, maxLabel1, Question.ONE.sliderLabels);
                questionLabel2.setText(Question.TWO.question);
                setSliderLabels(minLabel2, medLabel2, maxLabel2, Question.TWO.sliderLabels);
                break;
            case 3:
                questionLabel1.setText(Question.THREE.question);
                setSliderLabels(minLabel1, medLabel1, maxLabel1, Question.THREE.sliderLabels);
                questionLabel2.setText(Question.FOUR.question);
                setSliderLabels(minLabel2, medLabel2, maxLabel2, Question.FOUR.sliderLabels);
                break;
            case 5:
                questionLabel1.setText(Question.FIVE.question);
                setSliderLabels(minLabel1, medLabel1, maxLabel1, Question.FIVE.sliderLabels);
                questionLabel2.setText(Question.SIX.question);
                setSliderLabels(minLabel2, medLabel2, maxLabel2, Question.SIX.sliderLabels);
                break;
            case 7:
                questionLabel1.setText(Question.SEVEN.question);
                setSliderLabels(minLabel1, medLabel1, maxLabel1, Question.SEVEN.sliderLabels);
                questionLabel2.setText(Question.EIGHT.question);
                setSliderLabels(minLabel2, medLabel2, maxLabel2, Question.EIGHT.sliderLabels);
                break;
            case 9:
                questionLabel1.setText(Question.NINE.question);
                setSliderLabels(minLabel1, medLabel1, maxLabel1, Question.NINE.sliderLabels);
                questionLabel2.setText(Question.TEN.question);
                setSliderLabels(minLabel2, medLabel2, maxLabel2, Question.TEN.sliderLabels);
                break;
        }
    }

    @FXML
    private void onContinueButtonClicked() {
        setUserPreferences();
        if (quizId < 10) {
            incrementQuizId();
            super.getEnvironment().getNavigator().launchScreen(
                    new ProfileQuizController(super.getEnvironment(), super.getEnvironment().getNavigator(), quizId)
            );
        }
        // TODO: add else clause to move to dashboard screen
    }

    @FXML
    private void setSliderLabels(Label minLabel, Label medLabel, Label maxLabel, String[] sliderLabels) {
        minLabel.setText(sliderLabels[0]);
        medLabel.setText(sliderLabels[1]);
        maxLabel.setText(sliderLabels[2]);
    }

    private void incrementQuizId() {
        this.quizId += 2;
    }

    private void setUserPreferences() {
        User user = super.getEnvironment().getUser();
        switch (quizId) {
            case 1:
                user.setExperienceLevel((int) slider1.getValue());
                user.setGradientPreference((int) slider2.getValue());
                break;
            case 3:
                user.setBushPreference((int) slider1.getValue());
                user.setReservePreference((int) slider2.getValue());
                break;
            case 5:
                user.setLakeRiverPreference((int) slider1.getValue());
                user.setCoastPreference((int) slider2.getValue());
                break;
            case 7:
                user.setMountainPreference((int) slider1.getValue());
                user.setWildlifePreference((int) slider2.getValue());
                break;
            case 9:
                user.setHistoricPreference((int) slider1.getValue());
                user.setWaterfallPreference((int) slider2.getValue());
                break;
        }
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/profile_quiz.fxml";
    }

    @Override
    protected String getTitle() {
        return "Profile Quiz Screen";
    }
}
