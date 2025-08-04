package seng202.team5.models;

import java.util.Date;

public class LogTrail {
    //some basic variables
    private int id;
    private User user;
    private Trail trail;
    private Date dateWalked;
    private String notes;

    //Constructor for base variables
    public LogTrail(int id, User user, Trail trail, Date dateWalked, String notes) {
        this.id = id;
        this.user = user;
        this.trail = trail;
        this.dateWalked = dateWalked;
        this.notes = notes;
    }

    //getters
    public int getId() { return id; }
    public User getUser() { return user; }
    public Trail getTrail() { return trail; }
    public Date getDateVisited() { return dateWalked; }
    public String getNotes() { return notes; }
    //setters
    public void setUser(User user) { this.user = user; }
    public void setTrail(Trail trail) { this.trail = trail; }
    public void setDateVisited(Date dateVisited) { this.dateWalked = dateVisited; }
    public void setNotes(String notes) { this.notes = notes; }
}
