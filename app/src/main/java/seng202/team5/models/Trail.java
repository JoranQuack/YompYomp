package seng202.team5.models;

import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;

import java.util.HashSet;
import java.util.Set;

/**
 * The base class entity representing a trail.
 */
public class Trail {

    private int id;
    private String name;
    private String translation;
    private String region;
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
    private String cultureUrl;
    private Set<String> categories = new HashSet<>();
    private double userWeight;
    private double lat;
    private double lon;

    /**
     * Constructor for Trail class with userWeight and enhanced time fields
     *
     * @param id the trail id
     * @param name the trail name
     * @param translation the trail translation
     * @param region the trail region
     * @param difficulty the trail difficulty
     * @param description the trail description
     * @param completionInfo the trail completion info
     * @param minCompletionTimeMinutes the minimum completion time in minutes
     * @param maxCompletionTimeMinutes the maximum completion time in minutes
     * @param completionType the completion type of the trail
     * @param timeUnit the time unit of the completion time
     * @param isMultiDay whether the trail is a multi-day trail
     * @param hasVariableTime whether the trail has variable time
     * @param thumbnailURL the URL of the thumbnail image
     * @param webpageURL the URL of the trail's webpage
     * @param cultureUrl the URL the user can contribute about the trail's culture
     * @param userWeight the user's weighting for the trail
     * @param lat the latitude of the trail's location
     * @param lon the longitude of the trail's location
     */
    public Trail(int id, String name, String translation, String region, String difficulty, String description,
            String completionInfo, int minCompletionTimeMinutes, int maxCompletionTimeMinutes,
            String completionType, String timeUnit, boolean isMultiDay, boolean hasVariableTime,
            String thumbnailURL, String webpageURL, String cultureUrl, double userWeight, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.translation = translation;
        this.region = region;
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
        this.cultureUrl = cultureUrl;
        this.userWeight = userWeight;
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Constructor for Trail class using just dataset values (for testing)
     * Sets userWeight to default value of 0.0
     * Sets the completionTime to default value of 0
     * Sets enhanced time fields to default values
     *
     * @param id the trail id
     * @param name the trail name
     * @param difficulty the trail difficulty
     * @param description the trail description
     * @param completionInfo the trail completion info
     * @param thumbnailURL the URL of the thumbnail image
     * @param webpageURL the URL of the trail's webpage
     * @param lat the latitude of the trail's location
     * @param lon the longitude of the trail's location
     */
    public Trail(int id, String name, String difficulty, String description, String completionInfo,
            String thumbnailURL, String webpageURL, double lat, double lon) {
        this(id, name, "", "", difficulty, description, completionInfo, 0, 0, "unknown", "unknown",
                false, false, thumbnailURL, webpageURL, "", 0.0, lat, lon);
    }

    /**
     * Empty constructor for Trail class (for testing)
     */
    public Trail() {
    }

    /**
     * Constructor for Trail class for user input data
     * Calls SqlBasedTrailRepo to get new trail id
     * @param id the trail id
     * @param name the trail name
     * @param translation the trail translation
     * @param region the trail region
     * @param difficulty the trail difficulty
     * @param completionType the completion type of the trail
     * @param completionInfo the trail completion info
     * @param description the trail description
     * @param thumbUrl the URL of the thumbnail image
     * @param webUrl the URL of the trail's webpage
     * @param cultureUrl the URL the user can contribute about the trail's culture
     * @param userWeight the user's weighting for the trail
     * @param lat the latitude of the trail's location
     * @param lon the longitude of the trail's location
     */
    public Trail(int id, String name, String translation, String region, String difficulty, String completionType,
            String completionInfo, String description, String thumbUrl, String webUrl, String cultureUrl,
            double userWeight, double lat, double lon) {
        SqlBasedTrailRepo sqlBasedTrailRepo = new SqlBasedTrailRepo(new DatabaseService());
        if (id == -1) {
            this.id = sqlBasedTrailRepo.getNewTrailId();
        } else {
            this.id = id;
        }
        this.name = name;
        this.translation = translation;
        this.region = region;
        this.difficulty = difficulty;
        this.completionType = completionType;
        this.completionInfo = completionInfo;
        this.description = description;
        this.cultureUrl = cultureUrl;
        this.minCompletionTimeMinutes = 0;
        this.maxCompletionTimeMinutes = 0;
        this.timeUnit = "unknown";
        this.isMultiDay = false;
        this.hasVariableTime = false;
        this.thumbnailURL = thumbUrl;
        this.webpageURL = webUrl;
        this.userWeight = userWeight;
        this.lat = lat;
        this.lon = lon;
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

    public int getAvgCompletionTimeMinutes() {
        return (minCompletionTimeMinutes + maxCompletionTimeMinutes) / 2;
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

    public String getCultureUrl() {
        return cultureUrl;
    }

    public String getTranslation() {
        return translation;
    }

    public String getRegion() {
        return region;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
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

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public void setUserWeight(double userWeight) {
        this.userWeight = userWeight;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
