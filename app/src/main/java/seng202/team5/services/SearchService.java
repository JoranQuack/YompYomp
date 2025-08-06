package seng202.team5.services;

import java.util.List;
import java.util.stream.Collectors;

import seng202.team5.models.Trail;

public class SearchService {
    // private static SearchService instance;
    private static DataService dataService;
    private List<Trail> trails;
    private static final int MAX_RESULTS = 50;

    public SearchService() {
        String currentPath = System.getProperty("user.dir");
        String fullPath = currentPath
                + "/app/src/main/resources/datasets/DOC_Walking_Experiences_7994760352369043452.csv";
        dataService = new DataService(fullPath);
        trails = dataService.getTrails();
    }

    /**
     * Searches for trails based on the provided query. If the query is null or
     * empty, it returns the first 100 trails.
     *
     * @param query The search query to filter trails by name
     * @return A list of trails matching the search query, limited to 100 results
     *
     */
    public List<Trail> searchTrails(String query) {
        if (query == null || query.isEmpty()) {
            return trails.stream().limit(MAX_RESULTS).collect(Collectors.toList()); // Return all trails if no query
        }

        // Filter trails based on the search query
        return trails.stream()
                .filter(trail -> trail.getName().toLowerCase().contains(query.toLowerCase()))
                .limit(MAX_RESULTS)
                .collect(Collectors.toList());
    }

}
