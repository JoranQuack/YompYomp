package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.TrailLog;

/**
 * Controller for the trip log screen
 */
public class LogTrailController extends Controller {

    private TrailLog trailLog;
    private DatabaseService databaseService;
    private SqlBasedTrailRepo trailRepo;

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
        setupForm
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


}
