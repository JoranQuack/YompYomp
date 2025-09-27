package seng202.team5.gui;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * A bridge that handles communication between the JavaScript code
 * running inside the WebView (map.html) and the JavaFX {@link ModifyTrailController}
 */
public class JavaScriptBridge {

    private final ModifyTrailController controller;

    /**
     * Creates a new JavaScriptBridge that will update the given controller
     * when the user interacts with the map
     * @param controller the controller responsible for handling updates
     *                   to latitude/longitude fields when a marker is added
     */
    public JavaScriptBridge(ModifyTrailController controller) {
        this.controller = controller;
    }

    /**
     * Called from JavaScript when the user clicks on the map
     * This method parses the JSON string containing the latitude and longitude,
     * extracts the numeric values and forwards them to the controller's
     * {@link ModifyTrailController#updateLatLonFields(double, double)} method
     * @param latlng a JSON string containing latitude and longitude
     */
    public void addMarkerFromClick(String latlng){
        JSONParser parser = new JSONParser();
        try {
            JSONObject latlng_json = (JSONObject) parser.parse(latlng);
            double lat = (Double) latlng_json.get("lat");
            double lng = (Double) latlng_json.get("lng");
            controller.updateLatLonFields(lat, lng);
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }
}
