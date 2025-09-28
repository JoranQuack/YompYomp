package seng202.team5.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeoUtilsTest {

    @Test
    @DisplayName("Distance from a point to itself should be 0")
    public void testZeroDistanceSamePoint() {
        double distance = GeoUtils.distanceKm(0, 0, 0, 0);
        assertEquals(0.0, distance);
    }

    @Test
    @DisplayName("Should accurately calculate the distance with a 1 degree latitude difference")
    public void testSmallDistance() {
        double distance = GeoUtils.distanceKm(0, 0, 1, 0);
        assertEquals(111, distance, 1.0); // approx 111km between (0,0) and (1,0)
    }

    @Test
    @DisplayName("Should correctly calculate large distances")
    public void testLargeDistance() {
        // Opposite sides of the Earth (0,0) and (0,180) ~ half Earth circumference (~20037 km)
        double distance = GeoUtils.distanceKm(0, 0, 0, 180);
        assertEquals(20037, distance, 100);

        // North Pole to equator ~ 10007 km
        double distance2 = GeoUtils.distanceKm(90, 0, 0, 0);
        assertEquals(10007, distance2, 10);
    }
}
