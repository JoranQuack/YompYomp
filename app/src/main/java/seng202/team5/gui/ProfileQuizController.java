package seng202.team5.gui;

import javafx.fxml.FXML;
import seng202.team5.models.Question;
import seng202.team5.models.User;
import javafx.scene.control.*;

/**
 * Controller class for profile quiz screens
 * Reuse the controller for the whole quiz
 */
public class ProfileQuizController extends Controller {

    // To keep track of which question the user is on
    private int quizId;

    /**
     * Constructs the controller with navigator
     *
     * @param navigator screen navigator
     * @param quizId    quiz question id
     */
    public ProfileQuizController(ScreenNavigator navigator, int quizId) {
        super(navigator);
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
    private ProgressBar quizProgressBar;

    /**
     * Initialises the quiz screen with the first two questions
     */
    @FXML
    private void initialize() {
        User user = super.getUserService().getUser();
        int[] sliderValues = getUserPreferences(user);

        setSliders(slider1, sliderValues[0]);
        setSliders(slider2, sliderValues[1]);
        setProgressBar();
        setQuestionLabel();
        if (quizId == 9) {
            continueButton.setText("Complete Quiz");
        } else {
            continueButton.setText("Continue");
        }
        continueButton.setOnAction(e -> onContinueButtonClicked());
    }

    /**
     * Sets the sliders to the default position
     *
     * @param slider Slider object on quiz screen
     */
    @FXML
    private void setSliders(Slider slider, int initialValue) {
        slider.setSnapToTicks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setMin(1);
        slider.setMax(5);
        slider.setValue(initialValue);
    }

    /**
     * Sets quiz progress shown on the progress bar
     */
    @FXML
    private void setProgressBar() {
        double progress = (quizId - 1) / 10.0;
        quizProgressBar.setProgress(progress);
    }

    /**
     * Sets labels of the questions based on quiz question id
     */
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

    private int[] getUserPreferences(User user) {
        if (user == null) {
            return new int[] {3, 3};
        }

        return switch (quizId) {
            case 1 -> new int[]{user.getExperienceLevel() == 0 ? 3 : user.getExperienceLevel(),
                                user.getGradientPreference() == 0 ? 3 : user.getGradientPreference()};
            case 3 -> new int[]{user.getBushPreference() == 0 ? 3 : user.getBushPreference(),
                                user.getReservePreference() == 0 ? 3 : user.getReservePreference()};
            case 5 -> new int[]{user.getLakeRiverPreference() == 0 ? 3 : user.getLakeRiverPreference(),
                                user.getCoastPreference() == 0 ? 3 : user.getCoastPreference()};
            case 7 -> new int[]{user.getMountainPreference() == 0 ? 3 : user.getMountainPreference(),
                                user.getWildlifePreference() == 0 ? 3 : user.getWildlifePreference()};
            case 9 -> new int[]{user.getHistoricPreference() == 0 ? 3 : user.getHistoricPreference(),
                                user.getWaterfallPreference() == 0 ? 3 : user.getWaterfallPreference()};
            default -> new int[]{3, 3};
        };
    }

    /**
     * Action method for continue button
     * Launches the quiz screen again with next questions if not at the end of quiz
     * Launches the matchmaking screen if at the end of quiz
     */
    @FXML
    private void onContinueButtonClicked() {
        setUserPreferences();
        incrementQuizId();
        if (quizId < 10) {
            super.getNavigator()
                    .launchScreen(new ProfileQuizController(super.getNavigator(), quizId), null);
        } else {
            // Mark the user's profile as complete and save the final state
            super.getUserService().markProfileComplete();
            super.getNavigator().launchScreen(new MatchmakingController(super.getNavigator()), null);
        }
    }

    /**
     * Sets labels on the slider based on the question shown
     *
     * @param sliderLabels array of labels based on question - taken from enum
     */
    @FXML
    private void setSliderLabels(Label minLabel, Label medLabel, Label maxLabel, String[] sliderLabels) {
        minLabel.setText(sliderLabels[0]);
        medLabel.setText(sliderLabels[1]);
        maxLabel.setText(sliderLabels[2]);
    }

    /**
     * Increments the quiz id
     */
    private void incrementQuizId() {
        this.quizId += 2;
    }

    /**
     * Gets values from sliders and sets attributes of a User object
     */
    private void setUserPreferences() {
        User user = super.getUserService().getUser();
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
        super.getUserService().setUser(user);
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
