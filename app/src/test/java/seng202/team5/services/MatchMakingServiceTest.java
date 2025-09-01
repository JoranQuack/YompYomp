package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seng202.team5.data.IKeyword;
import seng202.team5.models.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchMakingServiceTest {

    private MatchMakingService matchMakingService;

    @BeforeEach
    void setUp() {
        IKeyword fakeKeywordRepo = () -> {
            Map<String, List<String>> map = new HashMap<>();
            map.put("FamilyFriendly", Arrays.asList("children", "easy"));
            map.put("Accessible", Arrays.asList("accessible", "abilities"));
            map.put("Difficult", Arrays.asList("difficult", "challenging"));
            map.put("Rocky", Arrays.asList("steep", "gorge"));
            map.put("Reserve", Arrays.asList("reserve", "park"));
            map.put("Wet", Arrays.asList("lake", "river"));
            map.put("Forest", Arrays.asList("forest", "bush"));
            map.put("Coast", Arrays.asList("coast", "beach"));
            map.put("Wildlife", Arrays.asList("wildlife", "animal"));
            map.put("Alpine", Arrays.asList("mountain", "hill"));
            map.put("Historical", Arrays.asList("historic-site", "ruins"));
            map.put("Waterfall", Arrays.asList("waterfall", "falls"));
            return map;
        };

        matchMakingService = new MatchMakingService(fakeKeywordRepo);
    }

    private User makeTestUser() {
        User user = new User();
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
    @DisplayName("Should return a partial match")
    void testPartialMatchTrail() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        List<String> trailKeywords = Arrays.asList("lake", "forest", "mountain", "sea");

        double score = matchMakingService.scoreTrail(trailKeywords);
        // matched keywords score of 13 and max score of 29 = 13/29 = 0.448
        assertEquals(0.448, score, 0.001);
    }

    @Test
    @DisplayName("No match should return 0%")
    void testNoMatchTrail() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        List<String> trailKeywords = Arrays.asList("waterfall", "beach");

        double score = matchMakingService.scoreTrail(trailKeywords);
        assertEquals(0.0, score, 0.0001);
    }

    @Test
    @DisplayName("Perfect match should return 100%")
    void TestPerfectMatch() {
        User user = makeTestUser();
        matchMakingService.setUserPreferences(user);

        //trail contains all keywords in repo
        List<String> trailKeywords =  Arrays.asList("lake", "forest", "mountain", "ruins", "children", "accessible", "difficult", "steep", "reserve", "coast", "wildlife", "waterfall");

        double score = matchMakingService.scoreTrail(trailKeywords);
        assertEquals(1.0, score, 0.0001);
    }
}
