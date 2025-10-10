package seng202.team5.services;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.locationtech.jts.geom.Geometry;

import seng202.team5.models.Trail;

import static org.junit.jupiter.api.Assertions.*;

public class RegionFinderTest {

    private static final String SHAPEFILE_PATH = "/datasets/regional/";
    private static final String SHAPEFILE_NAME = "regional-council-2025.shp";
    private static RegionFinder regionFinder;

    @BeforeAll
    public static void setup() {
        regionFinder = new RegionFinder(SHAPEFILE_PATH, SHAPEFILE_NAME);
    }

    @DisplayName("Should load all regions from shapefile successfully")
    @Test
    public void testGetAllRegions() {
        Map<String, Geometry> regions = regionFinder.getAllRegions();
        assertNotNull(regions);
        assertFalse(regions.isEmpty());
    }

    @Test
    @DisplayName("Should throw IOException when shapefile does not exist")
    void testLoadRegionsFromShapefile_FileNotFound() {
        RegionFinder regionFinder = new RegionFinder();

        String fakePath = "nonexistent/path/to/shapefile.shp";

        IOException exception = assertThrows(IOException.class, () -> {
            regionFinder.loadRegionsFromShapefile(fakePath);
        });

        assertTrue(exception.getMessage().contains("Shapefile not found"));
    }

    @Test
    @DisplayName("Should correctly identify region for known Auckland coordinates")
    public void testFindRegionForPoint() {
        // coordinates in Auckland
        String region = regionFinder.findRegionForPoint(-36.880933915772516, 174.7444827976006);
        assertNotNull(region);
        assertEquals("Auckland", region);
    }

    @Test
    @DisplayName("Should return 'Other' for coordinates near Null Island (0,0)")
    public void testFindRegionForNullIsland() {
        String region = regionFinder.findRegionForPoint(0, 0);
        assertNotNull(region);
        assertEquals("Other", region);
    }

    @Test
    @DisplayName("Should return 'Other' for ocean coordinates off New Zealand coast")
    public void testFindRegionForOceanCoordinates() {
        // coordinates in ocean off the coast of NZ
        String region = regionFinder.findRegionForPoint(-42.3102611205709, 169.3195787026245);
        assertNotNull(region);
        assertEquals("Other", region);
    }

    @Test
    @DisplayName("Should return 'Other' for invalid coordinates outside Earth bounds")
    public void testFindRegionForInvalidCoordinates() {
        String region = regionFinder.findRegionForPoint(10000, 10000);
        assertNotNull(region);
        assertEquals("Other", region);
    }

    @Test
    @DisplayName("Should return 'Otago' for coordinates on border between Otago and Canterbury")
    public void testFindRegionForBorderCoordinates() {
        // coordinates on the border between Otago and Canterbury
        String region = regionFinder.findRegionForPoint(-44.92984092565437, 171.10225477776783);
        assertNotNull(region);
        assertEquals("Otago", region);
    }

    @Test
    @DisplayName("Should return 'Canterbury' for coordinates just inside Canterbury border")
    public void testFindRegionForBorderCoordinates2() {
        // coordinates on the border between Otago and Canterbury (this time on
        // Canterbury side)
        String region = regionFinder.findRegionForPoint(-44.92683616374543, 171.11224219061646);
        assertNotNull(region);
        assertEquals("Canterbury", region);
    }

    @Test
    @DisplayName("Should find correct region for trail object with coordinates")
    public void testFindRegionForTrail() {
        Trail mockTrail = new Trail.Builder()
                .lat(-36.880933915772516)
                .lon(174.7444827976006)
                .build();

        String region = regionFinder.findRegionForTrail(mockTrail);

        assertNotNull(region);
        assertEquals("Auckland", region);
    }


    @Test
    @DisplayName("Should return 'Other' when finding region for null trail")
    public void testFindRegionForNullTrail() {
        String region = regionFinder.findRegionForTrail(null);
        assertNotNull(region);
        assertEquals("Other", region);
    }

    @Test
    @DisplayName("Should return correct DOC region ID for known regions")
    public void testGetDocRegionIdBasic() {
        assertEquals(3001000, regionFinder.getDocRegionId("Northland"));
        assertEquals(3012000, regionFinder.getDocRegionId("Wellington"));
        assertEquals(3009000, regionFinder.getDocRegionId("Manawatu-Whanganui"));
        assertNull(regionFinder.getDocRegionId("UnknownRegion"));
    }

    @Test
    @DisplayName("Should handle region names with special characters correctly")
    public void testGetDocRegionIdSpecialCharacters() {
        assertEquals(3009000, regionFinder.getDocRegionId("Manawatū-Whanganui"));
    }

    @Test
    @DisplayName("Should correctly identify remote hut regions")
    public void testIsRemoteHutRegion() {
        assertTrue(regionFinder.isRemoteHutRegion("West Coast"));
        assertTrue(regionFinder.isRemoteHutRegion("Tasman"));
        assertFalse(regionFinder.isRemoteHutRegion("Auckland"));
    }
}
