package seng202.team5.services;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import seng202.team5.data.FileBasedKeywordRepo;
import seng202.team5.data.IKeyword;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

/**
 * Service for searching and filtering trails with pagination support.
 * Provides trail search functionality and pagination calculations.
 */
public class SearchService {
    private List<Trail> trails;
    private IKeyword keywordRepo;
    private int maxResults = 100;
    private String keywordCSVPath = "src/main/resources/keywords.csv";

    /**
     * Creates SearchService with injected SQLBasedTrailRepo.
     */
    public SearchService(SqlBasedTrailRepo sqlBasedTrailRepo) {
        this.trails = sqlBasedTrailRepo.getAllTrails();
        this.keywordRepo = new FileBasedKeywordRepo(keywordCSVPath);
    }

    /**
     * Searches for trails based on the provided query with pagination support.
     * If the query is null or empty, it returns trails from the specified page.
     *
     * @param query The search query to filter trails by name
     * @param page  The page number (0-based) to retrieve results from
     * @return A list of trails matching the search query for the specified page,
     *         limited to maxResults
     */
    private List<Trail> searchTrails(String query, int page) {
        int startIndex = page * maxResults;

        if (query == null || query.isEmpty()) {
            return trails.stream()
                    .skip(startIndex)
                    .limit(maxResults)
                    .collect(Collectors.toList());
        }

        // Filter trails based on the search query with pagination
        return trails.stream()
                .filter(trail -> trail.getName().toLowerCase().contains(query.toLowerCase()))
                .skip(startIndex)
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    /**
     * Method for getting trails.
     *
     * @param searchQuery Search query to filter trails
     * @param page        Page number (0-based)
     * @return List of trails for the specified page
     */
    public List<Trail> getTrails(String searchQuery, int page) {
        return searchTrails(searchQuery, page);
    }

    /**
     * Calculates the total number of pages based on the search query.
     * If the query is null or empty, it returns the total number of pages
     * based on the total number of trails.
     *
     * @param searchQuery The search query to filter trails by name
     * @return The total number of pages required to display the trails
     */
    public int getNumberOfPages(String searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return (int) Math.ceil((double) trails.size() / maxResults);
        }
        long filteredCount = trails.stream()
                .filter(trail -> trail.getName().toLowerCase().contains(searchQuery.toLowerCase()))
                .count();
        return (int) Math.ceil((double) filteredCount / maxResults);
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
     * Checks if the description contains any of the keywords.
     *
     * @param description The description to check
     * @param keywords    The keywords to check against
     * @return True if the description contains any of the keywords, false otherwise
     */
    private boolean containsKeyword(String description, List<String> keywords) {
        String lowercaseDescription = description.toLowerCase();
        return keywords.stream()
                .anyMatch(keyword -> lowercaseDescription.contains(keyword.toLowerCase()));
    }

    /**
     * Categorises the trail based on the keywords in the description.
     *
     * @param trail The trail to categorise
     * @return A set of categories that the trail matches, or an empty set if no
     *         categories match
     */
    public Set<String> categoriseTrail(Trail trail) {
        Set<String> matchedCategories = new HashSet<>();
        String description = trail.getDescription();

        for (Map.Entry<String, List<String>> entry : keywordRepo.getKeywords().entrySet()) {
            if (containsKeyword(description, entry.getValue())) {
                matchedCategories.add(entry.getKey());
            }
        }

        return matchedCategories;
    }

    /**
     * Categorises all trails based on their descriptions.
     */
    public void categoriseAllTrails() {
        for (Trail trail : trails) {
            Set<String> matchedCategories = categoriseTrail(trail);
            trail.setCategories(matchedCategories);
        }
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
