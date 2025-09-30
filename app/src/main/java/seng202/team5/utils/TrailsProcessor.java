package seng202.team5.utils;

import seng202.team5.models.Trail;
import seng202.team5.services.RegionFinder;

import java.util.List;
import java.util.Objects;

public class TrailsProcessor {
    /**
     * Processes trails to populate time-related fields
     *
     * @param trails List of trails to process
     * @return List of processed trails
     */
    public static List<Trail> processTrails(List<Trail> trails) {
        RegionFinder regionFinder = new RegionFinder("/datasets/regional/", "regional-council-2025.shp");
        for (Trail trail : trails) {
            String completionInfo = trail.getCompletionInfo();

            if (completionInfo != null && !completionInfo.trim().isEmpty()) {
                try {
                    CompletionTimeParser.CompletionTimeResult result = CompletionTimeParser
                            .parseCompletionTime(completionInfo);

                    // Only use type returned by parser if current type of trail is unknown
                    // Prioritise entry from drop down over parser
                    if (Objects.equals(trail.getCompletionType(), "unknown")) {
                        trail.setCompletionType(result.getCompletionType());
                    }
                    // Update trail with parsed time information
                    trail.setMinCompletionTimeMinutes(result.getMinCompletionTimeMinutes());
                    trail.setMaxCompletionTimeMinutes(result.getMaxCompletionTimeMinutes());
                    trail.setTimeUnit(result.getTimeUnit());
                    trail.setMultiDay(result.isMultiDay());
                    trail.setHasVariableTime(result.hasVariableTime());

                } catch (Exception e) {
                    System.err.println("Error parsing completion time for trail " + trail.getId() +
                            " ('" + completionInfo + "'): " + e.getMessage());
                    // Keep default values if parsing fails
                }
            }

            trail.setDifficulty(DifficultyParser.parseDifficulty(trail.getDifficulty()));
            trail.setRegion(regionFinder.findRegionForTrail(trail));
        }
        return trails;
    }
}
