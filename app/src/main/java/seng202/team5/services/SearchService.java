package seng202.team5.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
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

    // Define filter predicates for each filter type
    private final Map<String, BiPredicate<Trail, String>> filterPredicates = Map.of(
            "query", (trail, value) -> isNullOrEmpty(value) ||
                    trail.getName().toLowerCase().contains(value.strip().toLowerCase()),
            "completionType", (trail, value) -> isNullOrEmpty(value) || value.equals("All types") ||
                    trail.getCompletionType().equalsIgnoreCase(value),
            "timeUnit", (trail, value) -> isNullOrEmpty(value) ||
                    value.equals("All durations") || trail.getTimeUnit().equalsIgnoreCase(value));

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
        return trails.size();
    }

    /**
     * Updates the filtered trails based on all active filters.
     */
    private void updateTrails() {
        this.filteredTrails = trails.stream()
                .filter(trail -> filterPredicates.entrySet().stream()
                        .allMatch(entry -> entry.getValue().test(trail, filters.get(entry.getKey()))))
                .collect(Collectors.toList());
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
        if (!filterPredicates.containsKey(filter)) {
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
     * Gets the distinct completion types from the trails.
     *
     * @return List of all completion types
     */
    public List<String> getAllCompletionTypes() {
        return filteredTrails.stream()
                .map(Trail::getCompletionType)
                .distinct()
                .filter(completionType -> !completionType.equals("unknown"))
                .collect(Collectors.toList());
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

}
