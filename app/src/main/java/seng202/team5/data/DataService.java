package seng202.team5.data;

import seng202.team5.models.Trail;

import java.util.List;

/**
 * Service class for managing trail data operations.
 * Provides abstraction layer between business logic and data access.
 */
public class DataService {
    /** Trail repository for data access */
    private final ITrail trailRepo;

    /**
     * Creates DataService with CSV file path (TEMPORARY).
     *
     * @param trailCSVPath Path to trail CSV file
     */
    public DataService(String trailCSVPath) {
        this.trailRepo = new FileBasedTrailRepo(trailCSVPath);
    }

    /**
     * Creates DataService with database repository.
     *
     * @param databaseService Database service instance
     */
    public DataService(DatabaseService databaseService) {
        this.trailRepo = new SqlBasedTrailRepo(databaseService);
    }

    /**
     * Creates DataService with custom repository - used for testing.
     *
     * @param trailRepo Custom trail repository
     */
    public DataService(ITrail trailRepo) {
        this.trailRepo = trailRepo;
    }

    /**
     * Gets all trails from data source.
     *
     * @return List of all trails
     */
    public List<Trail> getTrails() {
        return trailRepo.getAllTrails();
    }
}
