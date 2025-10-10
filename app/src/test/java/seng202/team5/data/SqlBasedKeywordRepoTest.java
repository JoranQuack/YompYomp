package seng202.team5.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seng202.team5.models.Trail;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SqlBasedKeywordRepoTest {

    private SqlBasedKeywordRepo sqlBasedKeywordRepo;
    private DatabaseService databaseService;
    private String testDbPath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        testDbPath = tempDir.resolve("test.db").toString();
        databaseService = new DatabaseService(testDbPath);
        sqlBasedKeywordRepo = new SqlBasedKeywordRepo(databaseService);

        try (Connection connection = databaseService.getConnection();
                Statement stmt = connection.createStatement()) {

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS category (
                        id   INTEGER PRIMARY KEY,
                        name TEXT    UNIQUE
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS keyword (
                        value       TEXT    NOT NULL,
                        categoryId INTEGER NOT NULL
                                            REFERENCES category (id),
                        PRIMARY KEY (
                            value,
                            categoryId
                        )
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS trail (
                        id INTEGER PRIMARY KEY,
                        name TEXT
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS trailCategory (
                        trailId INTEGER NOT NULL REFERENCES trail (id),
                        categoryId INTEGER NOT NULL REFERENCES category (id),
                        PRIMARY KEY (trailId, categoryId)
                    );
                           \s""");
            stmt.execute("DELETE FROM keyword");
            stmt.execute("DELETE FROM category");

            stmt.execute("INSERT INTO category (id, name) VALUES (1, 'FamilyFriendly')");
            stmt.execute("INSERT INTO category (id, name) VALUES (2, 'Forest')");
            stmt.execute("INSERT INTO category (id, name) VALUES (3, 'Alpine')");
            stmt.execute("INSERT INTO keyword (value, categoryId) VALUES ('children', 1)");
            stmt.execute("INSERT INTO keyword (value, categoryId) VALUES ('easy', 1)");
            stmt.execute("INSERT INTO keyword (value, categoryId) VALUES ('forest', 2)");
            stmt.execute("INSERT INTO keyword (value, categoryId) VALUES ('bush', 2)");
            stmt.execute("INSERT INTO keyword (value, categoryId) VALUES ('mountain', 3)");
            stmt.execute("INSERT INTO keyword (value, categoryId) VALUES ('alpine', 3)");
            stmt.execute("INSERT INTO trail (id, name) VALUES (1, 'Test Trail')");
            stmt.execute("INSERT INTO trailCategory (trailId, categoryId) VALUES (1, 1)");
            stmt.execute("INSERT INTO trailCategory (trailId, categoryId) VALUES (1, 2)");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = databaseService.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS trailCategory");
            stmt.execute("DROP TABLE IF EXISTS keyword");
            stmt.execute("DROP TABLE IF EXISTS trail");
            stmt.execute("DROP TABLE IF EXISTS category");
        }

        File dbFile = new File(testDbPath);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    @DisplayName("Should retrieve keywords grouped by category from database")
    void testGetKeywords() {
        Map<String, List<String>> keywords = sqlBasedKeywordRepo.getKeywords();

        assertNotNull(keywords);
        assertEquals(3, keywords.size());

        assertTrue(keywords.containsKey("FamilyFriendly"));
        assertTrue(keywords.containsKey("Forest"));
        assertTrue(keywords.containsKey("Alpine"));

        assertEquals(2, keywords.get("FamilyFriendly").size());
        assertTrue(keywords.get("FamilyFriendly").contains("children"));
        assertTrue(keywords.get("FamilyFriendly").contains("easy"));

        assertEquals(2, keywords.get("Forest").size());
        assertTrue(keywords.get("Forest").contains("forest"));
        assertTrue(keywords.get("Forest").contains("bush"));

        assertEquals(2, keywords.get("Alpine").size());
        assertTrue(keywords.get("Alpine").contains("mountain"));
        assertTrue(keywords.get("Alpine").contains("alpine"));
    }

    @Test
    @DisplayName("Should correctly count the number of categories in the database")
    void testCountCategories() {
        int categoryCount = sqlBasedKeywordRepo.countCategories();
        assertEquals(3, categoryCount, "There should be 3 categories in the database");
    }

    @Test
    @DisplayName("Should correctly get the categories for a given trail")
    void testGetCategoriesForTrail() {
        Set<String> categories = sqlBasedKeywordRepo.getCategoriesForTrail(1);
        assertNotNull(categories);
        assertFalse(categories.isEmpty(), "Trail 1 has two categories assigned");
        assertTrue(categories.contains("FamilyFriendly"));
        assertTrue(categories.contains("Forest"));
    }

    @Test
    @DisplayName("Should return all trail categories together")
    void testGetAllTrailCategories() {
        Map<Integer, Set<String>> trailCategories = sqlBasedKeywordRepo.getAllTrailCategories();
        assertNotNull(trailCategories);
        assertTrue(trailCategories.containsKey(1), "Trail 1 should be present");
        assertEquals(2, trailCategories.get(1).size(), "Trail 1 should have two categories");
        assertTrue(trailCategories.get(1).contains("FamilyFriendly"));
        assertTrue(trailCategories.get(1).contains("Forest"));
    }

    @Test
    @DisplayName("Should return empty set for trail with no categories")
    void testGetCategoriesForTrailWithNoCategories() {
        Set<String> categories = sqlBasedKeywordRepo.getCategoriesForTrail(999);
        assertNotNull(categories);
        assertTrue(categories.isEmpty(), "Non-existent trail should have no categories");
    }

    @Test
    @DisplayName("Should return empty map when no trail categories exist")
    void testGetAllTrailCategoriesWhenEmpty() throws SQLException {
        try (Connection conn = databaseService.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM trailCategory");
        }

        Map<Integer, Set<String>> trailCategories = sqlBasedKeywordRepo.getAllTrailCategories();
        assertNotNull(trailCategories);
        assertTrue(trailCategories.isEmpty(), "Should return empty map when no trail categories exist");
    }

    @Test
    @DisplayName("Should insert new categories and keywords into database")
    void testInsertCategoriesAndKeywords() throws SQLException {
        try (Connection conn = databaseService.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM trailCategory");
            stmt.execute("DELETE FROM keyword");
            stmt.execute("DELETE FROM category");
        }

        // creating the data
        Map<String, List<String>> testKeywords = new LinkedHashMap<>();
        testKeywords.put("Adventure", Arrays.asList("exciting", "thrill"));
        testKeywords.put("Scenic", Arrays.asList("beautiful", "view"));

        // Insert
        sqlBasedKeywordRepo.insertCategoriesAndKeywords(testKeywords);

        assertEquals(2, sqlBasedKeywordRepo.countCategories());

        Map<String, List<String>> retrievedKeywords = sqlBasedKeywordRepo.getKeywords();
        assertEquals(2, retrievedKeywords.size());
        assertTrue(retrievedKeywords.containsKey("Adventure"));
        assertTrue(retrievedKeywords.containsKey("Scenic"));
        assertEquals(2, retrievedKeywords.get("Adventure").size());
        assertEquals(2, retrievedKeywords.get("Scenic").size());
    }

    @Test
    @DisplayName("Should assign categories to trails")
    void testAssignTrailCategories() throws SQLException {
        try (Connection conn = databaseService.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM trailCategory");
            stmt.execute("INSERT INTO trail (id, name) VALUES (2, 'Test Trail 2')");
        }

        // Create the test trails
        Trail trail1 = new Trail.Builder()
                .id(1)
                .name("Trail 1")
                .categories(Set.of("FamilyFriendly", "Alpine"))
                .build();

        Trail trail2 = new Trail.Builder()
                .id(2)
                .name("Trail 2")
                .categories(Set.of("Forest"))
                .build();

        List<Trail> trails = Arrays.asList(trail1, trail2);
        sqlBasedKeywordRepo.assignTrailCategories(trails);

        // Verify assignments
        Set<String> trail1Categories = sqlBasedKeywordRepo.getCategoriesForTrail(1);
        assertEquals(2, trail1Categories.size());
        assertTrue(trail1Categories.contains("FamilyFriendly"));
        assertTrue(trail1Categories.contains("Alpine"));

        Set<String> trail2Categories = sqlBasedKeywordRepo.getCategoriesForTrail(2);
        assertEquals(1, trail2Categories.size());
        assertTrue(trail2Categories.contains("Forest"));
    }

    @Test
    @DisplayName("Should handle categories with null and empty keywords")
    void testGetKeywordsWithNullAndEmptyKeywords() throws SQLException {
        try (Connection conn = databaseService.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO category (id, name) VALUES (4, 'EmptyCategory')");
            stmt.execute("INSERT INTO category (id, name) VALUES (5, 'MixedCategory')");
            stmt.execute("INSERT INTO keyword (value, categoryId) VALUES ('valid', 5)");
            stmt.execute("INSERT INTO keyword (value, categoryId) VALUES ('', 5)");
            stmt.execute("INSERT INTO keyword (value, categoryId) VALUES ('   ', 5)");
        }

        Map<String, List<String>> keywords = sqlBasedKeywordRepo.getKeywords();

        assertTrue(keywords.containsKey("EmptyCategory"));
        assertTrue(keywords.get("EmptyCategory").isEmpty(), "EmptyCategory should have no keywords");

        assertTrue(keywords.containsKey("MixedCategory"));
        List<String> mixedKeywords = keywords.get("MixedCategory");
        assertEquals(1, mixedKeywords.size(), "Only valid keyword should be included");
        assertTrue(mixedKeywords.contains("valid"));
    }
}
