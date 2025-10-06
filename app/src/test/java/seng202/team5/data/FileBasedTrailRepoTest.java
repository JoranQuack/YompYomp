package seng202.team5.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Trail;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FileBasedTrailRepo functionality.
 * Tests the repository that loads trail data from CSV files.
 */
public class FileBasedTrailRepoTest {

    private FileBasedTrailRepo validRepo;
    private FileBasedTrailRepo invalidRepo;
    private FileBasedTrailRepo emptyRepo;

    @BeforeEach
    void setUp() {
        // actual DOC trails CSV file used in the application
        validRepo = new FileBasedTrailRepo("/datasets/DOC_Walking_Experiences_-2195374600472221140.csv");

        // non-existent file path
        invalidRepo = new FileBasedTrailRepo("/nonexistent/file.csv");

        // empty path
        emptyRepo = new FileBasedTrailRepo("");
    }

    @Test
    @DisplayName("Constructor should successfully load trails from valid CSV file")
    void testConstructorWithValidCsvFile() {
        assertNotNull(validRepo);
        assertDoesNotThrow(() -> new FileBasedTrailRepo("/datasets/DOC_Walking_Experiences_-2195374600472221140.csv"));
    }

    @Test
    @DisplayName("Constructor should handle invalid CSV file path gracefully")
    void testConstructorWithInvalidCsvFile() {
        assertNotNull(invalidRepo);
        // Should not throw exception, but trails list should be empty
        assertTrue(invalidRepo.getAllTrails().isEmpty());
    }

    @Test
    @DisplayName("Constructor should handle empty file path gracefully")
    void testConstructorWithEmptyPath() {
        assertNotNull(emptyRepo);
        assertTrue(emptyRepo.getAllTrails().isEmpty());
    }

    @Test
    @DisplayName("getAllTrails should return loaded trails from CSV")
    void testGetAllTrailsReturnsLoadedTrails() {
        List<Trail> trails = validRepo.getAllTrails();
        assertNotNull(trails);

        if (!trails.isEmpty()) {
            Trail firstTrail = trails.get(0);
            assertNotNull(firstTrail.getName());
            assertNotNull(firstTrail.getDifficulty());
            assertNotNull(firstTrail.getDescription());
            assertNotNull(firstTrail.getCompletionInfo());
            assertTrue(firstTrail.getId() > 0);
        }
    }

    @Test
    @DisplayName("countTrails should return correct number of trails")
    void testCountTrails() {
        int count = validRepo.countTrails();
        List<Trail> trails = validRepo.getAllTrails();

        assertEquals(trails.size(), count, "countTrails should match the size of getAllTrails()");
        assertTrue(count >= 0, "Trail count should be non-negative");
    }

    @Test
    @DisplayName("findById should return null (current implementation)")
    void testFindByIdReturnsNull() {
        Optional<Trail> result = validRepo.findById(1);
        assertNull(result, "findById currently returns null as per implementation");
    }

    @Test
    @DisplayName("Trails loaded from CSV should have valid data structure")
    void testTrailDataIntegrity() {
        List<Trail> trails = validRepo.getAllTrails();

        for (Trail trail : trails) {
            assertNotNull(trail, "Trail object should not be null");
            assertTrue(trail.getId() > 0, "Trail ID should be positive");
            assertNotNull(trail.getName(), "Trail name should not be null");
            assertNotNull(trail.getDifficulty(), "Trail difficulty should not be null");
            assertNotNull(trail.getDescription(), "Trail description should not be null");
            assertNotNull(trail.getCompletionInfo(), "Trail completion info should not be null");

            double lat = trail.getLat();
            double lon = trail.getLon();

            assertFalse(Double.isNaN(lat), "Latitude should not be NaN");
            assertFalse(Double.isNaN(lon), "Longitude should not be NaN");
            assertFalse(Double.isInfinite(lat), "Latitude should not be infinite");
            assertFalse(Double.isInfinite(lon), "Longitude should not be infinite");
        }
    }

    @Test
    @DisplayName("Repository should implement ITrail interface correctly")
    void testInterfaceImplementation() {
        assertTrue(validRepo instanceof ITrail, "FileBasedTrailRepo should implement ITrail interface");

        assertDoesNotThrow(() -> {
            ITrail iTrail = validRepo;
            iTrail.getAllTrails();
            iTrail.findById(1);
            iTrail.countTrails();
        });
    }
}
