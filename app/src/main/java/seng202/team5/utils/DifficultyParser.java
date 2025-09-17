package seng202.team5.utils;

/**
 * Utility class for parsing difficulty levels
 */
public class DifficultyParser {

    /**
     * Parses difficulty strings to a consistent format.
     *
     * @param difficulty The input difficulty string
     * @return Standardised difficulty string
     */
    public static String parseDifficulty(String difficulty) {
        if (difficulty == null || difficulty.trim().isEmpty()
                || difficulty.equals("[]")) {
            return "unknown";
        } else if (difficulty.contains(",")) {
            String[] difficultyParts = difficulty.split(",");
            return difficultyParts[difficultyParts.length - 1].trim().toLowerCase();
        } else {
            return difficulty.trim().toLowerCase();
        }
    }
}
