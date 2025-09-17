package seng202.team5.services;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

/**
 * Service for searching and filtering trails with pagination support.
 * Provides trail search functionality and pagination calculations.
 */
public class SearchService {
    private List<Trail> trails;
    private List<Trail> filteredTrails;
    private Map<String, String> filters;
    private int maxResults = 50;
    private String currentSortBy = "name"; // Default sorting by name
    private boolean isAscending = true; // Default to ascending order

    /** Ordered list of difficulty levels for proper sorting */
    private static final List<String> DIFFICULTY_ORDER = List.of("easiest", "easy", "intermediate", "advanced",
            "expert");

    /**
     * Configuration for a filter type containing all necessary metadata.
     */
    private static class FilterConfig {
        final String allOptionText;
        final Function<Trail, String> fieldExtractor;
        final BiPredicate<Trail, String> predicate;

        FilterConfig(String allOptionText, Function<Trail, String> fieldExtractor,
                BiPredicate<Trail, String> predicate) {
            this.allOptionText = allOptionText;
            this.fieldExtractor = fieldExtractor;
            this.predicate = predicate;
        }
    }

    // Centralized filter configuration - adding a new filter only requires one line
    // here!
    private final Map<String, FilterConfig> filterConfigs = Map.of(
            "query", new FilterConfig("", Trail::getName,
                    (trail, value) -> isNullOrEmpty(value) ||
                            trail.getName().toLowerCase().contains(value.strip().toLowerCase())),
            "completionType", new FilterConfig("All types", Trail::getCompletionType,
                    (trail, value) -> isNullOrEmpty(value) || value.equals("All types") ||
                            trail.getCompletionType().equalsIgnoreCase(value)),
            "timeUnit", new FilterConfig("All durations", Trail::getTimeUnit,
                    (trail, value) -> isNullOrEmpty(value) || value.equals("All durations") ||
                            trail.getTimeUnit().equalsIgnoreCase(value)),
            "difficulty", new FilterConfig("All difficulties", Trail::getDifficulty,
                    (trail, value) -> isNullOrEmpty(value) || value.equals("All difficulties") ||
                            trail.getDifficulty().equalsIgnoreCase(value)),
            "multiDay", new FilterConfig("Any time range", trail -> trail.isMultiDay() ? "Multi-day" : "Day walk",
                    (trail, value) -> isNullOrEmpty(value) || value.equals("Any time range") ||
                            (value.equals("Multi-day") && trail.isMultiDay()) ||
                            (value.equals("Day walk") && !trail.isMultiDay())));

    /**
     * Creates SearchService with injected SQLBasedTrailRepo.
     */
    public SearchService(SqlBasedTrailRepo sqlBasedTrailRepo) {
        this.trails = sqlBasedTrailRepo.getAllTrails();
        this.filteredTrails = trails;
        this.filters = new HashMap<>();
    }

    /**
     * Calculates the total number of pages based on the search query.
     * If the query is null or empty, it returns the total number of pages
     * based on the total number of trails.
     *
     * @param searchQuery The search query to filter trails by name
     * @return The total number of pages required to display the trails
     */
    public int getNumberOfPages() {
        updateTrails(); // Ensure filteredTrails is up to date
        return (int) Math.ceil((double) filteredTrails.size() / maxResults);
    }

    /**
     * Gets the total number of trails available in the dataset.
     *
     * @return The total number of trails
     */
    public int getNumberOfTrails() {
        return filteredTrails.size();
    }

    /**
     * Updates the filtered trails based on all active filters and applies sorting.
     */
    private void updateTrails() {
        this.filteredTrails = trails.stream()
                .filter(trail -> filterConfigs.entrySet().stream()
                        .allMatch(entry -> entry.getValue().predicate.test(trail, filters.get(entry.getKey()))))
                .filter(this::shouldIncludeInSort)
                .sorted(getSortComparator())
                .collect(Collectors.toList());
    }

    /**
     * Filter out unwanted trails from sort
     */
    private boolean shouldIncludeInSort(Trail trail) {
        switch (currentSortBy.toLowerCase()) {
            case "time":
                return trail.getAvgCompletionTimeMinutes() > 0;
            case "difficulty":
                return !trail.getDifficulty().equalsIgnoreCase("unknown");
            default:
                return true;
        }
    }

    /**
     * Gets the comparator for the current sort
     */
    private Comparator<Trail> getSortComparator() {
        Comparator<Trail> comparator;
        switch (currentSortBy.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(Trail::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            case "time":
                comparator = Comparator.comparingInt(Trail::getAvgCompletionTimeMinutes);
                break;
            case "difficulty":
                comparator = getDifficultyComparator();
                break;
            case "match":
                comparator = Comparator.comparingDouble(Trail::getUserWeight).reversed();
                break;
            default:
                comparator = Comparator.comparing(Trail::getName, String.CASE_INSENSITIVE_ORDER);
                break;
        }

        return isAscending ? comparator : comparator.reversed();
    }

    /**
     * Gets a comparator for difficulty
     */
    private Comparator<Trail> getDifficultyComparator() {
        return Comparator.comparing(trail -> {
            String difficulty = trail.getDifficulty().toLowerCase();
            int index = DIFFICULTY_ORDER.indexOf(difficulty);
            return index == -1 ? 999 : index;
        });
    }

    /**
     * Utility method to check if a string is null or empty after trimming.
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.strip().isEmpty();
    }

    /**
     * Updates a specific filter with the given value.
     *
     * @param filter       The filter type (e.g., "completionType", "difficulty")
     * @param filterString The value to filter by
     */
    public void updateFilter(String filter, String filterString) {
        if (!filterConfigs.containsKey(filter)) {
            throw new IllegalArgumentException("Unknown filter: " + filter);
        }
        filters.put(filter, filterString);
    }

    /**
     * Updates the current search query.
     *
     * @param query The search query to filter trail names by
     */
    public void setCurrentQuery(String query) {
        filters.put("query", query);
    }

    /**
     * Updates the search query (alias for setCurrentQuery for backward
     * compatibility).
     *
     * @param query The search query to filter trail names by
     */
    public void updateSearch(String query) {
        setCurrentQuery(query);
    }

    /**
     * Gets the trails for the specified page.
     *
     * @param page The page number (0-based)
     * @return List of trails for the specified page
     */
    public List<Trail> getPage(int page) {
        updateTrails();
        int startIndex = page * maxResults;

        return filteredTrails.stream()
                .skip(startIndex)
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    /**
     * Generic method to get distinct values from trails based on a field extractor.
     *
     * @param fieldExtractor Function to extract the desired field from a Trail
     * @return List of distinct values excluding "unknown"
     */
    public List<String> getDistinctTrailValues(Function<Trail, String> fieldExtractor) {
        return trails.stream()
                .map(fieldExtractor)
                .distinct()
                .filter(value -> !value.equals("unknown"))
                .collect(Collectors.toList());
    }

    /**
     * Gets filter options for a filter type
     *
     * @param filterType The filter type
     * @return List of filter options with "All" option first
     */
    public List<String> getFilterOptions(String filterType) {
        FilterConfig config = filterConfigs.get(filterType);
        if (config == null) {
            throw new IllegalArgumentException("Unknown filter type: " + filterType);
        }

        List<String> options = new java.util.ArrayList<>();

        if (!config.allOptionText.isEmpty()) {
            options.add(config.allOptionText);
        }

        getDistinctTrailValues(config.fieldExtractor).stream()
                .map(value -> value.substring(0, 1).toUpperCase() + value.substring(1))
                .forEach(options::add);

        return options;
    }

    /**
     * Gets the default "All" value for a specific filter type.
     *
     * @param filterType The filter type
     * @return The default "All" value for the filter
     */
    public String getDefaultFilterValue(String filterType) {
        FilterConfig config = filterConfigs.get(filterType);
        if (config == null) {
            throw new IllegalArgumentException("Unknown filter type: " + filterType);
        }
        return config.allOptionText;
    }

    /**
     * Sets a new max results limit for pagination.
     *
     * @param maxResults The maximum number of results per page
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * Gets the current max results limit for pagination.
     *
     * @return The maximum number of results per page
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Sets the sorting criteria.
     *
     * @param sortBy The field to sort by ("name", "time", "difficulty")
     */
    public void setSortBy(String sortBy) {
        this.currentSortBy = sortBy;
    }

    /**
     * Gets the current sorting criteria.
     *
     * @return The current sort field
     */
    public String getSortBy() {
        return currentSortBy;
    }

    /**
     * Gets available sorting options.
     *
     * @return List of available sort options
     */
    public List<String> getSortOptions() {
        return List.of("Name", "Time", "Difficulty", "Match");
    }

    /**
     * Sets the sort order.
     *
     * @param ascending true for ascending, false for descending
     */
    public void setSortAscending(boolean ascending) {
        this.isAscending = ascending;
    }

    /**
     * Gets the current sort order.
     *
     * @return true if ascending, false if descending
     */
    public boolean isSortAscending() {
        return isAscending;
    }

    /**
     * Gets the difficulty order list for consistent sorting.
     *
     * @return List of difficulty levels in order
     */
    public static List<String> getDifficultyOrder() {
        return DIFFICULTY_ORDER;
    }

}
