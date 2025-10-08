package seng202.team5.utils;

import javafx.scene.image.Image;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting weather data such as day of the week, date
 * strings, and icons
 */
public class WeatherFormatter {
    private static final String WEATHER_DIR = "/images/weather/";
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
     * Formats a date string from YYYY-MM-DD to DD/MM/YYYY format
     */
    public static String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            // If no date, return today's date
            return LocalDate.now().format(OUTPUT_FORMAT);
        }
        String[] parts = dateStr.split("-");
        if (parts.length != 3) {
            return "Invalid Date";
        }
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        LocalDate date = LocalDate.of(year, month, day);
        return date.format(OUTPUT_FORMAT);
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
