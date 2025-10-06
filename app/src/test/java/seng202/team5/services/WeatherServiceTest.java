package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

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



}
