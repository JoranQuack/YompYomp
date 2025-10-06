package seng202.team5.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import seng202.team5.models.Weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class WeatherService {

    private static String apiKey;
    private final String baseUrl;
    private final String forecastUrl;

    public WeatherService(String baseUrl, String forecastUrl) {
        apiKey = getApiKey();
        this.baseUrl = baseUrl;
        this.forecastUrl = forecastUrl;
    }

    /**
     * Fetches the API key from the properties file (very secure stuff)
     */
    private static String getApiKey() {
        try {
            Properties props = new Properties();
            props.load(WeatherService.class.getClassLoader().getResourceAsStream("config.properties"));
            String key = props.getProperty("openweather.api.key");
            if (key != null && !key.isEmpty()) {
                return key;
            }
        } catch (Exception e) {
            System.err.println("Please make sure your properties file is set up correctly.");
        }

        throw new RuntimeException(
                "OpenWeather API key not found. Please add to config.properties in the resources folder.");
    }

    /**
     * Fetches current weather for a given latitude and longitude
     *
     * @param lat Latitude of location
     * @param lon Longitude of location
     * @return Weather object with temperature, min/max, and description
     */
    public Weather getWeatherByCoords(double lat, double lon) {
        try {
            // Build the URL for the Current Weather API
            String urlStr = String.format(
                    "%s?lat=%f&lon=%f&units=metric&appid=%s",
                    baseUrl, lat, lon, apiKey);
            URL url = URI.create(urlStr).toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                System.err.println("Error fetching weather: " + conn.getResponseCode());
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null)
                response.append(line);
            in.close();

            // Parse JSON response
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject main = json.getAsJsonObject("main");
            JsonObject weatherObj = json.getAsJsonArray("weather").get(0).getAsJsonObject();

            double temp = main.get("temp").getAsDouble();
            double tempMin = main.get("temp_min").getAsDouble();
            double tempMax = main.get("temp_max").getAsDouble();
            String description = weatherObj.get("description").getAsString();

            return new Weather(temp, tempMin, tempMax, description, null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Weather> getFourDayForecast(double lat, double lon) {
        try {
            String urlStr = String.format(
                    "%s?lat=%f&lon=%f&units=metric&appid=%s",
                    forecastUrl, lat, lon, apiKey);
            URL url = URI.create(urlStr).toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                System.err.println("Error fetching forecast: " + conn.getResponseCode());
                return Collections.emptyList();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null)
                response.append(line);
            in.close();

            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray list = json.getAsJsonArray("list");

            Map<String, List<Double>> tempsByDate = new LinkedHashMap<>();
            Map<String, String> descByDate = new LinkedHashMap<>();

            for (JsonElement elem : list) {
                JsonObject entry = elem.getAsJsonObject();
                JsonObject main = entry.getAsJsonObject("main");
                JsonObject weatherObj = entry.getAsJsonArray("weather").get(0).getAsJsonObject();

                double temp = main.get("temp").getAsDouble();
                String description = weatherObj.get("description").getAsString();
                String date = entry.get("dt_txt").getAsString().split(" ")[0]; // yyyy-MM-dd

                tempsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(temp);
                descByDate.putIfAbsent(date, description);
            }

            List<Weather> forecast = new ArrayList<>();
            int count = 0;
            for (var entry : tempsByDate.entrySet()) {
                if (count >= 4) break;

                String date = entry.getKey();
                List<Double> temps = entry.getValue();
                double avg = temps.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                double min = temps.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                double max = temps.stream().mapToDouble(Double::doubleValue).max().orElse(0);
                String desc = descByDate.get(date);

                forecast.add(new Weather(avg, min, max, desc, date));
                count++;
            }

            return forecast;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
