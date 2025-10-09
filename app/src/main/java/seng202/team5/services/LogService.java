package seng202.team5.services;

import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import seng202.team5.utils.CompletionTimeParser;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LogService {

    private final SqlBasedTrailLogRepo trailLogRepo;
    private List<TrailLog> logs;
    private List<TrailLog> filteredLogs;
    private int maxResults = 50;
    private String currentSearchValue;
    private SqlBasedTrailRepo trailRepo;

    /**
     * Creates LogService with database-backed searching and pagination.
     */
    public LogService(DatabaseService databaseService) {
        this.trailLogRepo = new SqlBasedTrailLogRepo(databaseService);
        this.logs = trailLogRepo.getAllTrailLogs();
        this.filteredLogs = logs;
        this.trailRepo = new SqlBasedTrailRepo(databaseService);
    }

    public LogService(SqlBasedTrailLogRepo trailLogRepo, SqlBasedTrailRepo trailRepo) {
        this.trailLogRepo = trailLogRepo;
        this.logs = trailLogRepo.getAllTrailLogs();
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

    /**
     * Gets a trail log by its trail id.
     *
     * @param trailId the trail id to search for
     * @return the trail log, if it exists
     */
    public Optional<TrailLog> getLogByTrailId(int trailId) {
        return trailLogRepo.findByTrailId(trailId);
    }

    public void setCurrentQuery(String query) {
        currentSearchValue = query;
    }

    public List<TrailLog> getAllLogs() {
        return trailLogRepo.getAllTrailLogs();
    }

    public void addLog(TrailLog trailLog) {
        trailLogRepo.upsert(trailLog);
    }

    public Optional<Trail> getTrail(int trailId) {
        return trailRepo.findById(trailId);
    }

    public void deleteLog(int logId) {
        trailLogRepo.deleteById(logId);
    }

    public int countLogs() {
        return trailLogRepo.countTrailLogs();
    }

    public boolean isTrailLogged(int trailId) {
        return getAllLogs().stream().anyMatch(log -> log.getTrailId() == trailId);
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public TrailLog createLogFromTrail(Trail trail) {
        return new TrailLog(trailLogRepo.getNewTrailLogId(), trail.getId(), LocalDate.now(),
                (int) CompletionTimeParser.convertFromMinutes(trail.getAvgCompletionTimeMinutes()).value(),
                CompletionTimeParser.convertFromMinutes(trail.getAvgCompletionTimeMinutes()).unit(),
                trail.getCompletionType().contains("unknown") ? "one way" : trail.getCompletionType(), 3,
                trail.getDifficulty(), "");
    }

}
