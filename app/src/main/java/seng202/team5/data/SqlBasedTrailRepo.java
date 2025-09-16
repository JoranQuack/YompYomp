package seng202.team5.data;

import seng202.team5.models.Trail;
import seng202.team5.utils.QueryHelper;

import java.util.List;
import java.util.Optional;

/**
 * Class is responsible for holding and executing all SQL queries related to
 * 'Trails'
 */
public class SqlBasedTrailRepo implements ITrail {
    private final QueryHelper queryHelper;

    // SQL Constants
    private static final String SELECT_ALL = """
            SELECT *
            FROM trail
            """;

    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";

    private static final String UPSERT_SQL = """
            INSERT INTO trail (
                id, name, description, difficulty, completion_info, min_completion_time_minutes,
                max_completion_time_minutes, completion_type, time_unit, is_multi_day, has_variable_time,
                thumb_url, web_url, user_weight
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET
                name=excluded.name,
                description=excluded.description,
                difficulty=excluded.difficulty,
                completion_info=excluded.completion_info,
                min_completion_time_minutes=excluded.min_completion_time_minutes,
                max_completion_time_minutes=excluded.max_completion_time_minutes,
                completion_type=excluded.completion_type,
                time_unit=excluded.time_unit,
                is_multi_day=excluded.is_multi_day,
                has_variable_time=excluded.has_variable_time,
                thumb_url=excluded.thumb_url,
                web_url=excluded.web_url,
                user_weight=excluded.user_weight
            """;

    private static final String DELETE_SQL = "DELETE FROM trail WHERE id = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM trail";

    /**
     * Creates a SQL-based trail repository, uses query helper class
     *
     * @param databaseService provider of JDBC connection used by QueryHelper
     */
    public SqlBasedTrailRepo(DatabaseService databaseService) {
        this.queryHelper = new QueryHelper(databaseService);
    }

    /**
     * Retrieves all trails from database
     *
     * @return a list of all rows in the trail table
     */
    @Override
    public List<Trail> getAllTrails() {
        return queryHelper.executeQuery(SELECT_ALL, null, this::mapRowToTrail);
    }

    /**
     * Retrieves recommended trails from database
     *
     * @return a list of recommended trails
     */
    public List<Trail> getRecommendedTrails() {
        String sql = SELECT_ALL + " ORDER BY user_weight DESC LIMIT 8";
        return queryHelper.executeQuery(sql, null, this::mapRowToTrail);
    }

    /**
     * Returns if the trail has been processed or not
     *
     * @param trail
     * @return boolean indicating if trail has been processed
     */
    public boolean isTrailProcessed(Trail trail) {
        String sql = "SELECT COUNT(*) FROM trail WHERE id = ? AND completion_type IS NOT NULL AND completion_type != 'unknown'";
        Integer count = queryHelper.executeCountQuery(sql, stmt -> stmt.setInt(1, trail.getId()));
        return count != null && count > 0;
    }

    /**
     * Finds a single trail by its primary key
     *
     * @param id id of the object
     * @return an Optional containing the trail if found; otherwise empty
     */
    @Override
    public Optional<Trail> findById(int id) {
        return queryHelper.executeQuerySingle(
                SELECT_BY_ID,
                stmt -> stmt.setInt(1, id),
                this::mapRowToTrail);
    }

    /**
     * Inserts or updates a trail (UPSERT). If a row with the same id exists, its
     * fields are updated
     * ;otherwise a new row is inserted
     *
     * @param trail trail that needs to be updated
     */
    public void upsert(Trail trail) {
        queryHelper.executeUpdate(UPSERT_SQL, stmt -> setTrailParameters(stmt, trail));
    }

    /**
     * Inserts or updates all supplied trails. Loops and calls upsert method.
     *
     * @param trails List of trails to insert into database
     */
    public void upsertAll(List<Trail> trails) {
        if (trails.isEmpty())
            return;
        for (Trail trail : trails) {
            upsert(trail);
        }
    }

    /**
     * Deletes a trail by its primary key
     *
     * @param id the trail identifier to delete
     */
    public void deleteById(int id) {
        queryHelper.executeUpdate(DELETE_SQL, stmt -> stmt.setInt(1, id));
    }

    /**
     * Clears all user weight values from database
     */
    public void clearUserWeights() {
        queryHelper.executeUpdate("UPDATE trail SET user_weight = NULL", null);
    }

    /**
     * Counts all the rows in the trail table
     *
     * @return
     */
    @Override
    public int countTrails() {
        return queryHelper.executeCountQuery(COUNT_SQL, null);
    }

    /**
     * Maps the current result set row to a trail
     *
     * @param rs result set positioned at a row from trail
     * @return mapped Trail
     * @throws java.sql.SQLException if column cannot be read
     */
    private Trail mapRowToTrail(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Trail(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("difficulty"),
                rs.getString("description"),
                rs.getString("completion_info"),
                rs.getInt("min_completion_time_minutes"),
                rs.getInt("max_completion_time_minutes"),
                rs.getString("completion_type"),
                rs.getString("time_unit"),
                rs.getBoolean("is_multi_day"),
                rs.getBoolean("has_variable_time"),
                rs.getString("thumb_url"),
                rs.getString("web_url"),
                rs.getDouble("user_weight"));
    }

    /**
     * Binds trail fields to the prepared statement. The order must match.
     *
     * @param stmt  prepared statement to bind
     * @param trail source of values
     * @throws java.sql.SQLException if a parameter cannot be set
     */
    private void setTrailParameters(java.sql.PreparedStatement stmt, Trail trail) throws java.sql.SQLException {
        stmt.setInt(1, trail.getId());
        stmt.setString(2, trail.getName());
        stmt.setString(3, trail.getDescription());
        stmt.setString(4, trail.getDifficulty());
        stmt.setString(5, trail.getCompletionInfo());
        stmt.setInt(6, trail.getMinCompletionTimeMinutes());
        stmt.setInt(7, trail.getMaxCompletionTimeMinutes());
        stmt.setString(8, trail.getCompletionType());
        stmt.setString(9, trail.getTimeUnit());
        stmt.setBoolean(10, trail.isMultiDay());
        stmt.setBoolean(11, trail.hasVariableTime());
        stmt.setString(12, trail.getThumbnailURL());
        stmt.setString(13, trail.getWebpageURL());
        stmt.setDouble(14, trail.getUserWeight());
    }
}
