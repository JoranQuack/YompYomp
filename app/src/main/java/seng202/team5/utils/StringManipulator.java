package seng202.team5.utils;

public class StringManipulator {

    /**
     * Capitalise the first letter of a string.
     *
     * @param str The input string
     * @return The capitalised string
     */
    public static String capitaliseFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
