package seng202.team5.gui;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import seng202.team5.App;
import seng202.team5.models.Trail;
import seng202.team5.services.TrailService;

/**
 * A bridge that handles communication between the JavaScript code
 * running inside the WebView (map.html) and the JavaFX
 * {@link ModifyTrailController}
 */
public class JavaScriptBridge {

    private final Controller controller;
    private final TrailService trailService;

    /**
     * Creates a new JavaScriptBridge that will update the given controller
     * when the user interacts with the map
     *
     * @param controller the controller responsible for handling updates
     *                   to latitude/longitude fields when a marker is added
     */
    public JavaScriptBridge(Controller controller) {
        this.controller = controller;
        this.trailService = new TrailService(App.getTrailRepo());
    }

    /**
     * Called from JavaScript when the user clicks on the map
     * This method parses the JSON string containing the latitude and longitude,
     * extracts the numeric values and forwards them to the controller's
     * <p>
     * {@link ModifyTrailController#updateLatLonFields(double, double)} method
     *
     * @param latlng a JSON string containing latitude and longitude
     */
    public void addMarkerFromClick(String latlng) {
        if (controller instanceof ModifyTrailController modifyTrailController) {
            JSONParser parser = new JSONParser();
            try {
                JSONObject latlng_json = (JSONObject) parser.parse(latlng);
                double lat = (Double) latlng_json.get("lat");
                double lng = (Double) latlng_json.get("lng");
                modifyTrailController.updateLatLonFields(lat, lng);
            } catch (org.json.simple.parser.ParseException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Opens the trail page for the trail with the specified id
     *
     * @param trailId the unique identifier of the trail whose info page should be
     *                opened
     */
    public void openTrailInfo(int trailId) {
        if (controller instanceof ViewTrailController viewTrailController) {
            Trail trail = trailService.findTrailById(trailId);
            if (trail != null) {
                viewTrailController.openTrailInfo(trail);
            }
        }
    }
}
