package seng202.team5.services;

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

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

/**
 * Service class for loading regional shapefile data, and then doing stuff with
 * points using it
 */
public class RegionFinder {

    private static final String NAME_ATTRIBUTE = "REGC2025_1";
    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static final Map<String, Geometry> allRegions = new LinkedHashMap<>();
    private final String regionalDatasetsPath;
    private final String fileName;

    public RegionFinder() {
        this("/datasets/regional/", "regional-council-2025.shp");
    }

    /**
     * Constructs a new RegionFinder using a specified dataset path and shapefile name.
     * Automatically loads all region geometries into memory.
     *
     * @param regionalDatasetsPath the resource path to the dataset folder
     * @param fileName             the shapefile name
     */
    public RegionFinder(String regionalDatasetsPath, String fileName) {
        this.regionalDatasetsPath = regionalDatasetsPath;
        this.fileName = fileName;

        try {
            allRegions.clear();
            allRegions.putAll(loadAllRegions());
        } catch (IOException e) {
            System.err.println("Failed to load regions: " + e.getMessage());
        }
    }

    /**
     * Loads the shp file from the resources directory.
     *
     * @return Map of region names to their geometries
     * @throws IOException if there's an error reading the shapefile
     */
    private Map<String, Geometry> loadAllRegions() throws IOException {
        URL resourceUrl = getClass().getResource(regionalDatasetsPath + fileName);
        if (resourceUrl == null)
            throw new IOException("Regional shapefile not found: " + regionalDatasetsPath + fileName);

        File shapeFile;
        if (isJarResource(resourceUrl)) {
            shapeFile = extractShapefileResources(regionalDatasetsPath, fileName);
        } else {
            shapeFile = Paths.get(resourceUrl.getPath()).toFile();
        }

        return loadRegionsFromShapefile(shapeFile.getAbsolutePath());
    }

    /**
     * Determines whether a resource is located inside a JAR file.
     *
     * @param url the URL of the resource
     * @return {@code true} if the resource is inside a JAR, {@code false} otherwise
     */
    private boolean isJarResource(URL url) {
        return url.toString().startsWith("jar:");
    }

    /**
     * Extracts all shapefile components (.shp, .dbf, .shx, .prj) from the JAR into a temporary directory.
     * This allows GeoTools to read the shapefile even when bundled within a JAR.
     *
     * @param basePath    the base path of the shapefile within the resources
     * @param shpFileName the name of the shapefile (e.g. {@code regional-council-2025.shp})
     * @return a {@link File} object referencing the extracted .shp file
     * @throws IOException if an extraction error occurs
     */
    private File extractShapefileResources(String basePath, String shpFileName) throws IOException {
        String baseName = shpFileName.substring(0, shpFileName.lastIndexOf('.'));
        String[] exts = {".shp", ".dbf", ".shx", ".prj"};
        Path tempDir = Files.createTempDirectory("regions_");
        tempDir.toFile().deleteOnExit();

        for (String ext : exts) {
            String resourcePath = basePath + baseName + ext;
            try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
                if (in == null) {
                    System.err.println("Missing shapefile component: " + resourcePath);
                    continue;
                }
                Path out = tempDir.resolve(baseName + ext);
                Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
                out.toFile().deleteOnExit();
            }
        }

        return tempDir.resolve(baseName + ".shp").toFile();
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
        if (!file.exists()) throw new IOException("Shapefile not found: " + shapeFilePath);

        ShapefileDataStore dataStore = null;
        SimpleFeatureIterator iterator = null;
        Map<String, Geometry> regions = new LinkedHashMap<>();

        try {
            dataStore = new ShapefileDataStore(file.toURI().toURL());
            SimpleFeatureSource featureSource = dataStore.getFeatureSource();
            SimpleFeatureCollection featureCollection = featureSource.getFeatures();

            CoordinateReferenceSystem sourceCRS = dataStore.getSchema().getCoordinateReferenceSystem();
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);

            iterator = featureCollection.features();
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                String regionName = getRegionName(feature);
                if (regionName == null || regionName.isBlank()) continue;

                Geometry geom = (Geometry) feature.getDefaultGeometry();
                if (geom != null) geom = JTS.transform(geom, transform);
                regions.put(regionName, geom);
            }
        } catch (FactoryException | TransformException e) {
            throw new IOException("Coordinate transform error", e);
        } finally {
            if (iterator != null) iterator.close();
            if (dataStore != null) dataStore.dispose();
        }

        return regions;
    }

    /**
     * Extracts the region name from a feature using the specified attribute.
     *
     * @param feature The feature to extract the name from
     * @return The region name, or null if not found
     */
    private String getRegionName(SimpleFeature feature) {
        Object val = feature.getAttribute(NAME_ATTRIBUTE);
        return val != null ? val.toString().trim() : null;
    }

    /**
     * Finds the region for a given trail based on its coordinates.
     *
     * @param trail The trail to find the region for
     * @return The name of the region, or "Other" if not found
     */
    public String findRegionForTrail(Trail trail) {
        if (trail == null) return "Other";
        return findRegionForPoint(trail.getLat(), trail.getLon());
    }

    /**
     * Checks if a point is within any of the loaded regions.
     *
     * @param lat  Latitude coordinate
     * @param lon Longitude coordinate
     * @return Name of the region containing the point, or "Other" if not found
     */

    public String findRegionForPoint(double lat, double lon) {
        Point point = geometryFactory.createPoint(new Coordinate(lat, lon));
        for (Map.Entry<String, Geometry> e : allRegions.entrySet()) {
            if (e.getValue() != null && e.getValue().contains(point))
                return cleanRegionName(e.getKey());
        }
        return "Other";
    }

    /**
     * Cleans up region names by handling "Region" suffix and special cases
     *
     * @param name The raw region name from the shapefile
     * @return Cleaned region name
     */
    private String cleanRegionName(String name) {
        if (name.equals("Area Outside Region")) return "Other";
        if (name.contains("Manawat")) return "Manawatū-Whanganui";
        return name.replace(" Region", "").trim();
    }

    /**
     * Returns all loaded regions.
     *
     * @return Map of region names to their geometries
     */
    public Map<String, Geometry> getAllRegions() {
        return allRegions;
    }

    /**
     * Returns simple list of all region names (now in order yippee)
     *
     * @return List of region names
     */
    public List<String> getRegionNames() {
        List<String> list = new ArrayList<>();
        for (String n : allRegions.keySet()) {
            String cleaned = cleanRegionName(n);
            if (!list.contains(cleaned)) list.add(cleaned);
        }
        if (!list.contains("Other")) list.add("Other");
        return list;
    }

    /**
     * Checks if a region is a remote hut region.
     *
     * @param region The region to check
     * @return True if the region is a remote hut region, false otherwise
     */
    public boolean isRemoteHutRegion(String region) {
        return region.equalsIgnoreCase("West Coast")
                || region.equalsIgnoreCase("Tasman")
                || region.equalsIgnoreCase("Canterbury");
    }

    /**
     * Gets the doc region id
     *
     * @param regionName The region to get the doc id for
     * @return The doc region id or null if not found
     */
    public Integer getDocRegionId(String regionName) {
        if (regionName == null) return null;
        return switch (regionName.toLowerCase()) {
            case "northland" -> 3001000;
            case "auckland" -> 3002000;
            case "waikato" -> 3004000;
            case "bay of plenty" -> 3005000;
            case "gisborne" -> 3006000;
            case "taranaki" -> 3008000;
            case "manawat?-whanganui", "manawatu-whanganui", "manawatu", "whanganui", "manawatū-whanganui" -> 3009000;
            case "hawke's bay" -> 3010000;
            case "wellington" -> 3012000;
            case "chatham islands" -> 3013000;
            case "nelson", "tasman" -> 3014000;
            case "marlborough" -> 3015000;
            case "west coast" -> 3016000;
            case "canterbury" -> 3017000;
            case "otago" -> 3018000;
            case "southland" -> 3020000;
            default -> null;
        };
    }
}
