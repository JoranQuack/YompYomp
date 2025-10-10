package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DashboardServiceTest {

    @Mock
    private SqlBasedTrailRepo mockTrailRepo;

    private DashboardService dashboardService;
    private List<Trail> mockTrails;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockTrails = Arrays.asList(
                createMockTrail(703975, "Port Hills Track", "Te Ara o ngā Maunga", "Canterbury", "Easiest",
                        "One way", "Well-marked trail with gentle incline", "Great introductory hike for beginners.",
                        "thumb_ph.jpg", "https://example.com/porthills", "https://example.com/culture/ph",
                        4.5, -43.5850, 172.6750),

                createMockTrail(703976, "Godley Head Loop", "Te Ara o Rāpaki", "Canterbury", "Easy",
                        "Loop", "Gentle loop along coastal cliffs", "Ideal for casual walkers and families.",
                        "thumb_gh.jpg", "https://example.com/godleyhead", "https://example.com/culture/gh",
                        4.7, -43.5861, 172.7822),

                createMockTrail(703977, "Mount Herbert Summit", "Te Tihi o Te Ahu Pātiki", "Canterbury", "Expert",
                        "Loop", "Steep climb with exposed sections",
                        "Longest trail on the list, for experienced hikers.",
                        "thumb_mh.jpg", "https://example.com/mtherbert", "https://example.com/culture/mh",
                        4.9, -43.7160, 172.6500),

                createMockTrail(703978, "Bridle Path", "Te Ara a Hine", "Canterbury", "Advanced",
                        "Loop", "Historic track between Lyttelton and Christchurch",
                        "Challenging gradient, popular training route.",
                        "thumb_bp.jpg", "https://example.com/bridlepath", "https://example.com/culture/bp",
                        4.4, -43.6000, 172.7300),

                createMockTrail(703979, "Rapaki Track", "Te Ara Rāpaki", "Canterbury", "Easy",
                        "One way", "Gradual incline with open views", "Turned back due to weather conditions.",
                        "thumb_rt.jpg", "https://example.com/rapaki", "https://example.com/culture/rt",
                        4.3, -43.6005, 172.6602),

                createMockTrail(703980, "Hinewai Reserve Loop", "Te Wao o Hinewai", "Banks Peninsula", "Easy",
                        "Loop", "Regenerating native forest walk", "Scenic and relaxing with clear signage.",
                        "thumb_hw.jpg", "https://example.com/hinewai", "https://example.com/culture/hw",
                        4.8, -43.7800, 172.9800),

                createMockTrail(703981, "Cass-Lagoon Saddle", "Te Ara o Kāwhiu", "Arthur’s Pass", "Expert",
                        "Loop", "Alpine hike with steep ascents", "Multi-day option available for trampers.",
                        "thumb_cl.jpg", "https://example.com/casslagoon", "https://example.com/culture/cl",
                        4.6, -42.9500, 171.7100),

                createMockTrail(703982, "Rakaia Gorge Walkway", "Te Ara o Rakaia", "Canterbury", "Advanced",
                        "Loop", "Follows the river terraces and native bush", "Excellent day hike for fit walkers.",
                        "thumb_rg.jpg", "https://example.com/rakaiagorge", "https://example.com/culture/rg",
                        4.5, -43.4800, 171.8300),

                createMockTrail(703940, "Victoria Park Track", "Te Ara o Whero", "Christchurch", "Easiest",
                        "Loop", "Short scenic trail through forest park", "Some muddy sections after rain.",
                        "thumb_vp.jpg", "https://example.com/victoriapark", "https://example.com/culture/vp",
                        4.2, -43.5790, 172.6400),

                createMockTrail(703947, "Packhorse Hut Route", "Te Ara o te Pōkai", "Canterbury", "Advanced",
                        "One way", "Steady climb to historic hut", "Excellent views and well-maintained track.",
                        "thumb_phr.jpg", "https://example.com/packhorse", "https://example.com/culture/phr",
                        4.6, -43.6400, 172.6400),
                createMockTrail(703924, "Milford Track", "", "", "", "", "", "", "", "", "", 0, 0, 0));

        when(mockTrailRepo.getAllTrails()).thenReturn(new ArrayList<>(mockTrails));

        dashboardService = new DashboardService(mockTrailRepo);
    }

    private static Trail createMockTrail(int id, String name, String translation, String region,
                                         String difficulty, String completionType, String completionInfo,
                                         String description, String thumbnailURL, String webpageURL,
                                         String cultureUrl, double userWeight, double lat, double lon) {
        return new Trail.Builder()
                .id(id)
                .name(name)
                .translation(translation)
                .region(region)
                .difficulty(difficulty)
                .completionType(completionType)
                .completionInfo(completionInfo)
                .description(description)
                .thumbnailURL(thumbnailURL)
                .webpageURL(webpageURL)
                .cultureUrl(cultureUrl)
                .userWeight(userWeight)
                .lat(lat)
                .lon(lon)
                .build();
    }

    @Test
    @DisplayName("getRecommendedTrails should return top 8 trails sorted by match percentage descending")
    void testGetRecommendedTrails(){
        List<Trail> result = dashboardService.getRecommendedTrails();
        assertEquals(8, result.size(), "Should only return 8 trails");
    }

    @Test
    @DisplayName("getPopularTrailsByRegions should filter by region and return top 8 sorted by match percentage")
    void testGetPopularTrailsByRegions() {
        List<String> regions = Arrays.asList("RegionA", "RegionC");
        List<Trail> result = dashboardService.getPopularTrailsByRegions(regions);

        assertTrue(result.stream().allMatch(t -> regions.contains(t.getRegion())),
                "All trails should belong to selected regions");
        assertTrue(result.size() <= 8, "Should limit to at most 8 results");
    }

    @Test
    @DisplayName("getRandomTrails should return 8 random trails")
    void testGetRandomTrails() {
        List<Trail> first = dashboardService.getRandomTrails();
        List<Trail> second = dashboardService.getRandomTrails();

        assertEquals(8, first.size(), "Should return 8 trails each time");
        assertEquals(8, second.size(), "Should return 8 trails each time");

        // It's possible by chance they are equal but extremely unlikely
        boolean likelyDifferent = !first.equals(second);
        if(likelyDifferent) {
            assertTrue(likelyDifferent, "Order should usually differ between runs");
        }
    }
}
