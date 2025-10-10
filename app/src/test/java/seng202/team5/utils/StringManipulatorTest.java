package seng202.team5.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StringManipulatorTest {

    @Test
    @DisplayName("Should capitalise first letter of a lowercase word")
    void testCapitaliseNormalWord() {
        String result = StringManipulator.capitaliseFirstLetter("trail");
        assertEquals("Trail", result);
    }

    @Test
    @DisplayName("Should leave already-capitalised string unchanged")
    void testAlreadyCapitalised() {
        String result = StringManipulator.capitaliseFirstLetter("Trail");
        assertEquals("Trail", result);
    }

    @Test
    @DisplayName("Should return same string for single-character input")
    void testSingleCharacter() {
        assertEquals("T", StringManipulator.capitaliseFirstLetter("t"));
        assertEquals("T", StringManipulator.capitaliseFirstLetter("T"));
    }

    @Test
    @DisplayName("Should handle null and empty strings safely")
    void testNullOrEmpty() {
        assertNull(StringManipulator.capitaliseFirstLetter(null), "Null input should return null");
        assertEquals("", StringManipulator.capitaliseFirstLetter(""), "Empty string should return empty");
    }

    @Test
    @DisplayName("Should not alter remainder of string beyond first character")
    void testPreserveRemainder() {
        String result = StringManipulator.capitaliseFirstLetter("trailName");
        assertEquals("TrailName", result, "Only first letter should change");
    }
}
