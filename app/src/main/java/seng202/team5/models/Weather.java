package seng202.team5.models;

public class Weather {
    private double temperature;
    private double tempMax;
    private double tempMin;
    private String description;

    public Weather(double latitude, double longitude, double temperature, double tempMax, double tempMin, String location) {
        this.temperature = temperature;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.location = description;
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

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("Current: %.1f°C, Min: %.1f°C, Max: %.1f°C, %s",
                temperature, tempMin, tempMax, description);
}


