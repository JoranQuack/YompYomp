package seng202.team5.services;

import seng202.team5.data.FileBasedKeywordRepo;
import seng202.team5.data.IKeyword;
import seng202.team5.models.User;

import java.util.*;

/**
 * MatchMakingService takes uer preferences and trail keywords,
 * then calculates a weighted score for each trail.
 */
public class MatchMakingService {

    private final Map<String, List<String>> categoryToKeywords;//category -> keywords
    private final Map<String, String> keywordToCategory = new HashMap<>();
    private final Map<String, Integer> userWeights = new HashMap<>();

    public MatchMakingService(IKeyword keywordRepo) {
        this.categoryToKeywords = keywordRepo.getKeywords();
        buildReverseIndex();
    }

    /**
     * Build keyword -> category map once from the CSV data
     */
    private void buildReverseIndex() {
        for (Map.Entry<String, List<String>> entry : categoryToKeywords.entrySet()) {
            String category = entry.getKey();
            for (String keyword : entry.getValue()) {
                keywordToCategory.put(keyword.toLowerCase(Locale.ROOT), category);
            }
        }
    }

    /**
     * Sets the user preferences from a User object.
     * Each preference is maps to a keyword category
     * @param user the user whose preferences will be mapped
     */
    public void setUserPreferences(User user) {
        userWeights.clear();

        //Yes/No simplified to 0 or 5
        userWeights.put("FamilyFriendly", user.getIsFamilyFriendly() ?5:0);
        userWeights.put("Accessible", user.getIsAccessible() ?5:0);

        userWeights.put("Difficult", user.getExperienceLevel());
        userWeights.put("Rocky", user.getGradientPreference());
        userWeights.put("Forest", user.getBushPreference());
        userWeights.put("Wet", user.getLakeRiverPreference());
        userWeights.put("Beach", user.getCoastPreference());
        userWeights.put("Alpine", user.getMountainPreference());
        userWeights.put("Wildlife", user.getWildlifePreference());
        userWeights.put("Historical", user.getHistoricPreference());
        userWeights.put("Waterfall", user.getWaterfallPreference());
        userWeights.put("Reserve", user.getReservePreference());
    }

    /**
     * Calculates a score for a trail given its keywords based on the user's
     * answers to the setup questions. This is where a percentage match is calculated.
     * @param trailKeywords list of keywords for a trail
     * @return weighted score for a trail between 0.0 and 1.0
     */
    public double scoreTrail(List<String> trailKeywords) {
        if (trailKeywords == null || trailKeywords.isEmpty()) {
            return 0.0;
        }

        //maximum possible score that a trail could get if it matches the user's preferences perfectly
        int maxScore = userWeights.values().stream().mapToInt(Integer::intValue).sum();
        if (maxScore <= 0) {
            return 0.0;
        }

        //Avoid double-counting categories
        Set<String> matchedCategories = new HashSet<>();

        for (String kw: trailKeywords) {
            if (kw == null) {
                continue;
            }
            String category = keywordToCategory.get(kw.toLowerCase(Locale.ROOT));
            if (category != null && userWeights.containsKey(category)) {
                matchedCategories.add(category);
            }
        }

        int score = matchedCategories.stream()
                .mapToInt(cat -> userWeights.getOrDefault(cat, 0))
                .sum();
        return (double) score/maxScore;
    }

    public Map<String, Integer> getUserWeights() {
        return userWeights;
    }
}
