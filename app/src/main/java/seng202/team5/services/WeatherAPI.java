package seng202.team5.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import seng202.team5.models.Weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {
    private static final String API_KEY = "API_KEY_HERE";
    private static final String BASE_URL = "https://api.openweathermap.org/data/3.0/onecall";

    public static Weather getWeatherByCoords(double lat, double lon) {
        try {
            String urlStr = String.format("%s?lat=%f&lon=%f&exclude=minutely,hourly,alerts&units=metric&appid=%s",
                    BASE_URL, lat, lon, API_KEY);
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

            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject current = json.getAsJsonObject("current");
            JsonObject today = json.getAsJsonArray("daily").get(0).getAsJsonObject();

            double temp = current.get("temp").getAsDouble();
            double tempMin = today.getAsJsonObject("temp").get("min").getAsDouble();
            double tempMax = today.getAsJsonObject("temp").get("max").getAsDouble();
            String description = current.getAsJsonArray("weather")
                    .get(0).getAsJsonObject().get("description").getAsString();

            return new Weather(temp, tempMin, tempMax, description);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
