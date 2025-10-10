package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seng202.team5.models.Weather;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
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
    void setUp() {
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
            assertEquals(15.0, result.temperature());
            assertEquals(15.0, result.tempMin());
            assertEquals(15.0, result.tempMax());
            assertEquals("clear sky", result.description());
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
        // Use dynamic dates relative to tomorrow
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String jsonResponse = getString(tomorrow);

        when(mockUrl.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(jsonResponse.getBytes()));

        URI mockUri = mock(URI.class);
        when(mockUri.toURL()).thenReturn(mockUrl);

        try (MockedStatic<URI> uriMock = mockStatic(URI.class)) {
            uriMock.when(() -> URI.create(anyString())).thenReturn(mockUri);

            List<Weather> forecast = weatherService.getFourDayForecast(-43.5320, 172.6362);

            assertEquals(4, forecast.size());
            assertEquals(tomorrow.toString(), forecast.get(0).date());
            assertEquals("sunny", forecast.get(0).description());
            assertEquals("foggy", forecast.get(3).description());
        }
    }

    private static String getString(LocalDate tomorrow) {
        LocalDate dayAfter = tomorrow.plusDays(1);
        LocalDate dayAfter2 = tomorrow.plusDays(2);
        LocalDate dayAfter3 = tomorrow.plusDays(3);
        LocalDate dayAfter4 = tomorrow.plusDays(4);

        return String.format(
                """
                        {
                          "list": [
                            {"dt_txt": "%s 12:00:00", "main": {"temp": 18}, "weather": [{"description": "sunny", "icon": "01d"}]},
                            {"dt_txt": "%s 12:00:00", "main": {"temp": 20}, "weather": [{"description": "rainy", "icon": "10d"}]},
                            {"dt_txt": "%s 12:00:00", "main": {"temp": 22}, "weather": [{"description": "windy", "icon": "03d"}]},
                            {"dt_txt": "%s 12:00:00", "main": {"temp": 24}, "weather": [{"description": "foggy", "icon": "50d"}]},
                            {"dt_txt": "%s 12:00:00", "main": {"temp": 26}, "weather": [{"description": "clear", "icon": "01d"}]}
                          ]
                        }
                        """,
                tomorrow, dayAfter, dayAfter2, dayAfter3, dayAfter4);
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
