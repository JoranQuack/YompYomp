package seng202.team5.services;

import seng202.team5.data.FileBasedKeywordRepo;
import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.data.IKeyword;
import seng202.team5.models.Trail;
import seng202.team5.data.ITrail;

import java.util.List;
import java.util.Map;

/**
 * Service class for managing trail data operations.
 * Provides abstraction layer between business logic and data access.
 */
public class DataService {
    /** Trail repository for data access */
    private final ITrail trailRepo;

    /** Keyword repository for data access */
    private final IKeyword keywordRepo;

    /** List of all trails */
    private final List<Trail> trails;

    /** Categories of keywords loaded from CSV file */
    private final Map<String, List<String>> keywords;

    /**
     * Creates DataService with CSV file pathes.
     *
     * @param trailCSVPath Path to trail CSV file
     * @param keywordCSVPath Path to the keyword CSV file
     */
    public DataService(String trailCSVPath, String keywordCSVPath) {
        this.trailRepo = new FileBasedTrailRepo(trailCSVPath);
        this.keywordRepo = new FileBasedKeywordRepo(keywordCSVPath);
        this.trails = trailRepo.getAllTrails();
        this.keywords = keywordRepo.getKeywords();
    }

    /**
     * Gets all trails from data source.
     *
     * @return List of all trails
     */
    public List<Trail> getTrails() {
        return trailRepo.getAllTrails();
    }

    /**
     * Gets the map of categories to keywords.
     *
     * @return The map of categories to keywords.
     */
    public Map<String, List<String>> getKeywords() {
        return keywordRepo.getKeywords();
    }
}
