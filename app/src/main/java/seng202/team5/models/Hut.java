package seng202.team5.models;

import seng202.team5.gui.ScreenNavigator;

import java.util.Date;

public class Hut {
    //base variables from DOC dataset (some may be irrelavent)
    private int id;
    private String name;
    private String place;
    private String region;
    private boolean bookable;
    private String facilities;
    private String hasAlerts;
    private String thumbnailURL;
    private String webpageURL;
    private String description;
    private int x;
    private int y;
    private int docID;
    private Date dateLoaded;
    private String golbalID;
    private int x2;
    private int y2;

    //Constructor for base variables
    public Hut(int id, String name, String place, String region, boolean bookable, String facilities, String hasAlerts, String thumbnailURL, String webpageURL, String description, int x, int y, int docID, Date dateLoaded, String golbalID, int x2, int y2) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.region = region;
        this.bookable = bookable;
        this.facilities = facilities;
        this.hasAlerts = hasAlerts;
        this.thumbnailURL = thumbnailURL;
        this.webpageURL = webpageURL;
        this.description = description;
        this.x = x;
        this.y = y;
        this.docID = docID;
        this.dateLoaded = dateLoaded;
        this.golbalID = golbalID;
        this.x2 = x2;
        this.y2 = y2;
    }

    //getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPlace() { return place; }
    public String getRegion() { return region; }
    public boolean isBookable() { return bookable; }
    public String getFacilities() { return facilities; }
    public String getHasAlerts() { return hasAlerts; }
    public String getThumbnailURL() { return thumbnailURL; }
    public String getWebpageURL() { return webpageURL; }
    public String getDescription() { return description; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getDocID() { return docID; }
    public Date getDateLoaded() { return dateLoaded; }
    public String getGolbalID() { return golbalID; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }

    //setters
    public void setName(String name) { this.name = name; }
    public void setPlace(String place) { this.place = place; }
    public void setRegion(String region) { this.region = region; }
    public void setBookable(boolean bookable) { this.bookable = bookable; }
    public void setFacilities(String facilities) { this.facilities = facilities; }
    public void setHasAlerts(String hasAlerts) { this.hasAlerts = hasAlerts; }
    public void setThumbnailURL(String thumbnailURL) { this.thumbnailURL = thumbnailURL; }
    public void setWebpageURL(String webpageURL) { this.webpageURL = webpageURL; }
    public void setDescription(String description) { this.description = description; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setDocID(int docID) { this.docID = docID; }
    public void setDateLoaded(Date dateLoaded) { this.dateLoaded = dateLoaded; }
    public void setGolbalID(String golbalID) { this.golbalID = golbalID; }
    public void setX2(int x2) { this.x2 = x2; }
    public void setY2(int y2) { this.y2 = y2; }

}
