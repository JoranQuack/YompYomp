package seng202.team5.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

public class DashboardService {

    private final SqlBasedTrailRepo trailRepo;

    public DashboardService(SqlBasedTrailRepo trailRepo) {
        this.trailRepo = trailRepo;
    }

    /**
     * Get recommended trails (8 of them)
     *
     * @return list of recommended trails
     */
    public List<Trail> getRecommendedTrails() {
        List<Trail> trails = trailRepo.getAllTrails();
        return trails.stream()
                .sorted(Comparator.comparing(Trail::getUserWeight).reversed())
                .limit(8)
                .collect(Collectors.toList());
    }

    /**
     * Get recommended trails for a specific list of regions (8 of them)
     *
     * @param regions list of regions to get popular trails from
     * @return list of popular trails
     */
    public List<Trail> getPopularTrailsByRegions(List<String> regions) {
        List<Trail> trails = trailRepo.getAllTrails();
        return trails.stream()
                .filter(trail -> regions.contains(trail.getRegion()))
                .sorted(Comparator.comparing(Trail::getUserWeight).reversed())
                .limit(8)
                .collect(Collectors.toList());
    }

    /**
     * Get random trails (8 of them)
     *
     * @return list of random trails
     */
    public List<Trail> getRandomTrails() {
        List<Trail> allTrails = trailRepo.getAllTrails();
        Collections.shuffle(allTrails);
        return allTrails.stream().limit(8).collect(Collectors.toList());
    }
}
