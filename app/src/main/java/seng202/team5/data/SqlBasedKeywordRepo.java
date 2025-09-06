package seng202.team5.data;

import java.util.*;

/**
 * Repository for accessing keyword and category data from the database.
 * This class provides methods to retrieve keywords grouped by categories from
 * the SQL database.
 */
public class SqlBasedKeywordRepo {
    private final QueryHelper queryHelper;

    private static final String SELECT_ALL_CATEGORIES_WITH_KEYWORDS = """
            SELECT c.name as category_name, k.value as keyword_value
            FROM category c
            LEFT JOIN keyword k ON c.id = k.category_id
            ORDER BY c.name, k.value
            """;

    /**
     * Creates a SQL-based keyword repository
     *
     * @param databaseService provider of JDBC connection used by QueryHelper
     */
    public SqlBasedKeywordRepo(DatabaseService databaseService) {
        this.queryHelper = new QueryHelper(databaseService);
    }

    /**
     * Returns keywords grouped by category from the database
     *
     * @return Map of category names to lists of keywords in that category
     */
    public Map<String, List<String>> getKeywords() {
        Map<String, List<String>> categoryKeywords = new LinkedHashMap<>();

        queryHelper.executeQuery(SELECT_ALL_CATEGORIES_WITH_KEYWORDS, null, rs -> {
            String categoryName = rs.getString("category_name");
            String keywordValue = rs.getString("keyword_value");

            // Ensure category exists in map
            categoryKeywords.putIfAbsent(categoryName, new ArrayList<>());

            // Add keyword if it's not null (categories might not have keywords)
            if (keywordValue != null && !keywordValue.trim().isEmpty()) {
                categoryKeywords.get(categoryName).add(keywordValue);
            }

            return null; // We're building the map as a side effect
        });

        return categoryKeywords;
    }
}
