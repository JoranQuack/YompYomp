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
        return queryHelper.executeCountQuery(
                "SELECT COUNT(*) FROM category LIMIT 1",
                null);
    }

    /**
     * Counts the number of keywords in the database.
     *
     * @return the number of keywords
     */
    private int countKeywords() {
        return queryHelper.executeCountQuery(
                "SELECT COUNT(*) FROM keyword LIMIT 1",
                null);
    }

    /**
     * Determines if the keyword and category tables are populated
     *
     * @return bool
     */
    public boolean areTablesPopulated() {
        return countCategories() > 0 && countKeywords() > 0;
    }

    /**
     * Inserts categories and their associated keywords into the database.
     *
     * @param keywords Map of category names to lists of keywords
     */
    public void insertCategoriesAndKeywords(Map<String, List<String>> keywords) {
        // Categories
        List<String> categoryNames = new ArrayList<>(keywords.keySet());
        if (!categoryNames.isEmpty()) {
            queryHelper.executeBatch("INSERT OR IGNORE INTO category (name) VALUES (?)",
                    categoryNames,
                    (stmt, category) -> stmt.setString(1, category));
        }

        // Keywords
        List<KeywordEntry> keywordEntries = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : keywords.entrySet()) {
            String category = entry.getKey();
            List<String> keywordList = entry.getValue();

            for (String keyword : keywordList) {
                keywordEntries.add(new KeywordEntry(keyword, category));
            }
        }
        if (!keywordEntries.isEmpty()) {
            queryHelper.executeBatch(
                    "INSERT OR IGNORE INTO keyword (value, categoryId) " +
                            "SELECT ?, id FROM category WHERE name = ?",
                    keywordEntries,
                    (stmt, keywordEntry) -> {
                        stmt.setString(1, keywordEntry.keyword);
                        stmt.setString(2, keywordEntry.category);
                    });
        }
    }

    /**
     * Assigns categories to a trail in the database.
     *
     * @param trailId    The trail ID
     * @param categories Set of category names to assign
     */
    public void assignTrailCategories(int trailId, Set<String> categories) {
        if (categories.isEmpty())
            return;

        List<TrailCategoryEntry> entries = new ArrayList<>();
        for (String category : categories) {
            entries.add(new TrailCategoryEntry(trailId, category));
        }

        queryHelper.executeBatch(
                "INSERT OR IGNORE INTO trailCategory (trailId, categoryId) " +
                        "SELECT ?, id FROM category WHERE name = ?",
                entries,
                (stmt, entry) -> {
                    stmt.setInt(1, entry.trailId);
                    stmt.setString(2, entry.category);
                });
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

    /**
     * Helper class to represent a keyword entry for batch processing.
     */
    private static class KeywordEntry {
        final String keyword;
        final String category;

        KeywordEntry(String keyword, String category) {
            this.keyword = keyword;
            this.category = category;
        }
    }

    /**
     * Helper class to represent a trail-category association for batch processing.
     */
    private static class TrailCategoryEntry {
        final int trailId;
        final String category;

        TrailCategoryEntry(int trailId, String category) {
            this.trailId = trailId;
            this.category = category;
        }
    }
}
