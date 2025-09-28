package seng202.team5.services;

import seng202.team5.models.Trail;
import seng202.team5.utils.GeoUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for performing operations related to Trail objects
 */
public class TrailService {

    /**
     * Returns a list of trails that are within a specified radius of the given trail
     * The current trail itself is excluded from the results
     * @param currentTrail the trail from which distances are measured
     * @param radiusKm the radius in kilometres within which to search for nearby trails
     * @param allTrails the complete list of trails to filter from
     * @return a list of nearby trails sorted by proximity to the current trail
     */
    public List<Trail> getNearbyTrails(Trail currentTrail, double radiusKm, List<Trail> allTrails) {
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
