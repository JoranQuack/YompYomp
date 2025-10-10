package seng202.team5.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherFormatterTest {

    @Test
    @DisplayName("getDayOfWeek should return correct 3-letter day for valid date")
    void testGetDayOfWeek_ValidDate() {
        String result = WeatherFormatter.getDayOfWeek("2025-10-06"); //Monday
        assertEquals("MON", result, "Expected 'MON' for 2025-10-06");
    }

    @Test
    @DisplayName("getDayOfWeek should return 'Now' for null or empty input")
    void testGetDayOfWeek_EmptyInput() {
        String result = WeatherFormatter.getDayOfWeek("");
        assertEquals("Now", result, "Expected 'Now' for empty input");
    }

    @Test
    @DisplayName("getDayOfWeek should return 'Invalid Date' for malformed date")
    void testGetDayOfWeek_MalformedDate() {
        String result = WeatherFormatter.getDayOfWeek("2025/10/06");
        assertEquals("Invalid Date", result, "Expected 'Invalid Date' for malformed date");
    }

    @Test
    @DisplayName("formatDate should format YYYY-MM-DD to DD-MM-YYYY")
    void testFormatDate_ValidDate() {
        String result = WeatherFormatter.formatDate("2025-10-06");
        assertEquals("06/10/2025", result, "Expected to format date correctly");
    }

    @Test
    @DisplayName("formatDate should return today's date when input is null or empty")
    void testFormatDate_NullInput() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String result1 = WeatherFormatter.formatDate(null);
        String result2 = WeatherFormatter.formatDate("");
        assertEquals(today, result1, "Expected to get todays date correctly");
        assertEquals(today, result2, "Expected to get todays date correctly");
    }
}
