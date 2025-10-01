package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import seng202.team5.services.LogService;

import java.time.ZoneId;
import java.util.List;

/**
 * Controller for the trip log screen
 */
public class LogTrailController extends Controller {

    private TrailLog trailLog;
    private Trail trail;
    private LogService logService;
    private DatabaseService databaseService;
    private SqlBasedTrailRepo trailRepo;
    private SqlBasedTrailLogRepo trailLogRepo;

    /**
     * Launches the screen with the navigator
     *
     * @param navigator screen navigator
     * @param trailLog  the trail log to be logged
     */
    public LogTrailController(ScreenNavigator navigator, TrailLog trailLog) {
        super(navigator);
        this.trailLog = trailLog;
        this.databaseService = new DatabaseService();
        this.trailRepo = new SqlBasedTrailRepo(databaseService);
    }

    public void setTrailAndLog(Trail trail, TrailLog trailLog) {
        this.trail = trail;
        this.trailLog = trailLog;
        this.logService = new LogService(databaseService);
        populateFields();
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

    /**
     * Initialises the screen with components for the user to input data
     */
    @FXML
    private void initialize() {
        setupFormFields();
    }

    /**
     * Sets up the form fields with the data from the trailLog
     */
    private void setupFormFields() {
        timeUnitSelector.getItems().addAll(List.of("Minutes", "Hours", "Days"));
        trailTypeSelector.getItems().addAll(List.of("One way", "Loop", "Return"));
        perceivedDifficultySelector.getItems().addAll(List.of("Easiest", "Easy", "Intermediate", "Advanced", "Expert"));
    }

    private void populateFields() {
        trailNameLabel.setText(trail.getName());
        startDatePicker.setValue(trailLog.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        durationTextField.setText(trailLog.getCompletionTime() != null ? trailLog.getCompletionTime().toString() : "");
        timeUnitSelector.setValue(trailLog.getTimeUnit());
        trailTypeSelector.setValue(trailLog.getCompletionType());
        rateSlider.setValue(trailLog.getRating() != null ? trailLog.getRating() : 3);
        perceivedDifficultySelector.setValue(trailLog.getPerceivedDifficulty());
        noteTextArea.setText(trailLog.getNotes());
    }

    @FXML
    private void onDoneButtonClicked() {
        trailLog.setStartDate(java.sql.Date.valueOf(startDatePicker.getValue()));
        trailLog.setCompletionTime(!durationTextField.getText().isEmpty() ? Integer.parseInt(durationTextField.getText()) : null);
        trailLog.setTimeUnit(timeUnitSelector.getValue());
        trailLog.setCompletionType(trailTypeSelector.getValue());
        trailLog.setRating((int) rateSlider.getValue());
        trailLog.setPerceivedDifficulty(perceivedDifficultySelector.getValue());
        trailLog.setNotes(noteTextArea.getText());

        logService.updateLog(trailLog);
        // TODO implement confirmation label: Trail log saved successfully (not alert tho)
        super.getNavigator().goBack();
    }

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
        return false;
    }

    @Override
    protected int getNavbarPageIndex() {
        return 1; // Trails section
    }
}
