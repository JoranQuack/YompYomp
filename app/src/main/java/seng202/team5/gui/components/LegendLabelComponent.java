package seng202.team5.gui.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

import java.io.IOException;

public class LegendLabelComponent extends HBox {

    @FXML
    private Label colourLabel;
    @FXML
    private Label tagLabel;

    /**
     * Initializes the label with a given tag and colour code
     *
     * @param hexString
     * @param tag
     */
    public LegendLabelComponent(String hexString, String tag) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/legend_label.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        colourLabel.setBackground(Background.fill(Paint.valueOf(hexString)));
        tagLabel.setText(tag);
    }
}
