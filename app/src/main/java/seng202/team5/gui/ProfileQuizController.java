package seng202.team5.gui;

import javafx.fxml.FXML;
import seng202.team5.Environment;
import seng202.team5.models.Question;
import seng202.team5.models.User;
import javafx.scene.control.*;
import java.util.Arrays;

public class ProfileQuizController extends Controller {

    private User user;
    private int quizId;

    public ProfileQuizController(seng202.team5.Environment environment, ScreenNavigator navigator, int quizId) {
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
        // TODO: method to update progress bar
    }

    @FXML
    private void setSliders(Slider slider) {
        slider.setMin(1);
        slider.setMax(5);
        slider.setValue(3);
    }

    @FXML
    private void setQuestionLabel() {
        switch(this.quizId) {
            case 1:
                questionLabel1.setText(Question.ONE.question);
                minLabel1.setText(Question.ONE.sliderLabels[0]);
                medLabel1.setText(Question.ONE.sliderLabels[1]);
                maxLabel1.setText(Question.ONE.sliderLabels[2]);
                questionLabel2.setText(Question.TWO.question);
                minLabel2.setText(Question.TWO.sliderLabels[0]);
                medLabel2.setText(Question.TWO.sliderLabels[1]);
                maxLabel2.setText(Question.TWO.sliderLabels[2]);
                break;
            case 3:
                questionLabel1.setText(Question.THREE.question);
                minLabel1.setText(Question.THREE.sliderLabels[0]);
                medLabel1.setText(Question.THREE.sliderLabels[1]);
                maxLabel1.setText(Question.THREE.sliderLabels[2]);
                questionLabel2.setText(Question.FOUR.question);
                minLabel2.setText(Question.FOUR.sliderLabels[0]);
                medLabel2.setText(Question.FOUR.sliderLabels[1]);
                maxLabel2.setText(Question.FOUR.sliderLabels[2]);
                break;
            case 5:
                questionLabel1.setText(Question.FIVE.question);
                minLabel1.setText(Question.FIVE.sliderLabels[0]);
                medLabel1.setText(Question.FIVE.sliderLabels[1]);
                maxLabel1.setText(Question.FIVE.sliderLabels[2]);
                questionLabel2.setText(Question.SIX.question);
                minLabel2.setText(Question.SIX.sliderLabels[0]);
                medLabel2.setText(Question.SIX.sliderLabels[1]);
                maxLabel2.setText(Question.SIX.sliderLabels[2]);
                break;
            case 7:
                questionLabel1.setText(Question.SEVEN.question);
                minLabel1.setText(Question.SEVEN.sliderLabels[0]);
                medLabel1.setText(Question.SEVEN.sliderLabels[1]);
                maxLabel1.setText(Question.SEVEN.sliderLabels[2]);
                questionLabel2.setText(Question.EIGHT.question);
                minLabel2.setText(Question.EIGHT.sliderLabels[0]);
                medLabel2.setText(Question.EIGHT.sliderLabels[1]);
                maxLabel2.setText(Question.EIGHT.sliderLabels[2]);
                break;
            case 9:
                questionLabel1.setText(Question.NINE.question);
                minLabel1.setText(Question.NINE.sliderLabels[0]);
                medLabel1.setText(Question.NINE.sliderLabels[1]);
                maxLabel1.setText(Question.NINE.sliderLabels[2]);
                questionLabel2.setText(Question.TEN.question);
                minLabel2.setText(Question.TEN.sliderLabels[0]);
                medLabel2.setText(Question.TEN.sliderLabels[1]);
                maxLabel2.setText(Question.TEN.sliderLabels[2]);
                break;
        }
    }

    @FXML
    private void onContinueButtonClicked() {
        incrementQuizId();
        super.getEnvironment().getNavigator().launchScreen(
                new ProfileQuizController(super.getEnvironment(), super.getEnvironment().getNavigator(), quizId)
        );
    }

    private void setSliderLabels(String[] sliderLabels) {

    }

    private void incrementQuizId() {
        this.quizId += 2;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getQuizId() {
        return this.quizId;
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
