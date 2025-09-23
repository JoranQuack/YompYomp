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
            LEFT JOIN keyword k ON c.id = k.categoryId
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

    /**
     * Counts the number of categories in the database.
     *
     * @return the number of categories
     */
    public int countCategories() {
        List<Integer> results = queryHelper.executeQuery("SELECT COUNT(*) AS count FROM category", null, rs -> {
            if (rs.next()) {
                return rs.getInt("count");
            } else {
                return 0;
            }
        });

        return results.get(0);
    }

    /**
     * Inserts categories and their associated keywords into the database.
     *
     * @param keywords
     */
    public void insertCategoriesAndKeywords(Map<String, List<String>> keywords) {
        for (Map.Entry<String, List<String>> entry : keywords.entrySet()) {
            String category = entry.getKey();
            List<String> keywordList = entry.getValue();

            // Insert category
            queryHelper.executeUpdate("INSERT OR IGNORE INTO category (name) VALUES (?)",
                    stmt -> stmt.setString(1, category));

            // Insert keywords
            for (String keyword : keywordList) {
                queryHelper.executeUpdate(
                        "INSERT OR IGNORE INTO keyword (value, categoryId) " +
                                "SELECT ?, id FROM category WHERE name = ?",
                        stmt -> {
                            stmt.setString(1, keyword);
                            stmt.setString(2, category);
                        });
            }
        }
    }

    /**
     * Assigns categories to a trail in the database.
     *
     * @param trailId
     * @param categories
     */
    public void assignTrailCategories(int trailId, Set<String> categories) {
        for (String category : categories) {
            queryHelper.executeUpdate(
                    "INSERT OR IGNORE INTO trailCategory (trailId, categoryId) " +
                            "SELECT ?, id FROM category WHERE name = ?",
                    stmt -> {
                        stmt.setInt(1, trailId);
                        stmt.setString(2, category);
                    });
        }
    }

    /**
     * Gets all categories for a given trail.
     *
     * @param trailId
     * @return set of category names
     */
    public Set<String> getCategoriesForTrail(int trailId) {
        Set<String> categories = new HashSet<>();
        queryHelper.executeQuery(
                "SELECT c.name FROM category c " +
                        "JOIN trailCategory tc ON c.id = tc.categoryId " +
                        "WHERE tc.trailId = ?",
                stmt -> stmt.setInt(1, trailId),
                rs -> {
                    categories.add(rs.getString("name"));
                    return null;
                });
        return categories;
    }
}
