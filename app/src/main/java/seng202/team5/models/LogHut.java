package seng202.team5.models;

import java.util.Date;

public class LogHut {
    //Some basic variables
    private int id;
    private User user;
    private Hut hut;
    private Date firstDateVisited;
    private int nightsStayed;
    private String occupancy; // Empty, Moderate, At Capacity
    private String hutCondition; // Excellent, Good, Fine, Poor, Uninhabitable
    private int rating;
    private boolean isMultiDayTrip;
    private String tripId;
    private String notes;


    //Constructor for base variables
    public LogHut(int id, User user, Hut hut, Date firstDateVisited, int nightsStayed, String occupancy,
                  String hutCondition, int rating, boolean isMultiDayTrip, String tripId, String notes) {
        this.id = id;
        this.user = user;
        this.hut = hut;
        this.firstDateVisited = firstDateVisited;
        this.nightsStayed = nightsStayed;
        this.occupancy = occupancy;
        this.hutCondition = hutCondition;
        this.rating = rating;
        this.isMultiDayTrip = isMultiDayTrip;
        this.tripId = tripId;
        this.notes = notes;
    }

    //Getters
    public int getId() { return id; }
    public User getUser() { return user; }
    public Hut getHut() { return hut; }
    public Date getFirstDateVisited() { return firstDateVisited; }
    public int getNightsStayed() { return nightsStayed; }
    public String getOccupancy() { return occupancy; }
    public String getHutCondition() { return hutCondition; }
    public int getRating() { return rating; }
    public boolean isMultiDayTrip() { return isMultiDayTrip; }
    public String getTripId() { return tripId; }
    public String getNotes() { return notes; }

    //Setters
    public void setUser(User user) { this.user = user; }
    public void setHut(Hut hut) { this.hut = hut; }
    public void setFirstDateVisited(Date firstDateVisited) { this.firstDateVisited = firstDateVisited; }
    public void setNightsStayed(int nightsStayed) { this.nightsStayed = nightsStayed; }
    public void setOccupancy(String occupancy) { this.occupancy = occupancy; }
    public void setHutCondition(String hutCondition) { this.hutCondition = hutCondition; }
    public void setRating(int rating) { this.rating = rating; }
    public void setMultiDayTrip(boolean multiDayTrip) { this.isMultiDayTrip = multiDayTrip; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    public void setNotes(String notes) { this.notes = notes; }
}
