package seng202.team5.data;

import java.util.List;
import java.util.Map;

/**
 * Interface for accessing the keyword data
 */
public interface IKeyword {
    /**
     * Returns keywords grouped by category from the data source
     *
     * @return Map of category names to lists of keywords in that category
     */
    Map<String, List<String>> getKeywords();
}
