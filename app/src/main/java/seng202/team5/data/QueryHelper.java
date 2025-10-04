package seng202.team5.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Query helper for executing SQL
 */
public class QueryHelper {
    private final DatabaseService databaseService;

    /**
     * Constructor for QueryHelper class
     *
     * @param databaseService the databaseService instance
     */
    public QueryHelper(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Execute a query for result list
     * @param sql
     * @param paramSetter
     * @param rowMapper
     * @return a list of results
     * @param <T>
     */
    public <T> List<T> executeQuery(String sql, ParameterSetter paramSetter, RowMapper<T> rowMapper) {
        List<T> results = new ArrayList<>();
        try (Connection conn = databaseService.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (paramSetter != null) {
                paramSetter.setParameters(stmt);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query execution failed: " + sql, e);
        }
        return results;
    }

    /**
     * Execute a query for single result
     * @param sql
     * @param paramSetter
     * @param rowMapper
     * @return
     * @param <T>
     */
    public <T> Optional<T> executeQuerySingle(String sql, ParameterSetter paramSetter, RowMapper<T> rowMapper) {
        List<T> results = executeQuery(sql, paramSetter, rowMapper);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Execute an update/insert/delete operation
     *
     * @return the number of affected rows
     */
    public int executeUpdate(String sql, ParameterSetter paramSetter) {
        try (Connection conn = databaseService.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (paramSetter != null) {
                paramSetter.setParameters(stmt);
            }

            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Update execution failed: " + sql, e);
        }
    }

    /**
     * Execute a query that returns a single integer (like COUNT)
     *
     * @return the integer result
     */
    public int executeCountQuery(String sql, ParameterSetter paramSetter) {
        try (Connection conn = databaseService.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (paramSetter != null) {
                paramSetter.setParameters(stmt);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Count query execution failed: " + sql, e);
        }
    }

    /**
     * Execute batch updates when inserting/updating multiple records (fast)
     *
     * @param sql         the SQL statement to execute for EACH item
     * @param items       the list of items to process
     * @param paramSetter functional interface to set parameters for each item
     * @return array of update counts for each batch item
     */
    public <T> int[] executeBatch(String sql, List<T> items, BatchParameterSetter<T> paramSetter) {
        if (items.isEmpty()) {
            return new int[0];
        }

        try (Connection conn = databaseService.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // temp disable auto-commit for speeeeeed
            conn.setAutoCommit(false);

            try {
                for (T item : items) {
                    paramSetter.setParameters(stmt, item);
                    stmt.addBatch();
                }

                int[] results = stmt.executeBatch();
                conn.commit(); // Commit all at once
                return results;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Batch execution failed: " + sql, e);
        }
    }

    @FunctionalInterface
    public interface ParameterSetter {
        void setParameters(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    public interface BatchParameterSetter<T> {
        void setParameters(PreparedStatement stmt, T item) throws SQLException;
    }
}
