package seng202.team5.models;

import java.util.Date;

public class TrailLog {

    private int id;
    private int trailId;
    private Date startDate;
    private Integer completionTime;
    private String timeUnit;
    private String completionType;
    private Integer rating;
    private String personalDifficulty;
    private String notes;

    /**
     * Constructor for a trail log
     *
     * @param id
     * @param trailId
     * @param startDate
     * @param timeUnit
     * @param rating
     * @param personalDifficulty
     * @param notes
     */
    public TrailLog(int id, int trailId, Date startDate, Integer completionTime, String timeUnit, String completionType,
                    Integer rating, String personalDifficulty, String notes) {
        this.id = id;
        this.trailId = trailId;
        this.startDate = startDate;
        this.completionTime = completionTime;
        this.timeUnit = timeUnit;
        this.completionType = completionType;
        this.rating = rating;
        this.personalDifficulty = personalDifficulty;
        this.notes = notes;
    }

    //Getters
    public int getId() { return id; }
    public int getTrailId() { return trailId; }
    public Date getStartDate() { return startDate; }
    public Integer getCompletionTime() { return completionTime; }
    public String getTimeUnit() { return timeUnit; }
    public String getCompletionType() { return completionType; }
    public Integer getRating() { return rating; }
    public String getPersonalDifficulty() { return personalDifficulty; }
    public String getNotes() { return notes; }

    //Setters
    public void setId(int id) { this.id = id; }
    public void setTrailId(int trailId) { this.trailId = trailId; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public void setCompletionTime(Integer completionTime) { this.completionTime = completionTime; }
    public void setTimeUnit(String timeUnit) { this.timeUnit = timeUnit; }
    public void setCompletionType(String completionType) { this.completionType = completionType; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setPersonalDifficulty(String personalDifficulty) { this.personalDifficulty = personalDifficulty; }
    public void setNotes(String notes) { this.notes = notes; }
}
