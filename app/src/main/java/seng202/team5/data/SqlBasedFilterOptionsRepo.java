package seng202.team5.data;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Repository for managing filter options in the database.
 * Provides efficient access to filter dropdown options without
 * needing to process all trails in memory.
 */
public class SqlBasedFilterOptionsRepo {
    private final QueryHelper queryHelper;

    // SQL Constants
    private static final String SELECT_OPTIONS_BY_TYPE = """
            SELECT optionValue
            FROM filterOptions
            WHERE filterType = ?
            ORDER BY displayOrder, optionValue ASC
            """;

    private static final String COUNT_OPTIONS = """
            SELECT COUNT(*)
            FROM filterOptions
            WHERE filterType = ?
            """;

    private static final String UPSERT_OPTION = """
            INSERT INTO filterOptions (filterType, optionValue, displayOrder)
            VALUES (?, ?, ?)
            ON CONFLICT(filterType, optionValue) DO UPDATE SET
                displayOrder = excluded.displayOrder
            """;

    private static final String REFRESH_COMPLETION_TYPE_OPTIONS = """
            INSERT OR REPLACE INTO filterOptions (filterType, optionValue, displayOrder)
            SELECT 'completionType', completionType, 0
            FROM trail
            WHERE completionType IS NOT NULL
            AND completionType != 'unknown'
            AND completionType != ''
            GROUP BY completionType
            """;

    private static final String REFRESH_TIME_UNIT_OPTIONS = """
            INSERT OR REPLACE INTO filterOptions (filterType, optionValue, displayOrder)
            SELECT 'timeUnit', timeUnit, 0
            FROM trail
            WHERE timeUnit IS NOT NULL
            AND timeUnit != 'unknown'
            AND timeUnit != ''
            GROUP BY timeUnit
            """;

    private static final String REFRESH_DIFFICULTY_OPTIONS = """
            INSERT OR REPLACE INTO filterOptions (filterType, optionValue, displayOrder)
            SELECT 'difficulty', difficulty,
                CASE difficulty
                    WHEN 'easiest' THEN 1
                    WHEN 'easy' THEN 2
                    WHEN 'intermediate' THEN 3
                    WHEN 'advanced' THEN 4
                    WHEN 'expert' THEN 5
                    ELSE 999
                END as displayOrder
            FROM trail
            WHERE difficulty IS NOT NULL
            AND difficulty != 'unknown'
            AND difficulty != ''
            GROUP BY difficulty
            """;

    /**
     * Creates a SQL-based filter options repository
     *
     * @param databaseService provider of connection
     */
    public SqlBasedFilterOptionsRepo(DatabaseService databaseService) {
        this.queryHelper = new QueryHelper(databaseService);
    }

    /**
     * Gets all filter options for a specific filter type.
     *
     * @param filterType The filter type (e.g: "completionType", "difficulty")
     * @return List of option values
     */
    public List<String> getFilterOptions(String filterType) {
        return queryHelper.executeQuery(
                SELECT_OPTIONS_BY_TYPE,
                stmt -> stmt.setString(1, filterType),
                rs -> rs.getString("optionValue"));
    }

    /**
     * Checks if filter options exist for a given filter type.
     *
     * @param filterType The filter type to check
     * @return true if options exist, false otherwise
     */
    public boolean hasFilterOptions(String filterType) {
        Integer count = queryHelper.executeCountQuery(
                COUNT_OPTIONS,
                stmt -> stmt.setString(1, filterType));
        return count != null && count > 0;
    }

    /**
     * Refreshes all filter options by querying the trail table.
     * This should be called when trails are added/updated.
     */
    public void refreshAllFilterOptions() {
        clearAllFilterOptions();

        queryHelper.executeUpdate(REFRESH_COMPLETION_TYPE_OPTIONS, null);
        queryHelper.executeUpdate(REFRESH_TIME_UNIT_OPTIONS, null);
        queryHelper.executeUpdate(REFRESH_DIFFICULTY_OPTIONS, null);

        // Add multi-day options manually since it's a derived field
        insertOption("multiDay", "Multi-day", 1);
        insertOption("multiDay", "Day walk", 2);
    }

    /**
     * Clears all filter options from the database.
     */
    private void clearAllFilterOptions() {
        queryHelper.executeUpdate("DELETE FROM filterOptions", null);
    }

    /**
     * Inserts a single filter option.
     *
     * @param filterType   The filter type
     * @param optionValue  The option value
     * @param displayOrder The display order (lower numbers appear first)
     */
    private void insertOption(String filterType, String optionValue, int displayOrder) {
        queryHelper.executeUpdate(
                UPSERT_OPTION,
                stmt -> {
                    stmt.setString(1, filterType);
                    stmt.setString(2, optionValue);
                    stmt.setInt(3, displayOrder);
                });
    }

    /**
     * Checks if filter options are cached in the database.
     *
     * @return true if any filter options exist, false otherwise
     */
    public boolean areFilterOptionsStored() {
        Integer count = queryHelper.executeCountQuery(
                "SELECT COUNT(*) FROM filterOptions LIMIT 1",
                null);
        return count != null && count > 0;
    }

    /**
     * Gets all available filter types that have options.
     *
     * @return List of filter types
     */
    public List<String> getAvailableFilterTypes() {
        return queryHelper.executeQuery(
                "SELECT DISTINCT filterType FROM filterOptions ORDER BY filterType",
                null,
                rs -> rs.getString("filterType"));
    }

    /**
     * Gets filter options for multiple filter types efficiently.
     *
     * @param filterTypes List of filter types to get options for
     * @return Map of filter type to list of options
     */
    public Map<String, List<String>> getFilterOptionsMap(List<String> filterTypes) {
        Map<String, List<String>> optionsMap = new HashMap<>();

        for (String filterType : filterTypes) {
            optionsMap.put(filterType, getFilterOptions(filterType));
        }

        return optionsMap;
    }
}