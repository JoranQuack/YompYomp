package seng202.team5.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileBasedKeywordRepoTest {

    private FileBasedKeywordRepo validRepo;
    private FileBasedKeywordRepo invalidRepo;
    private FileBasedKeywordRepo emptyRepo;

    @BeforeEach
    void setUp() {
        // actual Categories and Keywords csv used in the application
        validRepo = new FileBasedKeywordRepo("/datasets/Categories_and_Keywords.csv");

        // non-existent file path
        invalidRepo = new FileBasedKeywordRepo("/nonexistent/file.csv");

        // empty path
        emptyRepo = new FileBasedKeywordRepo("");
    }

    @Test
    @DisplayName("Constructor should successfully load trails from valid CSV file")
    void testConstructorWithValidCsvFile() {
        assertNotNull(validRepo);
        assertDoesNotThrow(() -> new FileBasedKeywordRepo("/datasets/Categories_and_Keywords.csv"));
    }

    @Test
    @DisplayName("Constructor should handle invalid CSV file path gracefully")
    void testConstructorWithInvalidCsvFile() {
        assertNotNull(invalidRepo);
        // Should not throw exception, but trails list should be empty
        assertTrue(invalidRepo.getKeywords().isEmpty());
    }

    @Test
    @DisplayName("Constructor should handle empty file path gracefully")
    void testConstructorWithEmptyPath() {
        assertNotNull(emptyRepo);
        assertTrue(emptyRepo.getKeywords().isEmpty());
    }
}
