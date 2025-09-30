package seng202.team5.data;

import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.Trail;

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

    private static final String INSERT_OR_IGNORE_SQL = """
            INSERT OR IGNORE INTO trail (
                id, name, translation, region, difficulty, description, completionInfo, minCompletionTimeMinutes,
                maxCompletionTimeMinutes, completionType, timeUnit, isMultiDay, hasVariableTime,
                thumbUrl, webUrl, cultureUrl, userWeight, lat, lon
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;

    private static final String UPSERT_SQL = """
            INSERT INTO trail (
                id, name, translation, region, difficulty, description, completionInfo, minCompletionTimeMinutes,
                maxCompletionTimeMinutes, completionType, timeUnit, isMultiDay, hasVariableTime,
                thumbUrl, webUrl, cultureUrl, userWeight, lat, lon
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET
                name = excluded.name,
                translation = excluded.translation,
                region = excluded.region,
                difficulty = excluded.difficulty,
                description = excluded.description,
                completionInfo = excluded.completionInfo,
                minCompletionTimeMinutes = excluded.minCompletionTimeMinutes,
                maxCompletionTimeMinutes = excluded.maxCompletionTimeMinutes,
                completionType = excluded.completionType,
                timeUnit = excluded.timeUnit,
                isMultiDay = excluded.isMultiDay,
                hasVariableTime = excluded.hasVariableTime,
                thumbUrl = excluded.thumbUrl,
                webUrl = excluded.webUrl,
                cultureUrl = excluded.cultureUrl,
                userWeight = excluded.userWeight,
                lat = excluded.lat,
                lon = excluded.lon
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
        String sql = SELECT_ALL + " ORDER BY userWeight DESC, name ASC LIMIT 8";
        return queryHelper.executeQuery(sql, null, this::mapRowToTrail);
    }

    /**
     * Returns if the trail has been processed or not
     *
     * @param trail
     * @return boolean indicating if trail has been processed
     */
    public boolean isTrailProcessed(Trail trail) {
        String sql = "SELECT COUNT(*) FROM trail WHERE id = ? AND completionType IS NOT NULL AND completionType != 'unknown'";
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
     * Inserts a trail only if it doesn't already exist in the database.
     * If a row with the same id exists, it is ignored (no update occurs).
     *
     * @param trail trail that needs to be inserted if not exists
     */
    public void insertOrIgnore(Trail trail) {
        queryHelper.executeUpdate(INSERT_OR_IGNORE_SQL, stmt -> setTrailParameters(stmt, trail));
    }

    /**
     * Inserts or updates a trail (true UPSERT). If a row with the same id exists,
     * its
     * fields are updated; otherwise a new row is inserted.
     *
     * @param trail trail that needs to be upserted
     */
    public void upsert(Trail trail) {
        queryHelper.executeUpdate(UPSERT_SQL, stmt -> setTrailParameters(stmt, trail));
    }

    /**
     * Inserts all supplied trails only if they don't already exist in the database.
     * Existing trails are ignored (no update occurs).
     *
     * @param trails List of trails to insert if not exists
     */
    public void insertOrIgnoreAll(List<Trail> trails) throws MatchmakingFailedException {
        if (trails.isEmpty())
            throw new MatchmakingFailedException("Trails is empty.");

        queryHelper.executeBatch(INSERT_OR_IGNORE_SQL, trails, this::setTrailParameters);
    }

    /**
     * Inserts or updates all supplied trails (true UPSERT). If a trail with the
     * same id exists,
     * its fields are updated; otherwise a new trail is inserted.
     *
     * @param trails List of trails to upsert
     */
    public void upsertAll(List<Trail> trails) throws MatchmakingFailedException {
        if (trails.isEmpty())
            throw new MatchmakingFailedException("Trails is empty.");

        queryHelper.executeBatch(UPSERT_SQL, trails, this::setTrailParameters);
    }

    /**
     * Updates the trail user weights
     *
     * @param trails List of trails to update user weights for
     */
    public void updateUserWeights(List<Trail> trails) {
        if (trails.isEmpty())
            return;

        String sql = "UPDATE trail SET userWeight = ? WHERE id = ?";
        queryHelper.executeBatch(sql, trails, (stmt, trail) -> {
            stmt.setDouble(1, trail.getUserWeight());
            stmt.setInt(2, trail.getId());
        });
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
        queryHelper.executeUpdate("UPDATE trail SET userWeight = NULL", null);
    }

    /**
     * Counts all the rows in the trail table
     *
     * @return number of trails as an integer
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
     * @throws java.sql.SQLException if the column cannot be read
     */
    private Trail mapRowToTrail(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Trail(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("translation"),
                rs.getString("region"),
                rs.getString("difficulty"),
                rs.getString("description"),
                rs.getString("completionInfo"),
                rs.getInt("minCompletionTimeMinutes"),
                rs.getInt("maxCompletionTimeMinutes"),
                rs.getString("completionType"),
                rs.getString("timeUnit"),
                rs.getBoolean("isMultiDay"),
                rs.getBoolean("hasVariableTime"),
                rs.getString("thumbUrl"),
                rs.getString("webUrl"),
                rs.getString("cultureUrl"),
                rs.getDouble("userWeight"),
                rs.getDouble("lat"),
                rs.getDouble("lon"));
    }

    /**
     * Maps the result returned from MAX(id) to an integer
     *
     * @param rs result set
     * @return integer of max id
     * @throws java.sql.SQLException if the column cannot be read
     */
    private int mapMaxId(java.sql.ResultSet rs) throws java.sql.SQLException {
        return rs.getInt("MAX(id)");
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
        stmt.setString(3, trail.getTranslation());
        stmt.setString(4, trail.getRegion());
        stmt.setString(5, trail.getDifficulty());
        stmt.setString(6, trail.getDescription());
        stmt.setString(7, trail.getCompletionInfo());
        stmt.setInt(8, trail.getMinCompletionTimeMinutes());
        stmt.setInt(9, trail.getMaxCompletionTimeMinutes());
        stmt.setString(10, trail.getCompletionType());
        stmt.setString(11, trail.getTimeUnit());
        stmt.setBoolean(12, trail.isMultiDay());
        stmt.setBoolean(13, trail.hasVariableTime());
        stmt.setString(14, trail.getThumbnailURL());
        stmt.setString(15, trail.getWebpageURL());
        stmt.setString(16, trail.getCultureUrl());
        stmt.setDouble(17, trail.getUserWeight());
        stmt.setDouble(18, trail.getLat());
        stmt.setDouble(19, trail.getLon());
    }

    /**
     * Returns a new value of trail id in the database
     *
     * @return new trail id
     */
    public int getNewTrailId() {
        String getIdQuery = "SELECT MAX(id) FROM trail";
        return queryHelper.executeQuerySingle(getIdQuery, null, this::mapMaxId).get() + 1;
    }
}
