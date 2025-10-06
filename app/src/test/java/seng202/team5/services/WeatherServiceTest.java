package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.net.HttpURLConnection;

public class WeatherServiceTest {
    @Mock
    private WeatherService weatherService;

    @Mock
    private HttpURLConnection mockConnection;

    @BeforeEach
    public void setup() {
        //WeatherService weatherService = new WeatherService(); TODO    change for new constructor
    }
}
