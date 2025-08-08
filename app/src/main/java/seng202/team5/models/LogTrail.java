package seng202.team5.models;

import java.util.Date;

public class LogTrail {
    //Some basic variables
    private int id;
    private User user;
    private Trail trail;
    private Date dateCompleted;
    private int actualDuration; // hours
    private String weatherExperienced;
    private String personalDifficulty;
    private int rating;
    private boolean isMultiDayTrip;
    private String tripId; // for identifying trails that are a part of the same trip
    private String notes;



    //Constructor for base variables
    public LogTrail(int id, User user, Trail trail, Date dateCompleted, int actualDuration, String weatherExperienced,
                    String personalDifficulty, int rating, boolean isMultiDayTrip, String tripId, String notes) {
        this.id = id;
        this.user = user;
        this.trail = trail;
        this.dateCompleted = dateCompleted;
        this.actualDuration = actualDuration;
        this.weatherExperienced = weatherExperienced;
        this.personalDifficulty = personalDifficulty;
        this.rating = rating;
        this.isMultiDayTrip = isMultiDayTrip;
        this.tripId = tripId;
        this.notes = notes;
    }

    //Getters
    public int getId() { return id; }
    public User getUser() { return user; }
    public Trail getTrail() { return trail; }
    public Date getDateCompleted() { return dateCompleted; }
    public int getActualDuration() { return actualDuration; }
    public String getWeatherExperienced() { return weatherExperienced; }
    public String getPersonalDifficulty() { return personalDifficulty; }
    public int getRating() { return rating; }
    public boolean isMultiDayTrip() { return isMultiDayTrip; }
    public String getTripId() { return tripId; }
    public String getNotes() { return notes; }
    //Setters
    public void setUser(User user) { this.user = user; }
    public void setTrail(Trail trail) { this.trail = trail; }
    public void setDateCompleted(Date dateVisited) { this.dateCompleted = dateCompleted; }
    public void setActualDuration(int actualDuration) { this.actualDuration = actualDuration; }
    public void setWeatherExperienced(String weatherExperienced) { this.weatherExperienced = weatherExperienced; }
    public void setPersonalDifficulty(String personalDifficulty) { this.personalDifficulty = personalDifficulty; }
    public void setRating(int rating) { this.rating = rating; }
    public void setMultiDayTrip(boolean multiDayTrip) { this.isMultiDayTrip = multiDayTrip; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    public void setNotes(String notes) { this.notes = notes; }
}
