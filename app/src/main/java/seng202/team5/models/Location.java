package seng202.team5.models;

public class Location {
    //base variables
    private double latitude;
    private double longitude;
    private String region;
    private double altitude;

    //Constructor for base variables
    public Location(double latitude, double longitude, String region, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.region = region;
        this.altitude = altitude;
    }

    //Getters
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getRegion() { return region; }
    public double getAltitude() { return altitude; }

    //setters
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setRegion(String region) { this.region = region; }
    public void setAltitude(double altitude) { this.altitude = altitude; }
}
