package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Weather;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

class WeatherServiceTest {

    private WeatherService weatherService;
    private HttpURLConnection mockConnection;
    private URL mockUrl;
    private URI mockUri;

    @BeforeEach
    void setUp() throws Exception {
        weatherService = new WeatherService("https://fake-base-url", "https://fake-forecast-url");
        mockConnection = mock(HttpURLConnection.class);
        mockUrl = mock(URL.class);
        mockUri = mock(URI.class);
    }

    @Test
    @DisplayName("getWeatherByCoords returns correct Weather object using fake JSON")
    void testGetWeatherByCoords_ValidResponse() throws Exception {
        String fakeJson = """
                {
                  "main": { "temp": 15.0, "temp_min": 15.0, "temp_max": 15.0 },
                  "weather": [ { "description": "clear sky", "icon": "01d" } ]
                }
                """;

        when(mockUrl.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(
                new ByteArrayInputStream(fakeJson.getBytes()));

        URI mockUri = mock(URI.class);
        when(mockUri.toURL()).thenReturn(mockUrl);

        try (MockedStatic<URI> uriMock = mockStatic(URI.class)) {
            uriMock.when(() -> URI.create(anyString())).thenReturn(mockUri);

            Weather result = weatherService.getWeatherByCoords(-43.5320, 172.6362);

            assertNotNull(result);
            assertEquals(15.0, result.getTemperature());
            assertEquals(15.0, result.getTempMin());
            assertEquals(15.0, result.getTempMax());
            assertEquals("clear sky", result.getDescription());
        }
    }

    @Test
    @DisplayName("getWeatherByCoords should return null when response code != 200")
    void testGetWeatherByCoords_BadResponseCode() throws Exception {
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(404);
        when(mockUri.toURL()).thenReturn(mockUrl);

        try (MockedStatic<URI> uriMock = mockStatic(URI.class)) {
            uriMock.when(() -> URI.create(anyString())).thenReturn(mockUri);

            Weather weather = weatherService.getWeatherByCoords(-43.5320, 172.6362);
            assertNull(weather);
        }
    }

    @Test
    @DisplayName("getFourDayForecast parses four days correctly from valid JSON")
    void testGetFourDayForecast_ValidResponse() throws Exception {
        String jsonResponse = """
                {
                  "list": [
                    {"dt_txt": "2025-10-09 12:00:00", "main": {"temp": 12}, "weather": [{"description": "today", "icon": "01d"}]},
                    {"dt_txt": "2025-10-10 12:00:00", "main": {"temp": 14}, "weather": [{"description": "cloudy", "icon": "02d"}]},
                    {"dt_txt": "2025-10-10 18:00:00", "main": {"temp": 16}, "weather": [{"description": "cloudy", "icon": "02d"}]},
                    {"dt_txt": "2025-10-11 12:00:00", "main": {"temp": 18}, "weather": [{"description": "sunny", "icon": "01d"}]},
                    {"dt_txt": "2025-10-12 12:00:00", "main": {"temp": 20}, "weather": [{"description": "rainy", "icon": "10d"}]},
                    {"dt_txt": "2025-10-13 12:00:00", "main": {"temp": 22}, "weather": [{"description": "windy", "icon": "03d"}]},
                    {"dt_txt": "2025-10-14 12:00:00", "main": {"temp": 24}, "weather": [{"description": "foggy", "icon": "50d"}]}
                  ]
                }
                """;

        when(mockUrl.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(jsonResponse.getBytes()));

        URI mockUri = mock(URI.class);
        when(mockUri.toURL()).thenReturn(mockUrl);

        try (MockedStatic<URI> uriMock = mockStatic(URI.class)) {
            uriMock.when(() -> URI.create(anyString())).thenReturn(mockUri);

            List<Weather> forecast = weatherService.getFourDayForecast(-43.5320, 172.6362);

            assertEquals(4, forecast.size());
            assertEquals("2025-10-11", forecast.get(0).getDate());
            assertEquals("sunny", forecast.get(0).getDescription());
            assertEquals("foggy", forecast.get(3).getDescription());
        }
    }

    @Test
    @DisplayName("getFourDayForecast returns empty list when API responds with error code")
    void testGetFourDayForecast_ErrorResponse() throws Exception {
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(500);

        URI mockUri = mock(URI.class);
        when(mockUri.toURL()).thenReturn(mockUrl);

        try (MockedStatic<URI> uriMock = mockStatic(URI.class)) {
            uriMock.when(() -> URI.create(anyString())).thenReturn(mockUri);

            List<Weather> result = weatherService.getFourDayForecast(0, 0);
            assertTrue(result.isEmpty());
        }
    }

}
