package seng202.team5.data;

/**
 * Helper class for resolving test resource paths between our local development
 * and CI/CD
 */
public class TestPathHelper {

    /**
     * Gets the absolute path to a test resource file.
     *
     * @param resourcePath the relative path to the resource from
     *                     src/test/resources/
     * @return the absolute path to the resource
     */
    public static String getTestResourcePath(String resourcePath) {
        String path = TestPathHelper.class.getClassLoader().getResource(resourcePath).getPath();

        // Windows paths that start with / but contain :
        if (path.startsWith("/") && path.contains(":")) {
            path = path.substring(1);
        }

        return path;
    }
}
