package seng202.team5.data;

import java.io.*;
import java.util.*;

/**
 * Loads data from the keyword CSV file.
 * It stores the keywords in a map of category to a list of keywords.
 */
public class FileBasedKeywordRepo {
    // This is what this CSV reader will return. It is in the form
    // - Map of categories (ONE) : List of keywords (at least one)
    private final Map<String, List<String>> keywords = new HashMap<>();

    /**
     * Constructor - loads keywords from the CSV file for keywords.
     *
     * @param filePath path to the keywords CSV file
     */
    public FileBasedKeywordRepo(String filePath) {
        loadKeywordsFromCSV(filePath);
    }

    /**
     * Reads the categories and all the keywords for that category in the created
     * CSV.
     * The CSV has one category per line at index 0, and all the keywords for that
     * category
     * follow in a comma-separated list.
     *
     * @param filePath The path to the CSV file
     * @return A map of category names to lists of keywords in that category
     */
    private Map<String, List<String>> loadKeywordsFromCSV(String filePath) {
        // Using the filePath, open the CSV as a stream (in bytes) to read its contents.
        try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
            assert inputStream != null;
            try (// InputStreamReader converts the stream to a character stream, and
                 // BufferedReader allows efficient line-by-line reading
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length > 1) {
                        String category = values[0].trim();
                        List<String> keywordList = new ArrayList<>();
                        for (int i = 1; i < values.length; i++) {
                            keywordList.add(values[i].trim());
                        }
                        keywords.put(category, keywordList);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading keywords CSV", e);
        }
        return keywords;
    }

    /**
     * Gets the map of categories to keywords.
     *
     * @return The map of categories to keywords
     */
    public Map<String, List<String>> getKeywords() {
        return keywords;
    }
}
