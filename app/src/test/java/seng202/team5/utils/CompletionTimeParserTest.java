package seng202.team5.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;

public class CompletionTimeParserTest {

    @Test
    @DisplayName("Should parse basic hours correctly")
    public void testParseCompletionTime_basicHours() {
        CompletionTimeParser.CompletionTimeResult result = CompletionTimeParser.parseCompletionTime("2 hrs");

        assertEquals(120, result.getMinCompletionTimeMinutes());
        assertEquals(120, result.getMaxCompletionTimeMinutes());
        assertEquals("unknown", result.getCompletionType());
        assertEquals("hours", result.getTimeUnit());
        assertFalse(result.isMultiDay());
        assertFalse(result.hasVariableTime());
    }

    @Test
    @DisplayName("Should handle null input gracefully - default values include False for hasVariableTime")
    public void testParseCompletionTime_nullInput() {
        CompletionTimeParser.CompletionTimeResult result = CompletionTimeParser.parseCompletionTime(null);

        assertEquals(0, result.getMinCompletionTimeMinutes());
        assertEquals(0, result.getMaxCompletionTimeMinutes());
        assertEquals("unknown", result.getCompletionType());
        assertEquals("unknown", result.getTimeUnit());
        assertFalse(result.isMultiDay());
        assertFalse(result.hasVariableTime());
    }

    @Test
    @DisplayName("Should handle off-case input correctly")
    public void testParseCompletionTime_offCase() {
        CompletionTimeParser.CompletionTimeResult result = CompletionTimeParser
                .parseCompletionTime("2 hrs 20 min each way");

        assertEquals(140, result.getMinCompletionTimeMinutes());
        assertEquals(140, result.getMaxCompletionTimeMinutes());
        assertEquals("one way", result.getCompletionType());
        assertEquals("hours", result.getTimeUnit());
        assertFalse(result.isMultiDay());
        assertFalse(result.hasVariableTime());
    }

    @Test
    @DisplayName("Should format basic minutes correctly")
    public void testFormatMinutesToString_basicMinutes() {
        String result = CompletionTimeParser.formatMinutesToString(45);
        assertEquals("45 min", result);
    }

    @Test
    @DisplayName("Should format zero minutes as 'Unknown'")
    public void testFormatMinutesToString_zero() {
        String result = CompletionTimeParser.formatMinutesToString(0);
        assertEquals("Unknown", result);
    }

    @Test
    @DisplayName("Should format time range with same start and end time correctly")
    public void testFormatTimeRange_sameTime() {
        String result = CompletionTimeParser.formatTimeRange(60, 60);
        assertEquals("1 hr", result);
    }

    @Test
    @DisplayName("Should format time range with different start and end times correctly")
    public void testFormatTimeRange_differentTimes() {
        String result = CompletionTimeParser.formatTimeRange(30, 60);
        assertEquals("30 min - 1 hr", result);
    }

    @Test
    @DisplayName("Should convert minutes less than 60 correctly")
    public void testConvertFromMinutesBasicMinutes() {
        CompletionTimeParser.TimeValue result = CompletionTimeParser.convertFromMinutes(45);
        assertEquals(45, result.value());
        assertEquals("minutes", result.unit());
    }

    @Test
    @DisplayName("Should convert whole hours correctly")
    public void testConvertFromMinutesWholeHours() {
        CompletionTimeParser.TimeValue result = CompletionTimeParser.convertFromMinutes(120);
        assertEquals(2, result.value());
        assertEquals("hours", result.unit());
    }

    @Test
    @DisplayName("Should convert mixed hours and minutes correctly")
    public void testConvertFromMinutesMixedHoursAndMinutes() {
        CompletionTimeParser.TimeValue result = CompletionTimeParser.convertFromMinutes(150);
        assertEquals(2.5, result.value());
        assertEquals("hours", result.unit());
    }

    @Test
    @DisplayName("Should convert full days correctly")
    public void testConvertFromMinutesFullDays() {
        CompletionTimeParser.TimeValue result = CompletionTimeParser.convertFromMinutes(2880);
        assertEquals(2.0, result.value());
        assertEquals("days", result.unit());
    }

    @Test
    @DisplayName("Should convert partial days correctly")
    public void testConvertFromMinutesHalfDays() {
        CompletionTimeParser.TimeValue result = CompletionTimeParser.convertFromMinutes(1980);
        assertEquals(1.375, result.value());
        assertEquals("days", result.unit());
    }
}
