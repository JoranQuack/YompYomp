package seng202.team5.data;

import seng202.team5.models.Trail;
import java.util.List;

/**
 * Interface for accessing the trail data
 */
public interface ITrail {
    /**
     * Returns all trails available in the data source
     *
     * @return List of the trail objects
     */
    List<Trail> getAllTrails();

    /**
     * Finds a record by its ID
     * 
     * @param id id of the object
     * @return the record
     */
    java.util.Optional<Trail> findById(int id);

    /**
     * Counts all the rows in the trail table
     *
     * @return the count of trails
     */
    int countTrails();

}
