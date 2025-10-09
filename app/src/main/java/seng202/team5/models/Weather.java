package seng202.team5.models;

public record Weather(double temperature, double tempMax, double tempMin, String description, String date,
                      String iconType) {

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
