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
        String result = CompletionTimeParser.formatTimeRange(60, 120);
        assertEquals("1 - 2 hrs", result);
    }
}
