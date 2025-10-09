package seng202.team5.utils;

import seng202.team5.models.Trail;
import seng202.team5.services.RegionFinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TrailsProcessor {
    /**
     * Processes trails to populate time-related fields
     *
     * @param trails List of trails to process
     * @return List of processed trails
     */
    public static List<Trail> processTrails(List<Trail> trails) {
        RegionFinder regionFinder = new RegionFinder("/datasets/regional/", "regional-council-2025.shp");
        List<Trail> processedTrails = new ArrayList<>();

        for (Trail trail : trails) {
            String completionInfo = trail.getCompletionInfo();

            int minTime = trail.getMinCompletionTimeMinutes();
            int maxTime = trail.getMaxCompletionTimeMinutes();
            String completionType = trail.getCompletionType();
            String timeUnit = trail.getTimeUnit();
            boolean isMultiDay = trail.isMultiDay();
            boolean hasVariableTime = trail.hasVariableTime();

            if (completionInfo != null && !completionInfo.trim().isEmpty()) {
                try {
                    CompletionTimeParser.CompletionTimeResult result = CompletionTimeParser
                            .parseCompletionTime(completionInfo);

                    if (Objects.equals(trail.getCompletionType(), "unknown")) {
                        completionType = result.getCompletionType();
                    }
                    minTime = result.getMinCompletionTimeMinutes();
                    maxTime = result.getMaxCompletionTimeMinutes();
                    timeUnit = result.getTimeUnit();
                    isMultiDay = result.isMultiDay();
                    hasVariableTime = result.hasVariableTime();

                } catch (Exception e) {
                    System.err.println("Error parsing completion time for trail " + trail.getId() +
                            " ('" + completionInfo + "'): " + e.getMessage());
                }
            }

            processedTrails.add(
                    new Trail.Builder()
                            .from(trail) // copy existing fields
                            .minCompletionTimeMinutes(minTime)
                            .maxCompletionTimeMinutes(maxTime)
                            .completionType(completionType)
                            .timeUnit(timeUnit)
                            .isMultiDay(isMultiDay)
                            .hasVariableTime(hasVariableTime)
                            .difficulty(DifficultyParser.parseDifficulty(trail.getDifficulty()))
                            .region(regionFinder.findRegionForTrail(trail))
                            .build()
            );
        }

        return processedTrails;
    }

    /**
     * Returns a list of trails that are within a specified radius of the given trail
     * The current trail itself is excluded from the results
     * @param currentTrail the trail from which distances are measured
     * @param radiusKm the radius in kilometres within which to search for nearby trails
     * @param allTrails the complete list of trails to filter from
     * @return a list of nearby trails sorted by proximity to the current trail
     */
    public static List<Trail> getNearbyTrails(Trail currentTrail, double radiusKm, List<Trail> allTrails) {
        return allTrails.stream()
                .filter(trail -> !trail.equals(currentTrail)) // exclude the current trail
                .filter(trail -> GeoUtils.distanceKm(currentTrail.getLat(), currentTrail.getLon(),
                        trail.getLat(), trail.getLon()) <= radiusKm)
                .sorted(Comparator.comparingDouble(trail ->
                        GeoUtils.distanceKm(currentTrail.getLat(), currentTrail.getLon(),
                                trail.getLat(), trail.getLon())))
                .collect(Collectors.toList());
    }
}
