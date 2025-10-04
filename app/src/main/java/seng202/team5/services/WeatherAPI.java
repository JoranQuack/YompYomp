package seng202.team5.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import seng202.team5.models.Weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {

    // Your student/developer API key
    private static final String API_KEY = "API_KEY";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    /**
     * Fetches current weather for a given latitude and longitude
     *
     * @param lat Latitude of location
     * @param lon Longitude of location
     * @return Weather object with temperature, min/max, and description
     */
    public static Weather getWeatherByCoords(double lat, double lon) {
        try {
            // Build the URL for the Current Weather API
            String urlStr = String.format(
                    "%s?lat=%f&lon=%f&units=metric&appid=%s",
                    BASE_URL, lat, lon, API_KEY
            );
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                System.err.println("Error fetching weather: " + conn.getResponseCode());
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            // Parse JSON response
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject main = json.getAsJsonObject("main");
            JsonObject weatherObj = json.getAsJsonArray("weather").get(0).getAsJsonObject();

            double temp = main.get("temp").getAsDouble();
            double tempMin = main.get("temp_min").getAsDouble();
            double tempMax = main.get("temp_max").getAsDouble();
            String description = weatherObj.get("description").getAsString();

            return new Weather(temp, tempMin, tempMax, description);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
