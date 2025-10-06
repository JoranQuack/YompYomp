package seng202.team5.services;

import io.cucumber.java.Before;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.HttpURLConnection;

public class WeatherAPITest {
    @Mock
    private WeatherAPI weatherAPI;

    @Mock
    private HttpURLConnection mockConnection;

    @BeforeEach
    public void setup() {
        WeatherAPI weatherAPI = new WeatherAPI();
    }
}
