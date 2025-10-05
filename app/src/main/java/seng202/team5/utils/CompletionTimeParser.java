package seng202.team5.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing completion time strings from DOC trail data.
 * Handles various time formats including ranges, completion types, and
 * multi-day trips.
 *
 * The big goal of this class is to take DOC's awful completion time strings and
 * turn them into consistent data we can use for our app! Also being able to
 * translate back to human readable strings from what we will store in the Trail
 * objects, but again, in a consistent way.
 */
public class CompletionTimeParser {

    /**
     * Result class containing all parsed time information to make it easier to
     * store everything in one place as we process stuff
     */
    public static class CompletionTimeResult {
        private final int minCompletionTimeMinutes;
        private final int maxCompletionTimeMinutes;
        private final String completionType;
        private final String timeUnit;
        private final boolean isMultiDay;
        private final boolean hasVariableTime;

        public CompletionTimeResult(int minCompletionTimeMinutes, int maxCompletionTimeMinutes,
                String completionType, String timeUnit, boolean isMultiDay,
                boolean hasVariableTime) {
            this.minCompletionTimeMinutes = minCompletionTimeMinutes;
            this.maxCompletionTimeMinutes = maxCompletionTimeMinutes;
            this.completionType = completionType;
            this.timeUnit = timeUnit;
            this.isMultiDay = isMultiDay;
            this.hasVariableTime = hasVariableTime;
        }

        // Getters
        public int getMinCompletionTimeMinutes() {
            return minCompletionTimeMinutes;
        }

        public int getMaxCompletionTimeMinutes() {
            return maxCompletionTimeMinutes;
        }

        public String getCompletionType() {
            return completionType;
        }

        public String getTimeUnit() {
            return timeUnit;
        }

        public boolean isMultiDay() {
            return isMultiDay;
        }

        public boolean hasVariableTime() {
            return hasVariableTime;
        }
    }

    // Regex patterns for different time formats
    // these were created with help from ChatGPT and regex-generator.olafneumann.org
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "(\\d+(?:\\.\\d+)?)" + // Hours/minutes number
                    "\\s*" + // Optional space
                    "(hr?s?|hour?s?|min?s?|minutes?|day?s?)" + // Time unit
                    "(?:\\s+(\\d+)\\s*min(?:ute)?s?)?" // Optional additional minutes (e.g., "2 hr 30 min")
    );

    private static final Pattern RANGE_PATTERN = Pattern.compile(
            "(\\d+(?:\\.\\d+)?)" + // First number
                    "\\s*[-–—]\\s*" + // Range separator (hyphen, en-dash, em-dash)
                    "(\\d+(?:\\.\\d+)?)" + // Second number
                    "\\s*" + // Optional space
                    "(hr?s?|hour?s?|min?s?|minutes?|day?s?)" // Time unit
    );

    // Completion type patterns
    private static final Pattern COMPLETION_TYPE_PATTERN = Pattern.compile(
            "\\b(one way|return|loop|circuit|each way)\\b", Pattern.CASE_INSENSITIVE);

    // Variable time patterns
    private static final Pattern VARIABLE_TIME_PATTERN = Pattern.compile(
            "\\b(at your leisure|various options|variable|depends)\\b", Pattern.CASE_INSENSITIVE);

    /**
     * Parses a completion time string and extracts structured time information
     *
     * @param completionInfo The raw, annoying completion time string from CSV
     * @return CompletionTimeResult with parsed information
     */
    public static CompletionTimeResult parseCompletionTime(String completionInfo) {
        if (completionInfo == null || completionInfo.trim().isEmpty()) {
            return createDefaultResult();
        }
        String input = completionInfo.trim().toLowerCase();

        // Check for variable time first
        boolean hasVariableTime = VARIABLE_TIME_PATTERN.matcher(input).find();
        if (hasVariableTime) {
            return new CompletionTimeResult(0, 0, extractCompletionType(input),
                    "variable", false, true);
        }

        // Extract completion type
        String completionType = extractCompletionType(input);

        Matcher rangeMatcher = RANGE_PATTERN.matcher(input);
        if (rangeMatcher.find()) {
            return parseRange(rangeMatcher, completionType);
        }

        Matcher timeMatcher = TIME_PATTERN.matcher(input);
        if (timeMatcher.find()) {
            return parseSingleTime(timeMatcher, completionType, input);
        }

        // Handle special cases like "2+ days" (simplifying it to 2-3 days for now)
        if (input.contains("days") && input.contains("+")) {
            return new CompletionTimeResult(2880, 4320, completionType, "days", true, true); // 2-3 days
        }

        // may need to add more special cases as needed throughout testing!

        // fallback to our default if somehow nothing matches
        return createDefaultResult();
    }

    /**
     * Parses a range time pattern ("2-3 hr", "15-30 min")
     */
    private static CompletionTimeResult parseRange(Matcher matcher, String completionType) {
        double minValue = Double.parseDouble(matcher.group(1));
        double maxValue = Double.parseDouble(matcher.group(2));
        String unit = matcher.group(3);

        int minMinutes = convertToMinutes(minValue, unit);
        int maxMinutes = convertToMinutes(maxValue, unit);

        boolean isMultiDay = unit.startsWith("day");
        String timeUnit = isMultiDay ? "days" : (minMinutes >= 60 ? "hours" : "minutes");

        return new CompletionTimeResult(minMinutes, maxMinutes, completionType,
                timeUnit, isMultiDay, false);
    }

    /**
     * Parses a single time pattern (e.g., "2 hr 30 min", "90 min")
     */
    private static CompletionTimeResult parseSingleTime(Matcher matcher, String completionType, String input) {
        double value = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2);
        String additionalMinutes = matcher.group(3);

        int totalMinutes = convertToMinutes(value, unit);

        // Add more minutes if needed, like when we have "2 hr 30 min"
        if (additionalMinutes != null && !additionalMinutes.isEmpty()) {
            totalMinutes += Integer.parseInt(additionalMinutes);
        }

        boolean isMultiDay = unit.startsWith("day");
        String timeUnit = isMultiDay ? "days" : (totalMinutes >= 60 ? "hours" : "minutes");

        boolean hasMultipleOptions = input.contains("|") || input.contains(",");

        return new CompletionTimeResult(totalMinutes, totalMinutes, completionType,
                timeUnit, isMultiDay, hasMultipleOptions);
    }

    /**
     * Extracts completion type from the input string
     */
    private static String extractCompletionType(String input) {
        Matcher matcher = COMPLETION_TYPE_PATTERN.matcher(input);
        if (matcher.find()) {
            String type = matcher.group(1).toLowerCase();
            // Make things consistent (thanks DOC)
            switch (type) {
                case "each way":
                    return "one way";
                case "circuit":
                    return "loop";
                default:
                    return type;
            }
        }
        return "unknown";
    }

    /**
     * Converts time value to minutes based on unit
     */
    private static int convertToMinutes(double value, String unit) {
        unit = unit.toLowerCase();
        if (unit.startsWith("day")) {
            return (int) (value * 24 * 60); // days -> minutes
        } else if (unit.startsWith("hr") || unit.startsWith("hour")) {
            return (int) (value * 60); // hours -> minutes
        } else {
            return (int) value;
        }
    }

    /**
     * Converts a time value in minutes to a structured value-unit pair.
     * This is used for prefilling input fields, not for display.
     */
    public static TimeValue convertFromMinutes(int minutes) {
        if (minutes % (24 * 60) == 0) {
            return new TimeValue(minutes / (24 * 60.0), "days");
        } else if (minutes % 60 == 0) {
            return new TimeValue(minutes / 60.0, "hours");
        } else {
            return new TimeValue(minutes, "minutes");
        }
    }

    /**
     * Creates a default result for unparseable strings
     */
    private static CompletionTimeResult createDefaultResult() {
        return new CompletionTimeResult(0, 0, "unknown", "unknown", false, false);
    }

    /**
     * Formats time in minutes back to a readable string
     *
     * @param minutes Time in minutes
     * @return Formatted string ("2 hr 30 min", "45 min", "2 days")
     */
    public static String formatMinutesToString(int minutes) {
        if (minutes == 0) {
            return "Unknown";
        }

        if (minutes >= 1440) { // 24+ hours, therefore days
            int days = minutes / 1440;
            int remainingMinutes = minutes % 1440;
            if (remainingMinutes == 0) {
                return days + (days == 1 ? " day" : " days");
            } else {
                int hours = remainingMinutes / 60;
                return days + (days == 1 ? " day " : " days ") + hours + (hours == 1 ? " hr" : " hrs");
            }
        } else if (minutes >= 60) { // 1+ hours so we need hours
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            if (remainingMinutes == 0) {
                return hours + (hours == 1 ? " hr" : " hrs");
            } else {
                return hours + (hours == 1 ? " hr " : " hrs ") + remainingMinutes + " min";
            }
        } else {
            return minutes + " min";
        }
    }

    /**
     * Formats a time range to a readable string
     */
    public static String formatTimeRange(int minMinutes, int maxMinutes) {
        if (minMinutes == maxMinutes) {
            return formatMinutesToString(minMinutes);
        } else {
            return formatMinutesToString(minMinutes) + " - " + formatMinutesToString(maxMinutes);
        }
    }

    /**
     * Helper class for storing time values and units together.
     * @param value
     * @param unit
     */
    public record TimeValue(double value, String unit) {
    }
}