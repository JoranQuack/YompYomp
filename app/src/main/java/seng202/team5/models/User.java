package seng202.team5.models;

public class User {
    //Some basic variables
    private int id;
    private String name;
    private String email;
    private int experienceLevel;

    //Constructor for base variables
    public User(int id, String name, String email, int experienceLevel) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.experienceLevel = experienceLevel;
    }

    //getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getExperienceLevel() { return experienceLevel; }

    //setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setExperienceLevel(int experienceLevel) { this.experienceLevel = experienceLevel; }
}
