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
                new Trail.Builder()
                        .id(1)
                        .name("Alpine Trail")
                        .difficulty("Easy")
                        .description("A beautiful alpine trail through the mountains")
                        .completionInfo("2 hours")
                        .thumbnailURL("thumb1.jpg")
                        .webpageURL("https://example.com/trail1")
                        .lat(-43.5225)
                        .lon(172.5794) // Christchurch
                        .build(),
                new Trail.Builder()
                        .id(2)
                        .name("Forest Trail")
                        .difficulty("Medium")
                        .description("A scenic forest trail with wildlife viewing")
                        .completionInfo("3 hours")
                        .thumbnailURL("thumb2.jpg")
                        .webpageURL("https://example.com/trail2")
                        .lat(-43.5390)
                        .lon(172.6300) // ~5km away
                        .build(),
                new Trail.Builder()
                        .id(3)
                        .name("Mountain Peak Trail")
                        .difficulty("Hard")
                        .description("Challenging trail to the mountain peak")
                        .completionInfo("5 hours")
                        .thumbnailURL("thumb3.jpg")
                        .webpageURL("https://example.com/trail3")
                        .lat(-36.8485)
                        .lon(174.7633) // Auckland ~760km
                        .build()
        ));

        currentTrail = mockTrails.getFirst(); // Alpine Trail
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
        assertEquals("Forest Trail", results.getFirst().getName());
    }

    @Test
    @DisplayName("Faraway trails should be excluded at 100km radius")
    void testFarTrailExcluded() {
        List<Trail> results = TrailsProcessor.getNearbyTrails(currentTrail, 100, mockTrails);
        assertEquals(1, results.size());
        assertEquals("Forest Trail", results.getFirst().getName());
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
        assertEquals("Forest Trail", results.getFirst().getName());
    }
}
