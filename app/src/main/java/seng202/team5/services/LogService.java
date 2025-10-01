package seng202.team5.services;

import org.hsqldb.DatabaseManager;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.models.TrailLog;

import java.util.ArrayList;
import java.util.List;

public class LogService {

    private List<TrailLog> logs;
    private int maxResults = 50;
    private String currentSortBy = "name";

    /**
     * Creates LogService with database-backed filter options.
     */
    public LogService(DatabaseService databaseService) {
        this.logs = new SqlBasedTrailLogRepo(databaseService).getAllTrailLogs();
    }

    /**
     * Calculates the total number of pages required to display the currently
     * list of logs.
     */
    public int getNumberOfPages() {
        if (maxResults <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) logs.size() / maxResults);
    }


}
