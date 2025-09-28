package seng202.team5.services;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import seng202.team5.models.Trail;

/**
 * Service class for loading regional shapefile data, and then doing stuff with
 * points using it
 */
public class RegionFinder {
    private static final String NAME_ATTRIBUTE = "REGC2025_1"; // field with region names

    private static String regionalDatasetsPath;
    private static String fileName;
    private static Map<String, Geometry> allRegions = new HashMap<>();
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public RegionFinder(String regionalDatasetsPath, String fileName) {
        RegionFinder.regionalDatasetsPath = regionalDatasetsPath;
        RegionFinder.fileName = fileName;

        try {
            allRegions = loadAllRegions();
        } catch (IOException e) {
            System.err.println("Failed to load regions in RegionFinder constructor: " + e.getMessage());
        }
    }

    public RegionFinder() {
        this("/regional_datasets", "NZ_Regions_2025.shp");
    }

    /**
     * Loads the shp file from the resources directory.
     *
     * @return Map of region names to their geometries
     * @throws IOException if there's an error reading the shapefile
     */
    public Map<String, Geometry> loadAllRegions() throws IOException {

        try {
            URL resourceUrl = getClass().getResource(regionalDatasetsPath);
            if (resourceUrl == null) {
                throw new IOException("Regional datasets directory not found in resources: " + regionalDatasetsPath);
            }

            Path regionalDir = Paths.get(resourceUrl.toURI());
            Path shpPath = regionalDir.resolve(fileName);

            if (!shpPath.toFile().exists()) {
                throw new IOException("Shapefile not found: " + shpPath.toString());
            }

            try {
                Map<String, Geometry> regions = loadRegionsFromShapefile(shpPath.toString());
                allRegions.putAll(regions);
            } catch (IOException e) {
                System.err.println("Failed to load regions from " + shpPath.getFileName() + ": " + e.getMessage());
                throw e;
            }

        } catch (Exception e) {
            throw new IOException("Failed to load regional datasets", e);
        }

        return allRegions;
    }

    /**
     * Loads regions from a specific shapefile path.
     *
     * @param shapeFilePath Path to the shapefile
     * @return Map of region names to their geometries
     * @throws IOException if there's an error reading the shapefile
     */
    public Map<String, Geometry> loadRegionsFromShapefile(String shapeFilePath) throws IOException {
        File file = new File(shapeFilePath);
        if (!file.exists()) {
            throw new IOException("Shapefile not found: " + shapeFilePath);
        }

        ShapefileDataStore dataStore = null;
        SimpleFeatureIterator iterator = null;

        try {
            dataStore = new ShapefileDataStore(file.toURI().toURL());
            SimpleFeatureSource featureSource = dataStore.getFeatureSource();
            SimpleFeatureCollection featureCollection = featureSource.getFeatures();

            CoordinateReferenceSystem sourceCRS = dataStore.getSchema().getCoordinateReferenceSystem();
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326"); // lat/long

            Map<String, Geometry> regions = new HashMap<>();
            iterator = featureCollection.features();

            // Prepare coordinate transformation if needed
            MathTransform transform = null;
            if (sourceCRS != null && !CRS.equalsIgnoreMetadata(sourceCRS, targetCRS)) {
                transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
            }

            // Iterate through features and extract each region
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();

                // Get the region name from the attribute table
                String regionName = getRegionName(feature, NAME_ATTRIBUTE);
                if (regionName == null || regionName.trim().isEmpty()) {
                    continue; // Skip regions with invalid names
                }

                // Get the geometry
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                if (geometry != null && transform != null) {
                    geometry = JTS.transform(geometry, transform);
                }

                // Store if valid
                if (geometry != null) {
                    regions.put(regionName, geometry);
                }
            }

            return regions;

        } catch (FactoryException | TransformException e) {
            throw new IOException("Error processing coordinate reference systems", e);
        } finally {
            if (iterator != null) {
                iterator.close();
            }
            if (dataStore != null) {
                dataStore.dispose();
            }
        }
    }

    /**
     * Extracts the region name from a feature using the specified attribute.
     *
     * @param feature       The feature to extract the name from
     * @param nameAttribute The attribute name containing the region name
     * @return The region name, or null if not found
     */
    private String getRegionName(SimpleFeature feature, String nameAttribute) {
        if (nameAttribute == null) {
            return null;
        }

        Object nameValue = feature.getAttribute(nameAttribute);
        return nameValue != null ? nameValue.toString().trim() : null;
    }

    /**
     * Finds the region for a given trail based on its coordinates.
     *
     * @param trail The trail to find the region for
     * @return The name of the region, or "Other" if not found
     */
    public String findRegionForTrail(Trail trail) {
        if (trail == null) {
            return "Other";
        }

        return findRegionForPoint(trail.getLat(), trail.getLon());
    }

    /**
     * Checks if a point is within any of the loaded regions.
     *
     * @param latitude  Latitude coordinate
     * @param longitude Longitude coordinate
     * @return Name of the region containing the point, or "Other" if not found
     */
    public String findRegionForPoint(double latitude, double longitude) {
        try {
            // Create point (lat, lon)
            Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));

            // First: try contains() and intersects()
            for (Map.Entry<String, Geometry> entry : allRegions.entrySet()) {
                String regionName = entry.getKey();
                Geometry regionGeometry = entry.getValue();

                if (regionGeometry != null && regionGeometry.contains(point)) {
                    return cleanRegionName(regionName);
                }
            }

            // Otherwise, return "Other"
            return "Other";
        } catch (Exception e) {
            System.err.println("Error checking point against regions: " + e.getMessage());
            e.printStackTrace();
            return "Other";
        }
    }

    /**
     * Cleans up region names by handling "Region" suffix and special cases
     *
     * @param regionName The raw region name from the shapefile
     * @return Cleaned region name
     */
    private String cleanRegionName(String regionName) {
        if (regionName.equals("Area Outside Region")) {
            return "Other";
        } else if (regionName.contains("ManawatÅ«-Whanganui")) {
            return "Manawatū-Whanganui";
        } else {
            return regionName.replace(" Region", "").trim();
        }
    }

    /**
     * Returns all loaded regions.
     *
     * @return Map of region names to their geometries
     */
    public Map<String, Geometry> getAllRegions() {
        return allRegions;
    }
}
