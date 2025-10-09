package seng202.team5.services;

import seng202.team5.App;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import seng202.team5.models.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating account statistics from user trail logs and prefs
 */
public class AccountStatisticsService {

    private final SqlBasedTrailRepo trailRepo;
    private final MatchmakingService matchmakingService;

    private final User user;
    private final List<TrailLog> userLogs;
    private final List<Trail> loggedTrails;

    /**
     * Constructor for AccountStatisticsService
     *
     * @param matchmakingService Service for trail categorization and scoring
     */
    public AccountStatisticsService(MatchmakingService matchmakingService, User user) {
        this.trailRepo = App.getTrailRepo();
        this.matchmakingService = matchmakingService;
        this.user = user;
        this.userLogs = App.getTrailLogRepo().getAllTrailLogs();
        this.loggedTrails = getLoggedTrails(userLogs);
    }

    /**
     * Get trails that have been logged by the user
     *
     * @param logs List of trail logs
     * @return List of unique trails logged by user
     */
    private List<Trail> getLoggedTrails(List<TrailLog> logs) {
        Set<Integer> loggedTrailIds = logs.stream()
                .map(TrailLog::getTrailId)
                .collect(Collectors.toSet());

        return loggedTrailIds.stream()
                .map(trailRepo::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Get total number of unique trails logged by user
     *
     * @return Total number of trails logged
     */
    public int getTotalLoggedTrails() {
        return loggedTrails.size();
    }

    /**
     * Get user preferences data for bar chart
     *
     * @return Map of user preferences sorted by value
     */
    public Map<String, Integer> getUserPreferencesData() {
        Map<String, Integer> preferences = new LinkedHashMap<>();
        preferences.put("Experience", user.getExperienceLevel());
        preferences.put("Gradient", user.getGradientPreference());
        preferences.put("Forest", user.getBushPreference());
        preferences.put("Reserves", user.getReservePreference());
        preferences.put("Lakes/Rivers", user.getLakeRiverPreference());
        preferences.put("Coast", user.getCoastPreference());
        preferences.put("Mountain", user.getMountainPreference());
        preferences.put("Wildlife", user.getWildlifePreference());
        preferences.put("Historic", user.getHistoricPreference());
        preferences.put("Waterfall", user.getWaterfallPreference());

        // Sort by value desc
        return preferences.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    /**
     * Get difficulty statistics
     *
     * @return Map containing perceived difficulty stats
     */
    public Map<String, Object> getDifficultyStatistics() {
        Map<String, Object> difficultyStats = new HashMap<>();

        Map<String, Integer> perceivedDifficulties = new HashMap<>();
        for (TrailLog log : userLogs) {
            String perceived = log.getPerceivedDifficulty();
            if (perceived != null && !perceived.isEmpty()) {
                perceivedDifficulties.put(perceived, perceivedDifficulties.getOrDefault(perceived, 0) + 1);
            }
        }

        difficultyStats.put("perceived", perceivedDifficulties);
        return difficultyStats;
    }

    /**
     * Get simple average match score from trail weights
     *
     * @return Average match score as a percentage
     */
    public double getAverageMatchScore() {
        if (loggedTrails.isEmpty()) {
            return 0.0;
        }

        double sum = loggedTrails.stream()
                .mapToDouble(Trail::getUserWeight)
                .sum();

        return (sum / loggedTrails.size()) * 100; // Convert to %
    }

    /**
     * Get regional statistics from logged trails
     *
     * @return Map of top 6 regions by count
     */
    public Map<String, Integer> getRegionalStatistics() {
        Map<String, Integer> regionStats = new HashMap<>();

        for (Trail trail : loggedTrails) {
            String region = trail.getRegion();
            if (region != null && !region.isEmpty()) {
                regionStats.put(region, regionStats.getOrDefault(region, 0) + 1);
            }
        }

        return regionStats.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(6)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    /**
     * Get the top category by count from logged trails
     *
     * @return Top category as a string
     */
    public String getTopCategory() {
        Map<String, Integer> categoryStats = new HashMap<>();

        for (Trail trail : loggedTrails) {
            try {
                Set<String> categories = matchmakingService.categoriseTrail(trail);
                for (String category : categories) {
                    categoryStats.put(category, categoryStats.getOrDefault(category, 0) + 1);
                }
            } catch (MatchmakingFailedException e) {
                // Skip this trail if categorization fails
                continue;
            }
        }

        return categoryStats.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
    }
}