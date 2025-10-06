package seng202.team5.models;

import java.time.LocalDate;


/**
 * The base class entity representing a trail log.
 */

public class TrailLog {

    private int id;
    private int trailId;
    private LocalDate startDate;
    private Integer completionTime;
    private String timeUnit;
    private String completionType;
    private Integer rating;
    private String perceivedDifficulty;
    private String notes;

    /**
     * Constructor for a trail log
     *
     * @param id the trail log id
     * @param trailId the trail id
     * @param startDate the date the trail was started
     * @param completionTime the completion time of the trail in minutes
     * @param timeUnit the time unit of the completion time
     * @param completionType the completion type of the trail
     * @param rating the rating of the trail
     * @param perceivedDifficulty the personal difficulty of the trail
     * @param notes the notes for the trail log
     */
    public TrailLog(int id, int trailId, LocalDate startDate, Integer completionTime, String timeUnit, String completionType,
                    Integer rating, String perceivedDifficulty, String notes) {
        this.id = id;
        this.trailId = trailId;
        this.startDate = startDate;
        this.completionTime = completionTime;
        this.timeUnit = timeUnit;
        this.completionType = completionType;
        this.rating = rating;
        this.perceivedDifficulty = perceivedDifficulty;
        this.notes = notes;
    }

    //Getters
    public int getId() { return id; }
    public int getTrailId() { return trailId; }
    public LocalDate getStartDate() { return startDate; }
    public Integer getCompletionTime() { return completionTime; }
    public String getTimeUnit() { return timeUnit; }
    public String getCompletionType() { return completionType; }
    public Integer getRating() { return rating; }
    public String getPerceivedDifficulty() { return perceivedDifficulty; }
    public String getNotes() { return notes; }

    //Setters
    public void setId(int id) { this.id = id; }
    public void setTrailId(int trailId) { this.trailId = trailId; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setCompletionTime(Integer completionTime) { this.completionTime = completionTime; }
    public void setTimeUnit(String timeUnit) { this.timeUnit = timeUnit; }
    public void setCompletionType(String completionType) { this.completionType = completionType; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setPerceivedDifficulty(String personalDifficulty) { this.perceivedDifficulty = personalDifficulty; }
    public void setNotes(String notes) { this.notes = notes; }
}
