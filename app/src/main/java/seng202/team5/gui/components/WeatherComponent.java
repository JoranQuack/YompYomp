package seng202.team5.gui.components;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import seng202.team5.models.Weather;
import seng202.team5.utils.WeatherFormatter;

public class WeatherComponent extends VBox {
    @FXML
    private Label maxTempLabel;

    @FXML
    private Label minTempLabel;

    @FXML
    private ImageView weatherIcon;

    @FXML
    private Label dayLabel;

    @FXML
    private Label dateLabel;

    public WeatherComponent(Weather weather) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/weather_component.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        populateWeatherData(weather);
    }

    private void populateWeatherData(Weather weather) {
        maxTempLabel.setText(String.format("%.1f°C", weather.tempMax()));
        minTempLabel.setText(String.format("%.1f°C", weather.tempMin()));
        weatherIcon.setImage(WeatherFormatter.getWeatherIcon(weather.iconType()));
        dayLabel.setText(WeatherFormatter.getDayOfWeek(weather.date()));
        dateLabel.setText(WeatherFormatter.formatDate(weather.date()));
    }
}
