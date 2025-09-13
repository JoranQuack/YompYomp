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
    private String type;
    private String thumbnailURL;
    private String webpageURL;
    private String dateLoaded;
    // private double shapeLength;
    private double x;
    private double y;
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
     * @param type
     * @param thumbnailURL
     * @param webpageURL
     * @param dateLoaded
     * @param x
     * @param y
     * @param userWeight
     */
    public Trail(int id, String name, String difficulty, String description, String completionInfo,
            int minCompletionTimeMinutes, int maxCompletionTimeMinutes, String completionType, String timeUnit,
            boolean isMultiDay, boolean hasVariableTime, String type, String thumbnailURL, String webpageURL,
            String dateLoaded, double x, double y, double userWeight) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.completionInfo = completionInfo;
        this.minCompletionTimeMinutes = minCompletionTimeMinutes;
        this.maxCompletionTimeMinutes = maxCompletionTimeMinutes;
        this.completionType = completionType;
        this.timeUnit = timeUnit;
        this.isMultiDay = isMultiDay;
        this.hasVariableTime = hasVariableTime;
        this.type = type;
        this.thumbnailURL = thumbnailURL;
        this.webpageURL = webpageURL;
        this.dateLoaded = dateLoaded;
        this.x = x;
        this.y = y;
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
     * @param type
     * @param thumbnailURL
     * @param webpageURL
     * @param dateLoaded
     * @param x
     * @param y
     */
    public Trail(int id, String name, String difficulty, String description, String completionInfo,
            String type,
            String thumbnailURL, String webpageURL, String dateLoaded, double x, double y) {
        this(id, name, difficulty, description, completionInfo, 0, 0, "unknown", "unknown",
                false, false, type, thumbnailURL, webpageURL, dateLoaded, x, y, 0.0);
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

    public String getType() {
        return type;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getWebpageURL() {
        return webpageURL;
    }

    public String getDateLoaded() {
        return dateLoaded;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void setWebpageURL(String webpageURL) {
        this.webpageURL = webpageURL;
    }

    public void setDateLoaded(String dateLoaded) {
        this.dateLoaded = dateLoaded;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public void setUserWeight(double userWeight) {
        this.userWeight = userWeight;
    }
}
