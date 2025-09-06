package seng202.team5.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SqlBasedKeywordRepoTest {

    private SqlBasedKeywordRepo sqlBasedKeywordRepo;
    private DatabaseService databaseService;
    private String testDbPath;

    @BeforeEach
    void setUp() throws Exception {
        try {
            testDbPath = TestPathHelper.getTestResourcePath("database/test.db");
            databaseService = new DatabaseService(testDbPath);
        } catch (Exception e) {
            // Fallback to in-memory database for CI environments
            System.err.println(
                    "Warning: Could not find test database file, using in-memory database. Error: " + e.getMessage());
            testDbPath = ":memory:";
            databaseService = new DatabaseService(testDbPath);
        }
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
                        category_id INTEGER NOT NULL
                                            REFERENCES category (id),
                        PRIMARY KEY (
                            value,
                            category_id
                        )
                    )
                    """);
            stmt.execute("DELETE FROM keyword");
            stmt.execute("DELETE FROM category");

            stmt.execute("INSERT INTO category (id, name) VALUES (1, 'FamilyFriendly')");
            stmt.execute("INSERT INTO category (id, name) VALUES (2, 'Forest')");
            stmt.execute("INSERT INTO category (id, name) VALUES (3, 'Alpine')");
            stmt.execute("INSERT INTO keyword (value, category_id) VALUES ('children', 1)");
            stmt.execute("INSERT INTO keyword (value, category_id) VALUES ('easy', 1)");
            stmt.execute("INSERT INTO keyword (value, category_id) VALUES ('forest', 2)");
            stmt.execute("INSERT INTO keyword (value, category_id) VALUES ('bush', 2)");
            stmt.execute("INSERT INTO keyword (value, category_id) VALUES ('mountain', 3)");
            stmt.execute("INSERT INTO keyword (value, category_id) VALUES ('alpine', 3)");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = databaseService.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS keyword");
            stmt.execute("DROP TABLE IF EXISTS category");
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
}
