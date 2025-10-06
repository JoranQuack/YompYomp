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

    private final ITrailLog trailInterface;
    private List<TrailLog> logs;
    private List<TrailLog> filteredLogs;
    private int maxResults = 50;
    private String currentSearchValue;
    //TODO move this to use ITrail instead when refactor is complete
    private SqlBasedTrailRepo trailRepo;

    /**
     * Creates LogService with database-backed searching and pagnation.
     */
    public LogService(DatabaseService databaseService) {
        this.trailInterface = new SqlBasedTrailLogRepo(databaseService);
        this.logs = trailInterface.getAllTrailLogs();
        this.filteredLogs = logs;
        //TODO move this to use ITrail instead when refactor is complete
        this.trailRepo = new SqlBasedTrailRepo(databaseService);
    }

    //TODO change this constructor to be passed ITrail instead of the repo when the refactor is complete
    //TODO this means logServiceTest class is going to need to be updated aswell
    public LogService(ITrailLog trailInterface, SqlBasedTrailRepo trailRepo) {
        this.trailInterface = trailInterface;
        this.logs = trailInterface.getAllTrailLogs();
        this.filteredLogs = logs;
        this.trailRepo = trailRepo;
    }

    /**
     * Calculates the total number of pages required to display the currently
     * list of logs.
     */
    public int getNumberOfPages() {
        if (maxResults <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) filteredLogs.size() / maxResults);
    }

    /**
     * Updates logs based on current search
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
     * Gets a specific page of logs from the filtered results.
     *
     * @param page the page number (0-indexed)
     * @return list of logs for the specified page
     */
    public List<TrailLog> getPage(int page) {
        if (currentSearchValue != "") {
            updateLogs();
        }
        int startIndex = page * maxResults;
        return filteredLogs.stream()
                .skip(startIndex)
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    public Optional<TrailLog> getLogByTrailId(int trailId) {
        return getAllLogs()
                .stream()
                .filter(log -> log.getTrailId() == trailId)
                .findFirst();
    }

    public void setCurrentQuery(String query) {
        currentSearchValue = query;
    }
    public List<TrailLog> getAllLogs() {
        return trailInterface.getAllTrailLogs();
    }
    public void addLog(TrailLog trailLog) {
        trailInterface.upsert(trailLog);
    }
    public Optional<Trail> getTrail(int trailId) {
        return trailRepo.findById(trailId);
    }
    public void deleteLog(int logId) { trailInterface.deleteById(logId); }
    public int countLogs() { return trailInterface.countTrailLogs(); }
    public boolean isTrailLogged(int trailId) { return getAllLogs().stream().anyMatch(log -> log.getTrailId() == trailId); }
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

}
