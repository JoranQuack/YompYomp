package seng202.team5.gui.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LegendLabelComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        colourLabel.setBackground(Background.fill(Paint.valueOf(hexString)));
        tagLabel.setText(tag);
    }
}
