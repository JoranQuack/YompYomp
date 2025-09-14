package seng202.team5.services;

import java.util.List;
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
    private String currentQuery;
    private int maxResults = 50;

    /**
     * Creates SearchService with injected SQLBasedTrailRepo.
     */
    public SearchService(SqlBasedTrailRepo sqlBasedTrailRepo) {
        this.trails = sqlBasedTrailRepo.getAllTrails();
        this.filteredTrails = trails;
        this.currentQuery = "";
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
     * Updates the filtered trails based on the search query.
     *
     * @param query The search query to filter trails by name
     */
    public void updateSearch(String query) {
        currentQuery = query;
        if (currentQuery == null || currentQuery.isEmpty()) {
            this.filteredTrails = trails;
        } else {
            this.filteredTrails = trails.stream()
                    .filter(trail -> trail.getName().toLowerCase().contains(currentQuery.strip().toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Gets the trails for the specified page.
     *
     * @param page The page number (0-based)
     * @return List of trails for the specified page
     */
    public List<Trail> getPage(int page) {
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
