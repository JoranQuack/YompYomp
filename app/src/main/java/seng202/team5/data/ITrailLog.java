package seng202.team5.data;

import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.TrailLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    /**
     * Inserts or updates a trailLog (UPSERT). If a row with the same id exists, its
     * fields are updated;
     * otherwise a new row is inserted
     *
     * @param trailLog trailLog that needs to be updated
     */
    void upsert(TrailLog trailLog);

    /**
     * Inserts or updates all supplied trailLogs. Loops and calls upsert method.
     *
     * @param trailLogs
     */
    void upsertAll(List<TrailLog> trailLogs) throws MatchmakingFailedException;

    /**
     * Deletes a traillog by its primary key (id)
     *
     * @param id the trailLog identifier to delete
     */
    void deleteById(int id);

    /**
     * Returns a new value for the next trailLog id in the database
     *
     * @return new trialLog id
     */
    int getNewTrailLogId();

    /**
     * Maps the current result set to a trailLog
     *
     * @param rs result set positioned at a row from a trailLog
     * @return mapped trailLog
     * @throws SQLException if the column cannot be read
     */
    TrailLog mapRowToTrailLog(ResultSet rs) throws SQLException;

    /**
     * Binds the trailLog fields to the prepared statement. The order must match.
     *
     * @param stmt     prepared statement to bind
     * @param trailLog source of values
     * @throws SQLException if a parameter cannot be set
     */
    void setTrailLogParameters(PreparedStatement stmt, TrailLog trailLog) throws SQLException;

    /**
     * Returns the trail logs for a specific trail id
     *
     * @param trailId the trail id to search for
     * @return an optional containing the trail log if found, or empty if not found
     */
    Optional<TrailLog> findByTrailId(int trailId);
}
