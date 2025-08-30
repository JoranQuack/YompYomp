package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.data.IKeyword;
import seng202.team5.models.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchMakingServiceTest {

    private MatchMakingService matchMakingService;

    @BeforeEach
    void setUp() {
        IKeyword fakeKeywordRepo = () -> {
            Map<String, List<String>> map = new HashMap<>();
            // Each keyword points to its category
            map.put("lake", Collections.singletonList("Wet"));
            map.put("forest", Collections.singletonList("Forest"));
            map.put("mountain", Collections.singletonList("Alpine"));
            map.put("historic-site", Collections.singletonList("Historical"));
            return map;
        };

        matchMakingService = new MatchMakingService(fakeKeywordRepo);
    }

    private User makeTestUser() {
        User user = new User("profile");
        user.setIsFamilyFriendly(true);
        user.setIsAccessible(false);
        user.setExperienceLevel(3);
        user.setGradientPreference(2);
        user.setBushPreference(4);
        user.setReservePreference(1);
        user.setLakeRiverPreference(5);
        user.setCoastPreference(0);
        user.setMountainPreference(4);
        user.setWildlifePreference(2);
        user.setHistoricPreference(3);
        user.setWaterfallPreference(0);
        return user;
    }

    @Test
    void testPerfectMatchTrail() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        List<String> trailKeywords = Arrays.asList("lake", "forest", "mountain", "historic-site");

        double score = matchMakingService.scoreTrail(trailKeywords);

        assertEquals(1.0, score, 0.0001, "Perfect match trail should score 100%");
    }

    @Test
    void testPartialMatchTrail() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        List<String> trailKeywords = Arrays.asList("lake", "forest");

        double score = matchMakingService.scoreTrail(trailKeywords);

        // Should be less than 1.0 but greater than 0
        assertEquals(0.5, score, 0.0001, "Partial match should be about 50%");
    }

    @Test
    void testNoMatchTrail() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        List<String> trailKeywords = Arrays.asList("desert", "beach");

        double score = matchMakingService.scoreTrail(trailKeywords);

        assertEquals(0.0, score, 0.0001, "No match should return 0%");
    }
}
