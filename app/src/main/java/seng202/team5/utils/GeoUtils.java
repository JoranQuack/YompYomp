package seng202.team5.utils;

/**
 * Utility clas for geographic calculations
 */
public class GeoUtils {
    private static final int EARTH_RADIUS_KM = 6371;

    /**
     * Calculates the great-circle distance between two points on the Earth specified by their
     * latitude and longitude using the Haversine formula
     *
     * @param lat1 the latitude of the first point in decimal degrees
     * @param lon1 the longitude of the first point in decimal degrees
     * @param lat2 the latitude of the second point in decimal degrees
     * @param lon2 the longitude of the second point in decimal degrees
     * @return the distance between the two points in kilometers
     */
    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) *  Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return EARTH_RADIUS_KM * c;
    }
}
