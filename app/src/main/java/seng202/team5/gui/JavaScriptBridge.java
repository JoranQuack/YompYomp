package seng202.team5.gui;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * A bridge that handles communication between the JavaScript code
 * running inside the WebView (map.html) and the JavaFX {@link ModifyTrailController}
 */
public class JavaScriptBridge {

    public void addMarkerFromClick(String latlng){
        JSONParser parser = new JSONParser();
        try {
            JSONObject latlng_json = (JSONObject) parser.parse(latlng);
            float lat = ((Double)latlng_json.get("lat")).floatValue();
            float lng = ((Double) latlng_json.get("lng")).floatValue();
            String logMessage = String.format("From Java: you clicked at %s, %s", lat, lng);
            System.out.println(logMessage);
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }
}
