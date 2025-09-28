package seng202.team5.models;

import java.util.Date;

public class TripLog {
    //Some basic variables
    private int id;
    private int userId;
    private int trailId;
    private Date startDate;
    private Date endDate;
    private String completionInfo; // Raw duration string
    private int minDurationMinutes;
    private int maxDurationMinutes;
    private String durationType;
    private String timeUnit;
    private boolean isMultiDay;
    private boolean hasVariableTime;
    private String notes;
    private int rating;
    private String weather;
    private String personalDifficulty;



    //Constructor for base variables
    public TripLog(int id, int userId, int trailId, Date startDate, Date endDate, String completionInfo,
                   int minDurationMinutes, int maxDurationMinutes, String durationType, String timeUnit,
                   boolean isMultiDay, boolean hasVariableTime, String notes, int rating, String weather,
                   String personalDifficulty) {
        this.id = id;
        this.userId = userId;
        this.trailId = trailId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.completionInfo = completionInfo;
        this.minDurationMinutes = minDurationMinutes;
        this.maxDurationMinutes = maxDurationMinutes;
        this.durationType = durationType;
        this.timeUnit = timeUnit;
        this.isMultiDay = isMultiDay;
        this.hasVariableTime = hasVariableTime;
        this.notes = notes;
        this.rating = rating;
        this.weather = weather;
        this.personalDifficulty = personalDifficulty;
    }

    //Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getTrailId() { return trailId; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public String getCompletionInfo() { return completionInfo; }
    public int getMinDurationMinutes() { return minDurationMinutes; }
    public int getMaxDurationMinutes() { return maxDurationMinutes; }
    public String getDurationType() { return durationType; }
    public String getTimeUnit() { return timeUnit; }
    public boolean isMultiDay() { return isMultiDay; }
    public boolean isHasVariableTime() { return hasVariableTime; }
    public String getNotes() { return notes; }
    public int getRating() { return rating; }
    public String getWeather() { return weather; }
    public String getPersonalDifficulty() { return personalDifficulty; }

    //Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setTrail(int trailId) { this.trailId = trailId; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public void setCompletionInfo(String completionInfo) { this.completionInfo = completionInfo; }
    public void setMinDurationMinutes(int minDurationMinutes) { this.minDurationMinutes = minDurationMinutes; }
    public void setMaxDurationMinutes(int maxDurationMinutes) { this.maxDurationMinutes = maxDurationMinutes; }
    public void setDurationType(String durationType) { this.durationType = durationType; }
    public void setTimeUnit(String timeUnit) { this.timeUnit = timeUnit; }
    public void setIsMultiDay(boolean isMultiDay) { this.isMultiDay = isMultiDay; }
    public void setHasVariableTime(boolean hasVariableTime) { this.hasVariableTime = hasVariableTime; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setRating(int rating) { this.rating = rating; }
    public void setWeather(String weather) { this.weather = weather; }
    public void setPersonalDifficulty(String difficulty) { this.personalDifficulty = difficulty; }
}
