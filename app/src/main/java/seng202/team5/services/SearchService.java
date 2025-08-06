package seng202.team5.services;

import java.util.List;
import java.util.stream.Collectors;

import seng202.team5.models.Trail;

public class SearchService {
    // private static SearchService instance;
    private static DataService dataService;
    private List<Trail> trails;

    public SearchService() {
        String currentPath = System.getProperty("user.dir");
        String fullPath = currentPath
                + "/app/src/main/resources/datasets/DOC_Walking_Experiences_7994760352369043452.csv";
        dataService = new DataService(fullPath);
        trails = dataService.getTrails();
    }

    public List<Trail> searchTrails(String query) {
        if (query == null || query.isEmpty()) {
            return trails; // Return all trails if no query
        }

        // Filter trails based on the search query
        return trails.stream()
                .filter(trail -> trail.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

}
