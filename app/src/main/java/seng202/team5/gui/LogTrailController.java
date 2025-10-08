package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng202.team5.App;
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
    public LogTrailController(ScreenNavigator navigator, Trail trail) {
        super(navigator);
        this.trail = trail;
        this.databaseService = App.getDatabaseService();
        this.logService = new LogService(databaseService);
        this.trailLog = logService.getLogByTrailId(trail.getId()).orElse(logService.createLogFromTrail(trail));
    }

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
        populateFields();

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
        startDatePicker.setValue(trailLog.getStartDate());
        durationTextField.setText(trailLog.getCompletionTime().toString());
        timeUnitSelector.setValue(StringManipulator.capitaliseFirstLetter(trailLog.getTimeUnit()));

        trailTypeSelector.setValue(StringManipulator.capitaliseFirstLetter(trailLog.getCompletionType()));
        perceivedDifficultySelector
                .setValue(StringManipulator.capitaliseFirstLetter(trailLog.getPerceivedDifficulty()));
        rateSlider.setValue(trailLog.getRating());
        noteTextArea.setText(trailLog.getNotes());
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
        super.getNavigator().launchScreen(new LogBookController(getNavigator()));
    }

    @FXML
    private void onDeleteButtonClicked() {
        boolean confirmed = super.showAlert("Log deletion", "Are you sure you want to delete this log?",
                "This action cannot be undone.",
                "Delete", "Cancel", "bg-red");
        if (!confirmed)
            return;
        logService.deleteLog(trailLog.getId());
        super.getNavigator().goBack();
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/log_trail.fxml";
    }

    @Override
    protected String getTitle() {
        return "Log Trail";
    }

    @Override
    protected boolean shouldShowNavbar() {
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return 2; // Logbook page
    }
}
