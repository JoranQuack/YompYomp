package seng202.team5.models;

public class User {
    //Some basic variables
    private String name;
    private String email;
    private int experienceLevel;

    //Constructor for base variables
    public User(String name, String email, int experienceLevel) {
        this.name = name;
        this.email = email;
        this.experienceLevel = experienceLevel;
    }

    //getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getExperienceLevel() { return experienceLevel; }

    //setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setExperienceLevel(int experienceLevel) { this.experienceLevel = experienceLevel; }
}
