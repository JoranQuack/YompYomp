package seng202.team5.gui;

import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.data.SqlBasedTripRepo;
import seng202.team5.models.TrailLog;

/**
 * Controller for the trip log screen
 */
public class LogTrailController extends Controller {

    private TrailLog trailLog;
    private Controller lastController;
    private DatabaseService databaseService;
    private SqlBasedTrailRepo trailRepo;

    /**
     * Launches the screen with the navigator
     *
     * @param navigator screen navigator
     * @param lastController controller of the last screen the user interacted with
     */
    public LogTrailController(ScreenNavigator navigator, TrailLog trailLog, Controller lastController) {
        super(navigator);
        this.trailLog = trailLog;
        this.lastController = lastController;
        this.databaseService = new DatabaseService();
        this.trailRepo = new SqlBasedTrailRepo(databaseService);
        this.tripRepo = new SqlBasedTripRepo(databaseService);
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/log_trip.fxml";
    }

    @Override
    protected String getTitle() {
        return "Trip Log Screen";
    }


}
