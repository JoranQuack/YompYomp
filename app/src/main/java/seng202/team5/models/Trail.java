package seng202.team5.models;

// import java.util.Date;

import java.util.HashSet;
import java.util.Set;

public class Trail {
    // base variables from DOC dataset (some may be irrelevant)
    private int id;
    private String name;
    private String description;
    private String difficulty;
    private String completionInfo;
    private int minCompletionTimeMinutes;
    private int maxCompletionTimeMinutes;
    private String completionType;
    private String timeUnit;
    private boolean isMultiDay;
    private boolean hasVariableTime;
    private String thumbnailURL;
    private String webpageURL;
    private Set<String> categories = new HashSet<>();
    private double userWeight;

    /**
     * Constructor for Trail class with userWeight and enhanced time fields
     *
     * @param id
     * @param name
     * @param difficulty
     * @param description
     * @param completionInfo
     * @param minCompletionTimeMinutes
     * @param maxCompletionTimeMinutes
     * @param completionType
     * @param timeUnit
     * @param isMultiDay
     * @param hasVariableTime
     * @param thumbnailURL
     * @param webpageURL
     * @param userWeight
     */
    public Trail(int id, String name, String difficulty, String description, String completionInfo,
            int minCompletionTimeMinutes, int maxCompletionTimeMinutes, String completionType, String timeUnit,
            boolean isMultiDay, boolean hasVariableTime, String thumbnailURL, String webpageURL, double userWeight) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.description = description;
        this.completionInfo = completionInfo;
        this.minCompletionTimeMinutes = minCompletionTimeMinutes;
        this.maxCompletionTimeMinutes = maxCompletionTimeMinutes;
        this.completionType = completionType;
        this.timeUnit = timeUnit;
        this.isMultiDay = isMultiDay;
        this.hasVariableTime = hasVariableTime;
        this.thumbnailURL = thumbnailURL;
        this.webpageURL = webpageURL;
        this.userWeight = userWeight;
    }

    /**
     * Constructor for Trail class using just dataset values.
     * Sets userWeight to default value of 0.0
     * Sets the completionTime to default value of 0
     * Sets enhanced time fields to default values
     *
     * @param id
     * @param name
     * @param difficulty
     * @param description
     * @param completionInfo
     * @param thumbnailURL
     * @param webpageURL
     */
    public Trail(int id, String name, String description, String difficulty, String completionInfo,
            String thumbnailURL, String webpageURL) {
        this(id, name, description, difficulty, completionInfo, 0, 0, "unknown", "unknown",
                false, false, thumbnailURL, webpageURL, 0.0);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getCompletionInfo() {
        return completionInfo;
    }

    public int getMinCompletionTimeMinutes() {
        return minCompletionTimeMinutes;
    }

    public int getMaxCompletionTimeMinutes() {
        return maxCompletionTimeMinutes;
    }

    public String getCompletionType() {
        return completionType;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public boolean isMultiDay() {
        return isMultiDay;
    }

    public boolean hasVariableTime() {
        return hasVariableTime;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getWebpageURL() {
        return webpageURL;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public double getUserWeight() {
        return userWeight;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setCompletionInfo(String completionInfo) {
        this.completionInfo = completionInfo;
    }

    public void setMinCompletionTimeMinutes(int minCompletionTimeMinutes) {
        this.minCompletionTimeMinutes = minCompletionTimeMinutes;
    }

    public void setMaxCompletionTimeMinutes(int maxCompletionTimeMinutes) {
        this.maxCompletionTimeMinutes = maxCompletionTimeMinutes;
    }

    public void setCompletionType(String completionType) {
        this.completionType = completionType;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void setMultiDay(boolean multiDay) {
        this.isMultiDay = multiDay;
    }

    public void setHasVariableTime(boolean hasVariableTime) {
        this.hasVariableTime = hasVariableTime;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void setWebpageURL(String webpageURL) {
        this.webpageURL = webpageURL;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public void setUserWeight(double userWeight) {
        this.userWeight = userWeight;
    }
}
