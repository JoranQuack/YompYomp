package seng202.team5.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seng202.team5.data.DatabaseService;

/**
 * Query helper for executing SQL
 */
public class QueryHelper {
    private final DatabaseService databaseService;

    public QueryHelper(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Execute a query for result list
     *
     * @return a list of results
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
     *
     * @return a single optional result
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

    @FunctionalInterface
    public interface ParameterSetter {
        void setParameters(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }
}
