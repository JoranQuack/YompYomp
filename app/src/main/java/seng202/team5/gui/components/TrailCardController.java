package seng202.team5.gui.components;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import seng202.team5.models.Trail;
import seng202.team5.services.ImageService;

public class TrailCardController extends VBox {

    // UI Components
    @FXML
    private ImageView thumbnail;
    @FXML
    private Label title;
    @FXML
    private ProgressBar matchBar;
    @FXML
    private Label matchLabel;
    @FXML
    private Label difficultyLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label regionLabel;

    private final ImageService imageService;

    public TrailCardController() {
        this.imageService = new ImageService();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/trail_card.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this); // FXML elements
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } // If not work, crash?
    }

    /**
     * Sets the trail data for this card component.
     *
     * @param trail The trail object to display
     */
    public void setData(Trail trail) {
        title.setText(trail.getName());

        Image trailImage = imageService.loadTrailImage(trail.getThumbnailURL());
        thumbnail.setImage(trailImage);

        difficultyLabel.setText(trail.getDifficulty());
        durationLabel.setText(trail.getType());
        regionLabel.setText("Region");
        double weight = trail.getUserWeight();
        matchBar.setProgress(weight);
        int matchPercent = (int) Math.round(weight * 100);
        matchLabel.setText(matchPercent + "% match");
    }
}
