package seng202.team5.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                            """);
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
    void testGetCategoriesForTrailWithNoCategories() {
        Map<Integer, Set<String>> trailCategories = sqlBasedKeywordRepo.getAllTrailCategories();
        assertNotNull(trailCategories);
        assertTrue(trailCategories.containsKey(1), "Trail 1 should be present");
        assertEquals(2, trailCategories.get(1).size(), "Trail 1 should have two categories");
        assertTrue(trailCategories.get(1).contains("FamilyFriendly"));
        assertTrue(trailCategories.get(1).contains("Forest"));
    }
}
