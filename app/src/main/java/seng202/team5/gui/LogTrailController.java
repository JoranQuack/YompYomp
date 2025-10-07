package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng202.team5.data.DatabaseService;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import seng202.team5.services.LogService;
import seng202.team5.utils.StringManipulator;

import java.util.List;

/**
 * Controller for the trail log screen
 */
public class LogTrailController extends Controller {

    private TrailLog trailLog;
    private Trail trail;
    private LogService logService;
    private DatabaseService databaseService;

    /**
     * Launches the screen with the navigator
     *
     * @param navigator screen navigator
     * @param trailLog  the trail log to be logged
     */
    public LogTrailController(ScreenNavigator navigator, Trail trail, TrailLog trailLog) {
        super(navigator);
        this.trail = trail;
        this.trailLog = trailLog;
        this.databaseService = new DatabaseService();
        this.logService = new LogService(databaseService);
    }

    @FXML
    private Button backButton;
    @FXML
    private Label trailNameLabel;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField durationTextField;
    @FXML
    private ChoiceBox<String> timeUnitSelector;
    @FXML
    private ChoiceBox<String> trailTypeSelector;
    @FXML
    private Slider rateSlider;
    @FXML
    private ChoiceBox<String> perceivedDifficultySelector;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private Button doneButton;
    @FXML
    private Label errorLabel;

    /**
     * Initialises the screen with components for the user to input data
     */
    @FXML
    private void initialize() {
        setupFormFields();
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
        if (trail != null && trailLog != null) {
            populateFields();
        }
        backButton.setOnAction(event -> onBackButtonClicked());
        doneButton.setOnAction(event -> onDoneButtonClicked());

        durationTextField.textProperty().addListener((obs, oldVal, newVal) -> clearErrors());
    }

    /**
     * Sets up the form fields with the data from the trailLog
     */
    private void setupFormFields() {
        timeUnitSelector.getItems().addAll(List.of("Minutes", "Hours", "Days"));
        trailTypeSelector.getItems().addAll(List.of("One way", "Loop", "Return"));
        perceivedDifficultySelector.getItems().addAll(List.of("Easiest", "Easy", "Intermediate", "Advanced", "Expert"));
    }

    /**
     * Populates the form fields with the data from the trailLog or prefills it with
     * trail info that can be edited.
     */
    private void populateFields() {
        trailNameLabel.setText(trail.getName());
        if (trailLog.getStartDate() != null) {
            startDatePicker.setValue(trailLog.getStartDate());
        }

        if (trailLog.getCompletionTime() != null) {
            durationTextField.setText(trailLog.getCompletionTime().toString());
            timeUnitSelector.setValue(StringManipulator.capitaliseFirstLetter(trailLog.getTimeUnit()));
        } else {
            int avgMinutes = trail.getAvgCompletionTimeMinutes();
            if (avgMinutes >= 60) {
                int hours = avgMinutes / 60;
                durationTextField.setText(String.valueOf(hours));
                timeUnitSelector.setValue("Hours");
            } else {
                durationTextField.setText(String.valueOf(avgMinutes));
                timeUnitSelector.setValue("Minutes");
            }
        }

        trailTypeSelector.setValue(
                trailLog.getCompletionType() != null
                        ? StringManipulator.capitaliseFirstLetter(trailLog.getCompletionType())
                        : getValidCompletionType(trail));
        perceivedDifficultySelector.setValue(
                trailLog.getPerceivedDifficulty() != null
                        ? StringManipulator.capitaliseFirstLetter(trailLog.getPerceivedDifficulty())
                        : StringManipulator.capitaliseFirstLetter(trail.getDifficulty()));
        rateSlider.setValue(trailLog.getRating() != null ? trailLog.getRating() : 3);
        noteTextArea.setText(trailLog.getNotes());
    }

    /**
     * Gets a valid completion type for the trail if the trail type is unknown.
     *
     * @param trail the trail to get the completion type for
     * @return the valid completion type
     */
    private String getValidCompletionType(Trail trail) {
        String type = trail.getCompletionType();
        if (type == null || type.equalsIgnoreCase("unknown")) {
            return "One way";
        }
        return StringManipulator.capitaliseFirstLetter(type);
    }

    /**
     * Clears any error styling on the form field
     */
    private void clearErrors() {
        durationTextField.setStyle("");
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    /**
     * Shows an error message on the form field.
     *
     * @param control the control to show the error on
     * @param message the error message to show
     */
    private void showError(Control control, String message) {
        clearErrors();
        control.setStyle("-fx-border-color: red");
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }

    /**
     * Validates the form field.
     *
     * @return true if the form field is valid, false otherwise
     */
    private boolean validateFields() {
        String durationText = durationTextField.getText().trim();
        if (durationText.isEmpty()) {
            showError(durationTextField, "Please enter a duration");
            return false;
        }

        try {
            int duration = Integer.parseInt(durationText);
            if (duration <= 0) {
                showError(durationTextField, "Duration must be a positive integer");
                return false;
            }
        } catch (NumberFormatException e) {
            showError(durationTextField, "Duration must be a valid number");
            return false;
        }

        return true;
    }

    /**
     * Handles the event when the done button is clicked.
     */
    @FXML
    private void onDoneButtonClicked() {
        if (!validateFields()) {
            return;
        }
        trailLog.setStartDate(startDatePicker.getValue());
        trailLog.setCompletionTime(
                !durationTextField.getText().isEmpty() ? Integer.parseInt(durationTextField.getText()) : null);
        trailLog.setTimeUnit(timeUnitSelector.getValue());
        trailLog.setCompletionType(trailTypeSelector.getValue());
        trailLog.setRating((int) rateSlider.getValue());
        trailLog.setPerceivedDifficulty(perceivedDifficultySelector.getValue());
        trailLog.setNotes(noteTextArea.getText());

        logService.addLog(trailLog);
        super.getNavigator().goBack();
    }

    /**
     * Handles the event when the back button is clicked.
     */
    @FXML
    private void onBackButtonClicked() {
        super.getNavigator().goBack();
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/log_trail.fxml";
    }

    @Override
    protected String getTitle() {
        return "Trip Log Screen";
    }

    @Override
    protected boolean shouldShowNavbar() {
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return 1; // Trails section
    }
}
