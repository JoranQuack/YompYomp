package seng202.team5.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Trail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrailsProcessorTest {
    private List<Trail> mockTrails;
    private Trail currentTrail;

    @BeforeEach
    void setup() {
        mockTrails = new ArrayList<>(Arrays.asList(
                new Trail(1, "Alpine Trail", "Easy", "A beautiful alpine trail through the mountains",
                        "2 hours", "thumb1.jpg", "http://example.com/trail1", -43.5225, 172.5794), // Christchurch
                new Trail(2, "Forest Trail", "Medium", "A scenic forest trail with wildlife viewing",
                        "3 hours", "thumb2.jpg", "http://example.com/trail2", -43.5390, 172.6300), // ~5km away
                new Trail(3, "Mountain Peak Trail", "Hard", "Challenging trail to the mountain peak",
                        "5 hours", "thumb3.jpg", "http://example.com/trail3", -36.8485, 174.7633) // Auckland ~760km
        ));

        currentTrail = mockTrails.get(0); // Alpine Trail
    }

    @Test
    @DisplayName("No trails should be within 1km radius")
    void testNoNearbyTrailsWhenRadiusTooSmall() {
        List<Trail> results = TrailsProcessor.getNearbyTrails(currentTrail, 1, mockTrails);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Only one trail should be within 10km radius")
    void testNearbyTrailFoundWithinRadius() {
        List<Trail> results = TrailsProcessor.getNearbyTrails(currentTrail, 10, mockTrails);
        assertEquals(1, results.size());
        assertEquals("Forest Trail", results.get(0).getName());
    }

    @Test
    @DisplayName("Faraway trails should be excluded at 100km radius")
    void testFarTrailExcluded() {
        List<Trail> results = TrailsProcessor.getNearbyTrails(currentTrail, 100, mockTrails);
        assertEquals(1, results.size());
        assertEquals("Forest Trail", results.get(0).getName());
    }

    @Test
    @DisplayName("All trails should be included when distance radius is large enough")
    void allTrailsIncludedWithLargeDistance() {
        List<Trail> results = TrailsProcessor.getNearbyTrails(currentTrail, 1000, mockTrails);
        assertEquals(2, results.size());
        assertEquals("Forest Trail", results.get(0).getName());
        assertEquals("Mountain Peak Trail", results.get(1).getName());
    }

    @Test
    @DisplayName("Current trail should not appear in the results")
    void testExcludesCurrentTrailItself() {
        List<Trail> results = TrailsProcessor.getNearbyTrails(currentTrail, 10, mockTrails);
        assertEquals(1, results.size());
        assertEquals("Forest Trail", results.get(0).getName());
    }
}
