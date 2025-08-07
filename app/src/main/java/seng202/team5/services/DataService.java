package seng202.team5.services;

import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.data.ITrail;

import java.util.List;

/**
 * Service class for managing trail data operations.
 * Provides abstraction layer between business logic and data access.
 */
public class DataService {
    /** Trail repository for data access */
    private final ITrail trailRepo;

    /**
     * Creates DataService with CSV file path.
     *
     * @param trailCSVPath Path to trail CSV file
     */
    public DataService(String trailCSVPath) {
        this.trailRepo = new FileBasedTrailRepo(trailCSVPath);
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
