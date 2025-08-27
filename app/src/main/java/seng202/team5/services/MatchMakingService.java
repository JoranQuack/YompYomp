package seng202.team5.services;

import seng202.team5.data.IKeyword;
import seng202.team5.models.User;

import java.util.*;

/**
 * MatchMakingService takes uer preferences and trail keywords,
 * then calculates a weighted score for each trail.
 */
public class MatchMakingService {

    private final Map<String, List<String>> keywordCategories; //category -> keywords
    private final Map<String, Integer> userWeights = new HashMap<>();

    public MatchMakingService(IKeyword keywordRepo) {
        this.keywordCategories = keywordRepo.getKeywords();
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
}
