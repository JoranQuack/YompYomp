package seng202.team5.data;

import seng202.team5.models.Trail;

//import java.io.BufferedReader;
import com.opencsv.CSVReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.InputStreamReader;
import java.io.InputStream;

/**
 * FileBasedTrailRepo is responsible for loading the trail data from the DOC
 * trail CSV
 * It stores the trails in a list as trail object
 */
public class FileBasedTrailRepo implements ITrail {

    // List containing all the trails from the CSV
    private final List<Trail> trails = new ArrayList<>();

    /**
     * Constructor - Loads trails from the DOC CSV file path
     *
     * @param csvFilePath Path to the CSV file
     */
    public FileBasedTrailRepo(String csvFilePath) {
        loadTrailsFromCSV(csvFilePath);
    }

    /**
     * Reads the trail data from the DOC CSV file, it then creates a list of trail
     * objects
     * Each line in the CSV is a new trail that is expected to follow a specific
     * format
     *
     * @param filePath Path to the CSV file
     * @return List of trail objects retrieved from the CSV
     */
    private List<Trail> loadTrailsFromCSV(String filePath) {
        // Get the resource as an input stream from the classpath
        InputStream inputStream = getClass().getResourceAsStream(filePath);
        if (inputStream == null) {
            System.err.println("Could not find resource: " + filePath);
            return trails;
        }

        // define and try the OpenCSV reader with InputStreamReader
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            // list of values from each line of CSV
            String[] values;
            boolean firstLine = true;
            // check if there are still rows to come
            while ((values = reader.readNext()) != null) {
                // check that the line isn't a header line
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                // checks that the CSV line is the right length, otherwise skips to ensure
                // it doesn't break
                if (values.length != 10) {
                    System.err.println("Invalid CSV line: " + Arrays.toString(values));
                    continue;
                }

                // Parse each field from the current line
                int id = Integer.parseInt(values[0]);
                String name = values[1];
                String description = values[2];
                String difficulty = values[3];
                String completionTime = values[4];
                // String hasAlerts = values[5];
                String type = null;
                String thumbnailURL = values[6];
                String webpageURL = values[7];
                String dateLoaded = values[8];
                // double shapeLength = Double.parseDouble(values[9]);
                double x = 0;
                double y = 0;

                // create the new trail object
                Trail newTrail = new Trail(id, name, difficulty, description, completionTime, type,
                        thumbnailURL, webpageURL, dateLoaded, x, y);

                // add the new trail object to the list
                trails.add(newTrail);
            }
        } catch (Exception e) {
            System.out.println("Error with file: " + filePath + ": " + e.getMessage());
        }
        return trails;
    }

    /**
     * Returns the list of all the trail objects
     *
     * @return list of trail objects
     */
    @Override
    public List<Trail> getAllTrails() {
        return trails;
    }

    public java.util.Optional<Trail> findById(int id) {
        return null;
    }

    public void upsert(Trail trail) {
    }

    public void upsertAll(List<Trail> trails) {
    }

    public void deleteById(int id) {
    }
}
