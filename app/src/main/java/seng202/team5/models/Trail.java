package seng202.team5.models;

// import java.util.Date;

import java.util.HashSet;
import java.util.Set;

public class Trail {
    // base variables from DOC dataset (some may be irrelevant)
    private int id;
    private String name;
    private String description;
    private String difficulty;
    private String completionTime;
    //private String hasAlerts;
    private String type;
    private String thumbnailURL;
    private String webpageURL;
    private String dateLoaded;
    //private double shapeLength;
    private double x;
    private double y;
    private Set<String> categories = new HashSet<>();

    // Constructor for base variables
    public Trail(int id, String name, String difficulty, String description, String completionTime, String type,
            String thumbnailURL, String webpageURL, String dateLoaded, double x, double y){//, double shapeLength) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.completionTime = completionTime;
        this.type = type;
        this.thumbnailURL = thumbnailURL;
        this.webpageURL = webpageURL;
        this.dateLoaded = dateLoaded;
        this.x = x;
        this.y = y;
        //this.shapeLength = shapeLength;
        this.categories = categories;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getCompletionTime() {
        return completionTime;
    }

    public String getType() {
        return type;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getWebpageURL() {
        return webpageURL;
    }

    public String getDateLoaded() {
        return dateLoaded;
    }

    //public double getShapeLength() {
        //return shapeLength;
    //}

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public Set<String> getCategories() {
        return categories;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setCompletionTime(String completionTime) {
        this.completionTime = completionTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void setWebpageURL(String webpageURL) {
        this.webpageURL = webpageURL;
    }

    public void setDateLoaded(String dateLoaded) {
        this.dateLoaded = dateLoaded;
    }

    //public void setShapeLength(int shapeLength) {
        //this.shapeLength = shapeLength;
    //}

    public void setX(double x) {
        this.x = x;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public void setY(double y) {
        this.y = y;
    }
}
