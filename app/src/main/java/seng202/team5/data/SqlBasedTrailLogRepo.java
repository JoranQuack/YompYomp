package seng202.team5.data;

import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.TrailLog;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import static java.util.logging.Level.parse;

/**
 * Class is responsible for holding and executing all SQL queries related to
 * 'Trail Logs'
 */
public class SqlBasedTrailLogRepo implements ITrailLog {

    private final QueryHelper queryHelper;

    // SQL Constants
    private static final String SELECT_ALL = """
            SELECT *
            FROM trailLog
            """;

    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";

    private static final String UPSERT_SQL = """
            INSERT OR REPLACE INTO trailLog (
                id, trailId, startDate, completionTime, timeUnit, completionType,
                rating, perceivedDifficulty, notes
            ) VALUES (?,?,?,?,?,?,?,?,?)
            """;

    private static final String DELETE_SQL = "DELETE FROM trailLog WHERE id = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM trailLog";

    /**
     * Create a SQL-based trailLog repository, uses query helper class
     *
     * @param databaseService provider of JDBC connection used by QueryHelper
     */
    public SqlBasedTrailLogRepo(DatabaseService databaseService) {
        this.queryHelper = new QueryHelper(databaseService);
    }

    /**
     * Retrieves all trailLogs from the database
     *
     * @return a list of all rows in the trailLog table
     */
    @Override
    public List<TrailLog> getAllTrailLogs() {
        return queryHelper.executeQuery(SELECT_ALL, null, this::mapRowToTrailLog);
    }

    /**
     * Finds a single trailLog by its primary key
     *
     * @param id id of the object
     * @return an Optional containing the trailLog if found; otherwise empty
     */
    public Optional<TrailLog> findById(int id) {
        return queryHelper.executeQuerySingle(
                SELECT_BY_ID,
                stmt -> stmt.setInt(1, id),
                        this::mapRowToTrailLog
        );
    }

    /**
     * Counts all the rows in the trailLog table
     *
     * @return number of trailLogs as an integer
     */
    public int countTrailLogs() {
        return queryHelper.executeCountQuery(COUNT_SQL, null);
    }

    /**
     * Inserts or updates a trailLog (UPSERT). If a row with the same id exists, its
     * fields are updated;
     * otherwise a new row is inserted
     *
     * @param trailLog trailLog that needs to be updated
     */
    public void upsert(TrailLog trailLog) {
        queryHelper.executeUpdate(UPSERT_SQL, stmt -> setTrailLogParameters(stmt, trailLog));
    }

    /**
     * Inserts or updates all supplied trailLogs. Loops and calls upsert method.
     *
     * @param trailLogs List of trailLogs to insert into the database
     * @throws MatchmakingFailedException if trailLogs is empty
     */
    public void upsertAll(List<TrailLog> trailLogs) throws MatchmakingFailedException {
        if (trailLogs.isEmpty())
            throw new MatchmakingFailedException("trailLogs is empty.");

        queryHelper.executeBatch(UPSERT_SQL, trailLogs, this::setTrailLogParameters);
    }

    /**
     * Deletes a trailLog by its primary key
     *
     * @param id the trailLog identifier to delete
     */
    public void deleteById(int id) {
        queryHelper.executeUpdate(DELETE_SQL, stmt -> stmt.setInt(1, id));
    }

    /**
     * Returns a new value for the next trailLog id in the database
     *
     * @return new trailLog id
     */
    public int getNewTrailLogId() {
        String maxIdQuery = "SELECT MAX(id) FROM trailLog";
        return queryHelper.executeQuerySingle(maxIdQuery, null, rs -> rs.getInt(1)).orElse(0) + 1;
    }

    /**
     * Maps the current result set row to a trailLog
     *
     * @param rs result set positioned at a row from a trailLog
     * @return mapped trailLog
     * @throws SQLException if the column cannot be read
     */
    private TrailLog mapRowToTrailLog(ResultSet rs) throws SQLException {
        String dateString = rs.getString("startDate"); // i.e. 2025-10-03
        java.sql.Date startDate = null;
        if (dateString != null) {
            try {
                if (dateString.matches("\\d+")) {
                    startDate = new java.sql.Date(Long.parseLong(dateString));
                } else {
                    java.util.Date utilDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
                    startDate = new java.sql.Date(utilDate.getTime());
                }
            } catch (java.text.ParseException e) {
                throw new SQLException("Could not parse date: " + dateString, e);
            }
        }
        return new TrailLog(
                rs.getInt("id"),
                rs.getInt("trailId"),
                startDate,
                (Integer) rs.getObject("completionTime"),
                rs.getString("timeUnit"),
                rs.getString("completionType"),
                (Integer) rs.getObject("rating"),
                rs.getString("perceivedDifficulty"),
                rs.getString("notes")
        );
    }

    /**
     * Binds trailLog fields to the prepared statement. The order must match.
     *
     * @param stmt  prepared statement to bind
     * @param trailLog source of values
     * @throws SQLException if a parameter cannot be set
     */
    private void setTrailLogParameters(PreparedStatement stmt, TrailLog trailLog) throws SQLException {
        stmt.setInt(1, trailLog.getId());
        stmt.setInt(2, trailLog.getTrailId());
        stmt.setString(3, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(trailLog.getStartDate()));
        if (trailLog.getCompletionTime() != null) stmt.setInt(4, trailLog.getCompletionTime());
        else stmt.setNull(4, Types.INTEGER);
        stmt.setString(5, trailLog.getTimeUnit());
        stmt.setString(6, trailLog.getCompletionType());
        if (trailLog.getRating() != null) stmt.setInt(7, trailLog.getRating());
        else stmt.setNull(7, Types.INTEGER);
        stmt.setString(8, trailLog.getPerceivedDifficulty());
        stmt.setString(9, trailLog.getNotes()
        );
    }
}
