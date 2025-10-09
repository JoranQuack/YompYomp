package seng202.team5.models;

import java.util.HashSet;
import java.util.Set;

public class Trail {

    private final int id;
    private final String name;
    private final String translation;
    private final String region;
    private final String description;
    private final String difficulty;
    private final String completionInfo;
    private final int minCompletionTimeMinutes;
    private final int maxCompletionTimeMinutes;
    private final String completionType;
    private final String timeUnit;
    private final boolean isMultiDay;
    private final boolean hasVariableTime;
    private final String thumbnailURL;
    private final String webpageURL;
    private final String cultureUrl;
    private final Set<String> categories;
    private final double userWeight;
    private final double lat;
    private final double lon;

    private Trail(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.translation = builder.translation;
        this.region = builder.region;
        this.description = builder.description;
        this.difficulty = builder.difficulty;
        this.completionInfo = builder.completionInfo;
        this.minCompletionTimeMinutes = builder.minCompletionTimeMinutes;
        this.maxCompletionTimeMinutes = builder.maxCompletionTimeMinutes;
        this.completionType = builder.completionType;
        this.timeUnit = builder.timeUnit;
        this.isMultiDay = builder.isMultiDay;
        this.hasVariableTime = builder.hasVariableTime;
        this.thumbnailURL = builder.thumbnailURL;
        this.webpageURL = builder.webpageURL;
        this.cultureUrl = builder.cultureUrl;
        this.categories = builder.categories != null ? builder.categories : new HashSet<>();
        this.userWeight = builder.userWeight;
        this.lat = builder.lat;
        this.lon = builder.lon;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTranslation() { return translation; }
    public String getRegion() { return region; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public String getCompletionInfo() { return completionInfo; }
    public int getMinCompletionTimeMinutes() { return minCompletionTimeMinutes; }
    public int getMaxCompletionTimeMinutes() { return maxCompletionTimeMinutes; }
    public int getAvgCompletionTimeMinutes() {
        return (minCompletionTimeMinutes + maxCompletionTimeMinutes) / 2;
    }
    public String getCompletionType() { return completionType; }
    public String getTimeUnit() { return timeUnit; }
    public boolean isMultiDay() { return isMultiDay; }
    public boolean hasVariableTime() { return hasVariableTime; }
    public String getThumbnailURL() { return thumbnailURL; }
    public String getWebpageURL() { return webpageURL; }
    public String getCultureUrl() { return cultureUrl; }
    public Set<String> getCategories() { return categories; }
    public double getUserWeight() { return userWeight; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }

    // Builder
    public static class Builder {
        private int id;
        private String name = "";
        private String translation = "";
        private String region = "";
        private String description = "";
        private String difficulty = "unknown";
        private String completionInfo = "";
        private int minCompletionTimeMinutes = 0;
        private int maxCompletionTimeMinutes = 0;
        private String completionType = "unknown";
        private String timeUnit = "unknown";
        private boolean isMultiDay = false;
        private boolean hasVariableTime = false;
        private String thumbnailURL = "";
        private String webpageURL = "";
        private String cultureUrl = "";
        private Set<String> categories = new HashSet<>();
        private double userWeight = 0.0;
        private double lat;
        private double lon;

        public Builder() {}

        /** Copy constructor from existing Trail */
        public Builder from(Trail trail) {
            this.id = trail.getId();
            this.name = trail.getName();
            this.translation = trail.getTranslation();
            this.region = trail.getRegion();
            this.description = trail.getDescription();
            this.difficulty = trail.getDifficulty();
            this.completionInfo = trail.getCompletionInfo();
            this.minCompletionTimeMinutes = trail.getMinCompletionTimeMinutes();
            this.maxCompletionTimeMinutes = trail.getMaxCompletionTimeMinutes();
            this.completionType = trail.getCompletionType();
            this.timeUnit = trail.getTimeUnit();
            this.isMultiDay = trail.isMultiDay();
            this.hasVariableTime = trail.hasVariableTime();
            this.thumbnailURL = trail.getThumbnailURL();
            this.webpageURL = trail.getWebpageURL();
            this.cultureUrl = trail.getCultureUrl();
            this.categories = new HashSet<>(trail.getCategories());
            this.userWeight = trail.getUserWeight();
            this.lat = trail.getLat();
            this.lon = trail.getLon();
            return this;
        }

        // Builder setters
        public Builder id(int id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder translation(String translation) { this.translation = translation; return this; }
        public Builder region(String region) { this.region = region; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public Builder completionInfo(String completionInfo) { this.completionInfo = completionInfo; return this; }
        public Builder minCompletionTimeMinutes(int min) { this.minCompletionTimeMinutes = min; return this; }
        public Builder maxCompletionTimeMinutes(int max) { this.maxCompletionTimeMinutes = max; return this; }
        public Builder completionType(String type) { this.completionType = type; return this; }
        public Builder timeUnit(String timeUnit) { this.timeUnit = timeUnit; return this; }
        public Builder isMultiDay(boolean multiDay) { this.isMultiDay = multiDay; return this; }
        public Builder hasVariableTime(boolean variableTime) { this.hasVariableTime = variableTime; return this; }
        public Builder thumbnailURL(String url) { this.thumbnailURL = url; return this; }
        public Builder webpageURL(String url) { this.webpageURL = url; return this; }
        public Builder cultureUrl(String url) { this.cultureUrl = url; return this; }
        public Builder categories(Set<String> categories) { this.categories = categories; return this; }
        public Builder userWeight(double weight) { this.userWeight = weight; return this; }
        public Builder lat(double lat) { this.lat = lat; return this; }
        public Builder lon(double lon) { this.lon = lon; return this; }

        public Trail build() {
            return new Trail(this);
        }
    }
}
