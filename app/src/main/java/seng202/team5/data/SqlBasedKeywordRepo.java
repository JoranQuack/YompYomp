package seng202.team5.data;

import java.util.*;

/**
 * Repository for accessing keyword and category data from the database.
 * This class provides methods to retrieve keywords grouped by categories from
 * the SQL database.
 */
public class SqlBasedKeywordRepo implements IKeyword {
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

            categoryKeywords.putIfAbsent(categoryName, new ArrayList<>());
            if (keywordValue != null && !keywordValue.trim().isEmpty()) {
                categoryKeywords.get(categoryName).add(keywordValue);
            }

            return null;
        });

        return categoryKeywords;
    }

    public void insertCategoriesAndKeywords(Map<String, List<String>> keywords) {
        for (Map.Entry<String, List<String>> entry : keywords.entrySet()) {
            String category = entry.getKey();
            List<String> keywordList = entry.getValue();

            // Insert category
            queryHelper.executeUpdate("INSERT INTO category (name) VALUES (?)", stmt -> stmt.setString(1, category));

            // Insert keywords
            for (String keyword : keywordList) {
                queryHelper.executeUpdate("INSERT INTO keyword (value, category_id) VALUES (?, ?)", stmt -> {
                    stmt.setString(1, keyword);
                    stmt.setInt(2, getCategoryId(category));
                });
            }
        }
    }

    /**
     * Helper method to get the ID of a category by its name.
     *
     * @param category
     * @return
     */
    private int getCategoryId(String category) {
        List<Integer> results = queryHelper
                .executeQuery("SELECT id FROM category WHERE name = ?", ps -> ps.setString(1, category), rs -> {
                    if (rs.next()) {
                        return rs.getInt("id");
                    } else {
                        throw new RuntimeException("Category not found: " + category);
                    }
                });

        return results.get(0);
    }
}
