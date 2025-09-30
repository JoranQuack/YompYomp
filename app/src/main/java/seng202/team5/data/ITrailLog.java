package seng202.team5.data;

import seng202.team5.models.TrailLog;

import java.util.*;

/**
 * Interface for accessing the trail log data
 */
public interface ITrailLog {
    /**
     * Returns all trail logs available
     *
     * @return List of trail log objects
     */
    List<TrailLog> getAllTrailLogs();

    /**
     * Finds a trail log by its ID
     *
     * @param id id of the object
     * @return the trail log id
     */
    Optional<TrailLog> findById(int id);

    /**
     * Count all the rows in the trail logs table
     *
     * @return number of trail logs as an integer
     */
    int countTrailLogs();
}
