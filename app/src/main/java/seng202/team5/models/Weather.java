package seng202.team5.models;

public class Weather {
    private double temperature;
    private double tempMax;
    private double tempMin;
    private String description;
    private String date;

    public Weather(double temperature, double tempMax, double tempMin, String description, String date) {
        this.temperature = temperature;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.description = description;
        this.date = date;
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

    public String getDate() {return date;}

    @Override
    public String toString() {
        return String.format("Current: %.1f°C, Min: %.1f°C, Max: %.1f°C, %s",
                temperature, tempMin, tempMax, description);
    }
}

