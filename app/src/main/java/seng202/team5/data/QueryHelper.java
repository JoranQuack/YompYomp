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
 *
 * Supports single/multiple result queries, update/insert/delete operations, count queries,
 * and batch operations. Uses functional interfaces for parameter setting and row mapping.
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
     * @param sql          the SQL query string
     * @param paramSetter  functional interface to set query parameters
     * @param rowMapper    functional interface to map each ResultSet row to a type T
     * @return a list of results
     * @param <T>          the type of results
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
     * @param sql          the SQL query string
     * @param paramSetter  functional interface to set query parameters
     * @param rowMapper    functional interface to map each ResultSet row to a type T
     * @return             an optional containing the result if found or empty if no rows
     * @param <T>          the type of the result
     */
    public <T> Optional<T> executeQuerySingle(String sql, ParameterSetter paramSetter, RowMapper<T> rowMapper) {
        List<T> results = executeQuery(sql, paramSetter, rowMapper);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    /**
     * Execute an update/insert/delete operation
     * @param sql          the SQL statement
     * @param paramSetter  functional interface to set statement parameters
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
     * @param sql          the SQL query string
     * @param paramSetter  functional interface to set query parameters
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

    /**
     * Execute multiple SQL statements in a single transaction because the lab
     * computers are super duper slow
     *
     * @param statements List of SQL statements with their parameter setters
     */
    public void executeTransaction(List<SqlStatement> statements) {
        if (statements.isEmpty()) {
            return;
        }

        try (Connection conn = databaseService.getConnection()) {
            conn.setAutoCommit(false);

            try {
                for (SqlStatement statement : statements) {
                    try (PreparedStatement stmt = conn.prepareStatement(statement.sql())) {
                        if (statement.paramSetter() != null) {
                            statement.paramSetter().setParameters(stmt);
                        }
                        stmt.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Transaction execution failed", e);
        }
    }

    /**
     * Record to hold SQL statement and its parameter setter
     */
    public record SqlStatement(String sql, ParameterSetter paramSetter) {
    }

    /**
     * Functional interface for setting parameters on a PreparedStatement.
     */
    @FunctionalInterface
    public interface ParameterSetter {
        /**
         * Set parameters on a PreparedStatement before execution
         *
         * @param stmt the PreparedStatement
         * @throws SQLException if setting parameters fails
         */
        void setParameters(PreparedStatement stmt) throws SQLException;
    }

    /**
     * Functional interface for mapping a single ResultSet row to an object of type T.
     *
     * @param <T> the type of the mapped object
     */
    @FunctionalInterface
    public interface RowMapper<T> {
        /**
         * Maps a single row of the ResultSet to an object.
         *
         * @param rs the ResultSet, positions at the current row
         * @return the mapped object
         * @throws SQLException if reading from ResultSet fails
         */
        T mapRow(ResultSet rs) throws SQLException;
    }

    /**
     * Functional interface for setting parameters for a batch update on a PreparedStatement.
     *
     * @param <T> the type of the items in the batch
     */
    @FunctionalInterface
    public interface BatchParameterSetter<T> {
        /**
         * Sets parameters for a single item in a batch.
         *
         * @param stmt the PreparedStatement
         * @param item the item to set parameters for
         * @throws SQLException if setting parameters fails
         */
        void setParameters(PreparedStatement stmt, T item) throws SQLException;
    }
}
