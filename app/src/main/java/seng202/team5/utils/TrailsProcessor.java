package seng202.team5.utils;

import seng202.team5.models.Trail;

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
        for (Trail trail : trails) {
            String completionInfo = trail.getCompletionInfo();

            if (completionInfo != null && !completionInfo.trim().isEmpty()) {
                try {
                    CompletionTimeParser.CompletionTimeResult result = CompletionTimeParser
                            .parseCompletionTime(completionInfo);

                    // Update trail with parsed time information
                    if (Objects.equals(trail.getCompletionType(), "unknown") ||
                        (!Objects.equals(trail.getCompletionType(), result.getCompletionType())) &&
                        !Objects.equals(result.getCompletionType(), "unknown")) {
                        trail.setCompletionType(result.getCompletionType());
                    }
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
        }
        return trails;
    }
}
