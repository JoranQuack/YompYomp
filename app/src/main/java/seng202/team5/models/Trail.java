package seng202.team5.models;

import java.util.Date;

public class Trail {
    //base variables from DOC dataset (some may be irrelavent)
    private int id;
    private String name;
    private String description;
    private String difficulty;
    private String completionTime;
    private String hasAlerts;
    private String thumbnailURL;
    private String webpageURL;
    private String dateLoaded;
    private double shapeLength;

    //Constructor for base variables
    public Trail(int id, String name, String difficulty, String description, String completionTime, String hasAlerts, String thumbnailURL, String webpageURL, String dateLoaded, double shapeLength) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.completionTime = completionTime;
        this.hasAlerts = hasAlerts;
        this.thumbnailURL = thumbnailURL;
        this.webpageURL = webpageURL;
        this.dateLoaded = dateLoaded;
        this.shapeLength = shapeLength;
    }

    //Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public String getCompletionTime() { return completionTime; }
    public String getHasAlerts() { return hasAlerts; }
    public String getThumbnailURL() { return thumbnailURL; }
    public String getWebpageURL() { return webpageURL; }
    public String getDateLoaded() { return dateLoaded; }
    public double getShapeLength() { return shapeLength; }

    //Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setCompletionTime(String completionTime) { this.completionTime = completionTime; }
    public void setHasAlerts(String hasAlerts) { this.hasAlerts = hasAlerts; }
    public void setThumbnailURL(String thumbnailURL) { this.thumbnailURL = thumbnailURL; }
    public void setWebpageURL(String webpageURL) { this.webpageURL = webpageURL; }
    public void setDateLoaded(String dateLoaded) { this.dateLoaded = dateLoaded; }
    public void setShapeLength(int shapeLength) { this.shapeLength = shapeLength; }

}
