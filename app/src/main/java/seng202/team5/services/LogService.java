package seng202.team5.services;

import seng202.team5.data.DatabaseService;
import seng202.team5.data.ITrailLog;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LogService {

    private ITrailLog trailInterface;
    private SqlBasedTrailLogRepo trailLogRepo;
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
        this.trailInterface = new SqlBasedTrailLogRepo(databaseService);
        this.trailLogRepo = new SqlBasedTrailLogRepo(databaseService);
        this.trailRepo = new SqlBasedTrailRepo(databaseService);
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

    public Optional<TrailLog> getTrailLog(int logId) {
        return trailInterface.findById(logId);
    }

    public Optional<Trail> getTrail(int trailId) {
        return trailRepo.findById(trailId);
    }

    /**
     * Updates trails based on current filters and sort settings.
     */
    public void updateLogs() {
        String lower = currentSearchValue == null ? "" : currentSearchValue.toLowerCase();

        filteredLogs = logs.stream()
                .filter(t -> {
                    Optional<Trail> trail = trailRepo.findById(t.getTrailId());
                    return trail.isPresent() && trail.get().getName().toLowerCase().contains(lower);
                })
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

    public List<TrailLog> getAllLogs() {
        return trailInterface.getAllTrailLogs();
    }

    public void addLog(TrailLog trailLog) {
        trailLogRepo.upsert(trailLog);
    }

    public void updateLog(TrailLog trailLog) {
        trailLogRepo.upsert(trailLog);
    }

    public void deleteLog(int logId) {
        trailLogRepo.deleteById(logId);
    }

    public int countLogs() {
        return trailInterface.countTrailLogs();
    }

    public boolean isTrailLogged(int trailId) {
        return getAllLogs().stream().anyMatch(log -> log.getTrailId() == trailId);
    }

}
