package seng202.team5.services;

import org.hsqldb.DatabaseManager;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogService {

    private List<TrailLog> logs;
    private List<TrailLog> filteredLogs;
    private int maxResults = 50;
    private String currentSearchValue;
    private SqlBasedTrailRepo trailRepo;

    /**
     * Creates LogService with database-backed filter options.
     */
    public LogService(DatabaseService databaseService) {
        this.logs = new SqlBasedTrailLogRepo(databaseService).getAllTrailLogs();
        this.filteredLogs = logs;
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

    /**
     * Gets the total number of logs in the results.
     */
    public int getNumberOfLogs() {
        return logs.size();
    }

    public Trail getTrail(int id) {
        return new SqlBasedTrailRepo(new DatabaseService()).findById(id).get();
    }

    /**
     * Updates trails based on current filters and sort settings.
     */
    public void updateLogs() {
        String lower = currentSearchValue == null ? "" : currentSearchValue.toLowerCase();

        filteredLogs = logs.stream()
                .filter(t -> getTrail(t.getTrailId()).getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    /**
     * Gets a specific page of trails from the filtered results.
     *
     * @param page the page number (0-indexed)
     * @return list of trails for the specified page
     */
    public List<TrailLog> getPage(int page) {
        if (currentSearchValue != null) {
            updateLogs();
        }
        int startIndex = page * maxResults;
        return filteredLogs.stream()
                .skip(startIndex)
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    public void setCurrentQuery(String query) {
        currentSearchValue = query;
    }
}
