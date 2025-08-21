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
     * @param id id of the object
     * @return the record
     */
    java.util.Optional<Trail> findById(int id);

    /**
     * Upsets into the database
     * @param trail trail that needs to be updated
     */
    void upsert(Trail trail);

    /**
     * upsert all the trails in the database
     * @param trails
     */
    void upsertAll(List<Trail> trails);

    /**
     * delete a certain record by accessing it through its id
     * @param id
     */
    void deleteById(int id);

}
