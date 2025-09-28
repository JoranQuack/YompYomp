package seng202.team5.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.locationtech.jts.geom.Geometry;

import seng202.team5.models.Trail;

public class RegionFinderTest {

    private static final String SHAPEFILE_PATH = "/datasets/regional/";
    private static final String SHAPEFILE_NAME = "regional-council-2025.shp";
    private static RegionFinder regionFinder;

    @BeforeAll
    public static void setup() {
        regionFinder = new RegionFinder(SHAPEFILE_PATH, SHAPEFILE_NAME);
    }

    @Test
    public void testGetAllRegions() {
        Map<String, Geometry> regions = regionFinder.getAllRegions();
        assertNotNull(regions);
        assertFalse(regions.isEmpty());
    }

    @Test
    public void testFindRegionForPoint() {
        // coordinates in Auckland
        String region = regionFinder.findRegionForPoint(174.7444827976006, -36.880933915772516);
        assertNotNull(region);
        assertEquals(region, "Auckland");
    }

    @Test
    public void testFindRegionForNullIsland() {
        String region = regionFinder.findRegionForPoint(0, 0);
        assertNotNull(region);
        assertEquals(region, "Other");
    }

    @Test
    public void testFindRegionForOceanCoordinates() {
        // coordinates in ocean off the coast of NZ
        String region = regionFinder.findRegionForPoint(169.3195787026245, -42.3102611205709);
        assertNotNull(region);
        assertEquals(region, "Other");
    }

    @Test
    public void testFindRegionForInvalidCoordinates() {
        String region = regionFinder.findRegionForPoint(10000, 10000);
        assertNotNull(region);
        assertEquals(region, "Other");
    }

    @Test
    public void testFindRegionForBorderCoordinates() {
        // coordinates on the border between Otago and Canterbury
        String region = regionFinder.findRegionForPoint(171.10225477776783, -44.92984092565437);
        assertNotNull(region);
        assertEquals(region, "Otago");
    }

    @Test
    public void testFindRegionForBorderCoordinates2() {
        // coordinates on the border between Otago and Canterbury (this time on
        // Canterbury side)
        String region = regionFinder.findRegionForPoint(171.11224219061646, -44.92683616374543);
        assertNotNull(region);
        assertEquals(region, "Canterbury");
    }

    @Test
    public void testFindRegionForTrail() {
        Trail mockTrail = new Trail();
        mockTrail.setLat(174.7444827976006);
        mockTrail.setLon(-36.880933915772516);
        String region = regionFinder.findRegionForTrail(mockTrail);
        assertNotNull(region);
        assertEquals(region, "Auckland");
    }

    @Test
    public void testFindRegionForNullTrail() {
        String region = regionFinder.findRegionForTrail(null);
        assertNotNull(region);
        assertEquals(region, "Other");
    }
}
