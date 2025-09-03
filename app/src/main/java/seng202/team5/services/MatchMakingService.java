package seng202.team5.services;

import seng202.team5.data.IKeyword;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.models.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MatchMakingService takes user preferences and trail keywords,
 * then calculates a weighted score for each trail.
 * It categorises trails based on the keywords in the description,
 * computes user-personalised scores, and sorts the trail page using pagination.
 */
public class MatchMakingService {
    private final Map<String, List<String>> categoryToKeywords; //category -> keywords
    private final Map<String, String> keywordToCategory = new HashMap<>(); //keyword -> category
    private final Map<String, Integer> userWeights = new HashMap<>(); //Higher weight is more favourable
    private final Map<Integer, Double> trailWeights = new HashMap<>(); //Identified by trail ID
    private SqlBasedTrailRepo trailRepo;
    private boolean weightsCalculated = false;
    private int maxResults = 100;

    /**
     * Creates a MatchMakingService instance.
     *
     * @param keywordRepo repository for category-to-keyword data. This is temporary until the database is implemented for this.
     * @param trailRepo   repository for trail data which are used for scoring and sorting
     */
    public MatchMakingService(IKeyword keywordRepo, SqlBasedTrailRepo trailRepo) {
        this.categoryToKeywords = keywordRepo.getKeywords();
        this.trailRepo = trailRepo;
        buildReverseIndex();
    }

    /**
     * Builds the keyword-to-category reverse index once from the categoryToKeywords map.
     * Case-insensitive matching.
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
     * Resets all cached weights calculated and flags.
     * Used when user preferences change.
     */
    private void resetWeights() {
        userWeights.clear();
        trailWeights.clear();
        weightsCalculated = false;
    }

    /**
     * Sets the user preferences from a User object and derives per-category weights.
     * Boolean preferences are mapped to 0 or 5. Numeric preferences are mapped to their value.
     * Each preference is maps to a keyword category.
     *
     * @param user the user whose preferences will be mapped into category weights.
     */
    public void setUserPreferences(User user) {
        resetWeights();

        //Yes/No simplified to 0 or 5
        userWeights.put("FamilyFriendly", user.getIsFamilyFriendly() ? 5 : 0);
        userWeights.put("Accessible", user.getIsAccessible() ? 5 : 0);

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
     * Categorises a single trail by scanning its description for known keywords.
     * Matching is case-insensitive.
     *
     * @param trail the trail to categorise
     * @return a set of categories that match the trail's description or an empty set if no
     *         categories match
     */
    public Set<String> categoriseTrail(Trail trail) {
        Set<String> matchedCategories = new HashSet<>();
        String description = trail.getDescription().toLowerCase(Locale.ROOT);

        for (Map.Entry<String, String> entry : keywordToCategory.entrySet()) {
            String keyword = entry.getKey();
            if (description.contains(keyword)) {
                matchedCategories.add(entry.getValue());
            }
        }

        return matchedCategories;
    }

    /**
     * Calculates the max score used for calculating the weight of a trail.
     * Helper function!
     */
    private double calculateMaxScore() {
        double maxScore = userWeights.values().stream().mapToInt(Integer::intValue).sum();
        if (maxScore <= 0) {
            return 0.0;
        }
        return maxScore;
    }

    /**
     * Gets the max score used for calculating the weight of a trail.
     */
    public double getMaxScore() {
        return calculateMaxScore();
    }

    /**
     * Calculates a score for a trail given its categories it matches, based on the user's
     * answers to the setup questions. This is where a percentage match is calculated.
     *
     * @param trailCategories set of categories the trail matches
     * @return weighted score for the trail between 0.0 and 1.0
     */
    public double scoreTrail(Set<String> trailCategories) {
        if (trailCategories == null || trailCategories.isEmpty()) {
            return 0.0;
        }

        //maximum possible score that a trail could get if it matches the user's preferences perfectly
        double maxScore = calculateMaxScore();

        double score = trailCategories.stream()
                .mapToInt(category -> userWeights.getOrDefault(category, 0))
                .sum();
        return score / maxScore;
    }

    /**
     * Assigns weights to each trail based on the categories and user preferences.
     * Updates the trail models attributes, categories, and userWeight.
     */
    public void assignWeightsToTrails() {
        List<Trail> trails = trailRepo.getAllTrails();
        trailWeights.clear();

        for (Trail trail : trails) {
            Set<String> categories = categoriseTrail(trail);
            double weight = scoreTrail(categories);
            trail.setCategories(categories);
            trail.setUserWeight(weight);
            trailWeights.put(trail.getId(), weight);
        }

        weightsCalculated = true;
    }

    /**
     * Retrieves a cached weight for a specific trail ID.
     * If weights have not been calculated for the current user, they are computed first.
     *
     * @param trailId the ID of the trail to retrieve the weight for
     * @return the trail's cached weight
     */
    public double getTrailWeight(int trailId) {
        if (!weightsCalculated) {
            assignWeightsToTrails();
        }
        return trailWeights.getOrDefault(trailId, 0.0);
    }

    /**
     * Helper that returns all trails sorted by their personalised weight. (Highest first).
     * Weights are calculated and applied to each trail before sorting.
     * If weights are equal, they are ordered alphabetically (Lexicographically).
     *
     * @return list of trails sorted (descending) by personalised weight
     */
    private List<Trail> getSortedTrails() {
        if (!weightsCalculated) {
            assignWeightsToTrails();
        }

        return trailRepo.getAllTrails().stream()
                .peek(trail -> trail.setUserWeight(getTrailWeight(trail.getId())))
                .sorted((t1, t2) -> {
                    int weightCompare = Double.compare(t2.getUserWeight(), t1.getUserWeight());
                    if (weightCompare != 0) {
                        return weightCompare; // descending order if weights are different
                    }
                    return t1.getName().compareTo(t2.getName()); //orders alphabetically if weights are equal
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns all trails sorted by their personalised weight. (Recommending trails).
     * Personalised to the user's preferences.
     *
     * @return list of trails sorted (descending) by personalised weight
     */
    public List<Trail> getTrailsSortedByWeight() {
        return getSortedTrails();
    }

    /**
     * Returns a page of trails sorted by their personalised weight.
     *
     * @param page the page number (0-based)
     * @return a list containing up to pageSize trails for the specified page
     */
    public List<Trail> getPersonalisedTrails(int page) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number must be non-negative.");
        }

        List<Trail> sortedTrails = getSortedTrails();
        int startIndex = page * maxResults;
        return sortedTrails.stream()
                .skip(startIndex)
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    /**
     * Gets max results
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Set max results
     */
    public void setMaxResults(int maxResults) {
        if (maxResults <= 0) {
            throw new IllegalArgumentException("Max results must be positive.");
        }
        this.maxResults = maxResults;
    }

    /**
     * Calculate the number of personalised pages based on the number of trails, given a page size.
     *
     * @param pageSize number of trails per page
     * @return total page count
     */
    public int getNumberOfPersonalisedPages(int pageSize) {
        return (int) Math.ceil((double) trailRepo.getAllTrails().size() / pageSize);
    }

    /**
     * Returns a copy of the user weights map.
     * @return a Map containing user preference weights
     */
    public Map<String, Integer> getUserWeights() {
        return new HashMap<>(userWeights);
    }

}
