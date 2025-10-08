package seng202.team5.utils;

import javafx.scene.image.Image;

/**
 * Utility class for formatting weather data such as day of the week, date
 * strings, and icons
 */
public class WeatherFormatter {
    private static final String WEATHER_DIR = "/images/weather/";

    /**
     * Returns the day of the week for a given date string
     */
    public static String getDayOfWeek(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "Now";
        }
        String[] parts = dateStr.split("-");
        if (parts.length != 3) {
            return "Invalid Date";
        }
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        java.time.LocalDate date = java.time.LocalDate.of(year, month, day);
        return date.getDayOfWeek().toString().substring(0, 3);
    }

    /**
     * Returns a weather icon based on the weather description
     */
    public static Image getWeatherIcon(String iconType) {
        if (iconType == null || iconType.isEmpty()) {
            return new Image(WEATHER_DIR + "02.png");
        }
        return new Image(WEATHER_DIR + iconType.substring(0, iconType.length() - 1) + ".png");
    }
}
