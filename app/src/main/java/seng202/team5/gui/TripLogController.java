package seng202.team5.gui;

import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.data.SqlBasedTripRepo;
import seng202.team5.models.TripLog;

/**
 * Controller for the trip log screen
 */
public class TripLogController extends Controller {

    private TripLog trip;
    private Controller lastController;
    private DatabaseService databaseService;
    private SqlBasedTrailRepo trailRepo;
    private SqlBasedTripRepo tripRepo;

    /**
     * Launches the screen with the navigator
     *
     * @param navigator screen navigator
     * @param lastController controller of the last screen the user interacted with
     */
    public TripLogController(ScreenNavigator navigator, TripLog trip, Controller lastController) {
        super(navigator);
        this.trip = trip;
        this.lastController = lastController;
        this.databaseService = new DatabaseService();
        this.trailRepo = new SqlBasedTrailRepo(databaseService);
        this.tripRepo = new SqlBasedTripRepo(databaseService);
    }




}
