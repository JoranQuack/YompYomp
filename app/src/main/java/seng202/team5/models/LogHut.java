package seng202.team5.models;

import java.util.Date;

//Some basic variables
public class LogHut {
    private int id;
    private User user;
    private Hut hut;
    private Date dateVisited;
    private String notes;

    //Constructor for base variables
    public LogHut(int id, User user, Hut hut, Date dateVisited, String notes) {
        this.id = id;
        this.user = user;
        this.hut = hut;
        this.dateVisited = dateVisited;
        this.notes = notes;
    }

    //getters
    public int getId() { return id; }
    public User getUser() { return user; }
    public Hut getHut() { return hut; }
    public Date getDateVisited() { return dateVisited; }
    public String getNotes() { return notes; }

    //setters
    public void setUser(User user) { this.user = user; }
    public void setHut(Hut hut) { this.hut = hut; }
    public void setDateVisited(Date dateVisited) { this.dateVisited = dateVisited; }
    public void setNotes(String notes) { this.notes = notes; }
}
