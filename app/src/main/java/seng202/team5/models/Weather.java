package seng202.team5.models;

public class Weather {
    private double temperature;
    private double tempMax;
    private double tempMin;
    private String description;
    private String date;
    private String iconType;

    public Weather(double temperature, double tempMax, double tempMin, String description, String date,
            String iconType) {
        this.temperature = temperature;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.description = description;
        this.date = date;
        this.iconType = iconType;
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

    public String getDate() {
        return date;
    }

    public String getIconType() {
        return iconType;
    }

    @Override
    public String toString() {
        if (date == null || date.isEmpty()) {
            return String.format("Now: %.1f°C (min %.1f°C / max %.1f°C) — %s",
                    temperature, tempMin, tempMax, description);
        } else {
            return String.format("%s: %.1f°C (min %.1f°C / max %.1f°C) — %s",
                    date, temperature, tempMin, tempMax, description);
        }
    }
}
