package seng202.team5.services;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.mockito.Mockito.*;

public class WeatherServiceTest {
    @Mock
    private WeatherService weatherService;

    @Mock
    private HttpURLConnection mockConnection;

    @Mock
    private URL mockURL;

    @BeforeEach
    public void setup() {
        weatherService = new WeatherService("https://fake-weather-url", "https://fake-forecast-url");
        mockConnection = mock(HttpURLConnection.class);
        mockURL = mock(URL.class);
    }

    @Test
    @DisplayName("getWeatherByCoords returns correct Weather on valid JSON")
    public void getWeatherByCoords() throws IOException {
        String jsonResponse = """
        {
          "main": {"temp": 20.0, "temp_min": 15.0, "temp_max": 25.0},
          "weather": [{"description": "clear sky"}]
        } 
        """;

        when(mockURL.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(jsonResponse.getBytes()));

        try (var uriMock = mockStatic(java.net.URI.class)) {
            uriMock.when(() -> java.net.URI.create(anyString())).thenReturn(mock(java.net.URI.class));
        }
    }



}
