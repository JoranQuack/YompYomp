package seng202.team5.services;

import seng202.team5.data.IKeyword;

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
}
