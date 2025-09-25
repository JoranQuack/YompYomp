package seng202.team5.models;

public class Weather {
    private double latitude;
    private double longitude;
    private double temperature;
    private double tempMax;
    private double tempMin;
    private String location;

    public Weather(double latitude, double longitude, double temperature, double tempMax, double tempMin, String location) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getTempMax() {
        return tempMax;
    }

    public double getTempMin() {
        return tempMin;
    }

    public String getLocation() {
        return location;
    }
}


