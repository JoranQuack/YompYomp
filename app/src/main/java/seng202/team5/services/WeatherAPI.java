package seng202.team5.services;

import seng202.team5.models.Weather;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {

    private static final String API_KEY = "your_api_key_here";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static Weather getWeatherByCoords(double lat, double lon) {
        try {
            String urlStr = String.format("%s?lat=%f&lon=%f&appid=%s&units=metric",
                    BASE_URL, lat, lon, API_KEY);
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                System.err.println("Error: " + conn.getResponseCode());
                return null;
            }

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            double temp = json.getAsJsonObject("main").get("temp").getAsDouble();
            double tempMin = json.getAsJsonObject("main").get("temp_min").getAsDouble();
            double tempMax = json.getAsJsonObject("main").get("temp_max").getAsDouble();
            String description = json.getAsJsonArray("weather")
                    .get(0).getAsJsonObject()
                    .get("description").getAsString();

            return new Weather(temp, tempMin, tempMax, description);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
