package seng202.team5.data;

import java.io.*;
import java.util.*;

/**
 * Loads data from the keyword CSV file.
 * It stores the keywords in a map of category to a list of keywords.
 */
public class FileBasedKeywordRepo implements IKeyword {
    private final Map<String, List<String>> keywords = new HashMap<>();

    /**
     * Constructor - loads keywords from the CSV file for keywords
     * @param filePath path to the keywords CSV file
     */
    public FileBasedKeywordRepo(String filePath) {
        loadKeywordsFromCSV(filePath);
    }

    private Map<String, List<String>> loadKeywordsFromCSV(String filePath) {
        try (InputStream inputstream = getClass().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream))) {

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
        } catch (IOException e) {
            throw new RuntimeException("Error loading keywords CSV", e);
        }
        return keywords;
    }

    @Override
    public Map<String, List<String>> getKeywords() {
        return keywords;
    }
}
