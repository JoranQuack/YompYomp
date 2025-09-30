package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.TrailLog;

import java.util.List;

/**
 * Controller for the trip log screen
 */
public class LogTrailController extends Controller {

    private TrailLog trailLog;
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
