package seng202.team5.data;

import seng202.team5.models.TripLog;

import java.util.*;

/**
 * Interface for accessing the trip data
 */
public interface ITrip {
    /**
     * Returns all trips available
     */
    List<TripLog> getAllTrips();

    /**
     * Finds a trip by its ID
     *
     * @param id id of the object
     * @return the trip
     */
    Optional<TripLog> findById(int id);

    /**
     * Count all trips
     */
    int countTrips();
}
