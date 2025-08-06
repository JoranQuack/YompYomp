package seng202.team5.data;

import seng202.team5.models.Trail;
import java.util.List;

/**
 * Interface for accessing the trail data
 */
public interface ITrail {
    /**
     * Returns all trails avaliable in the data source
     * @return List of the trail objects
     */
    List<Trail> getAllTrails();
}
