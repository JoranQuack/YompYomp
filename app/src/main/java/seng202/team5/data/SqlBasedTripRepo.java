package seng202.team5.data;

import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.TrailLog;

import java.sql.Date;
import java.sql.*;
import java.util.*;

public class SqlBasedTripRepo implements ITrip {

    private final QueryHelper queryHelper;

    // SQL Constants
    private static final String SELECT_ALL = """
            SELECT *
            FROM trip
            """;

    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";

    private static final String UPSERT_SQL = """
            INSERT OR IGNORE INTO trip (
                id, name, translation, region, difficulty, description, completionInfo, minCompletionTimeMinutes,
                maxCompletionTimeMinutes, completionType, timeUnit, isMultiDay, hasVariableTime,
                thumbUrl, webUrl, cultureUrl, userWeight, lat, lon
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;

    private static final String DELETE_SQL = "DELETE FROM trip WHERE id = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM trip";

    /**
     * Create a SQL-based trip repository, uses query helper class
     *
     * @param databaseService provider of JDBC connection used by QueryHelper
     */
    public SqlBasedTripRepo(DatabaseService databaseService) {
        this.queryHelper = new QueryHelper(databaseService);
    }

    /**
     * Retrieves all trips from the database
     *
     * @return a list of all rows in the trip table
     */
    @Override
    public List<TrailLog> getAllTrips() {
        return queryHelper.executeQuery(SELECT_ALL, null, this::mapRowToTrip);
    }

    /**
     * Finds a single trip by its primary key
     *
     * @param id id of the object
     * @return an Optional containing the trip if found; otherwise empty
     */
    @Override
    public Optional<TrailLog> findById(int id) {
        return queryHelper.executeQuerySingle(
                SELECT_BY_ID,
                stmt -> stmt.setInt(1, id),
                        this::mapRowToTrip);
    }

    /**
     * Counts all the rows in the trip table
     *
     * @return number of trips as an integer
     */
    @Override
    public int countTrips() {
        return queryHelper.executeCountQuery(COUNT_SQL, null);
    }

    /**
     * Inserts or updates a trip (UPSERT). If a row with the same id exists, its
     * fields are updated;
     * otherwise a new row is inserted
     *
     * @param trip trip that needs to be updated
     */
    public void upsert(TrailLog trip) {
        queryHelper.executeUpdate(UPSERT_SQL, stmt -> setTripParameters(stmt, trip));
    }

    /**
     * Inserts or updates all supplied trips. Loops and calls upsert method.
     *
     * @param trips List of trips to insert into the database
     */
    public void upsertAll(List<TrailLog> trips) throws MatchmakingFailedException {
        if (trips.isEmpty())
            throw new MatchmakingFailedException("Trips is empty.");

        queryHelper.executeBatch(UPSERT_SQL, trips, this::setTripParameters);
    }

    /**
     * Deletes a trip by its primary key
     *
     * @param id the trip identifier to delete
     */
    public void deleteById(int id) {
        queryHelper.executeUpdate(DELETE_SQL, stmt -> stmt.setInt(1, id));
    }

    /**
     * Returns a new value for the next trip id in the database
     *
     * @return new trip id
     */
    public int getNewTripId() {
        String maxIdQuery = "SELECT MAX(id) FROM trip";
        return queryHelper.executeQuerySingle(maxIdQuery, null, this::mapMaxId).orElse(0) + 1;
    }

    /**
     * Maps the current result set row to a trip
     *
     * @param rs result set positioned at a row from a trip
     * @return mapped trip
     * @throws java.sql.SQLException if the column cannot be read
     */
    private TrailLog mapRowToTrip(ResultSet rs) throws SQLException {
        return new TrailLog(
                rs.getInt("id"),
                rs.getInt("userId"),
                rs.getInt("tripId"),
                rs.getDate("startDate"),
                rs.getDate("endDate"),
                rs.getString("completionInfo"),
                rs.getInt("minDurationMinutes"),
                rs.getInt("maxDurationMinutes"),
                rs.getString("durationType"),
                rs.getString("timeUnit"),
                rs.getBoolean("isMultiDay"),
                rs.getBoolean("hasVariableTime"),
                rs.getString("notes"),
                rs.getInt("rating"),
                rs.getString("weather"),
                rs.getString("personalDifficulty"));
    }

    /**
     * Maps the result returned from MAX(id) to an integer
     *
     * @param rs result set
     * @return integer of max id
     * @throws SQLException if the column cannot be read
     */
    private int mapMaxId(ResultSet rs) throws SQLException {
        return rs.getInt(1);
    }

    /**
     * Binds trip fields to the prepared statement. The order must match.
     *
     * @param stmt  prepared statement to bind
     * @param trip source of values
     * @throws SQLException if a parameter cannot be set
     */
    private void setTripParameters(PreparedStatement stmt, TrailLog trip) throws SQLException {
        stmt.setInt(1, trip.getId());
        stmt.setInt(2, trip.getUserId());
        stmt.setInt(3, trip.getTrailId());
        stmt.setDate(4, (Date) trip.getStartDate());
        stmt.setDate(5, (Date) trip.getEndDate());
        stmt.setString(6, trip.getCompletionInfo());
        stmt.setInt(7, trip.getMinDurationMinutes());
        stmt.setInt(8, trip.getMaxDurationMinutes());
        stmt.setString(9, trip.getDurationType());
        stmt.setString(10, trip.getTimeUnit());
        stmt.setBoolean(11, trip.isMultiDay());
        stmt.setBoolean(12, trip.isHasVariableTime());
        stmt.setString(13, trip.getNotes());
        stmt.setInt(14, trip.getRating());
        stmt.setString(15, trip.getWeather());
        stmt.setString(16, trip.getPersonalDifficulty());
    }
}
