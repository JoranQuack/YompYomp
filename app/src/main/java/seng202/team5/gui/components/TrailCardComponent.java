package seng202.team5.gui.components;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import seng202.team5.models.Trail;
import seng202.team5.services.ImageService;
import seng202.team5.utils.CompletionTimeParser;
import seng202.team5.utils.StringManipulator;

public class TrailCardComponent extends VBox {

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
    private Label regionLabel;
    @FXML
    private FlowPane attributesFlowPane;
    @FXML
    private Label difficultyLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private HBox thumbnailContainer;

    private final ImageService imageService;

    private boolean isUnmatched;

    public TrailCardComponent(boolean isUnmatched) {
        this.isUnmatched = isUnmatched;
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
     * Completion time formatting.
     *
     * @param trail
     */
    private String formatCompletionTime(Trail trail) {
        if (trail.getMinCompletionTimeMinutes() == trail.getMaxCompletionTimeMinutes()) {
            return StringManipulator.capitaliseFirstLetter(
                    CompletionTimeParser.formatMinutesToString(trail.getMinCompletionTimeMinutes()));
        }
        return StringManipulator.capitaliseFirstLetter(
                CompletionTimeParser.formatMinutesToString(trail.getMinCompletionTimeMinutes()))
                + " - "
                + StringManipulator.capitaliseFirstLetter(
                        CompletionTimeParser.formatMinutesToString(trail.getMaxCompletionTimeMinutes()));
    }

    /**
     * Sets the trail data for this card component.
     *
     * @param trail The trail object to display
     */
    public void setData(Trail trail) {
        resetComponentVisibility();

        title.setText(trail.getName());

        Image trailImage = imageService.loadTrailImage(trail.getThumbnailURL());
        thumbnail.setImage(trailImage);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);

        clip.widthProperty().bind(thumbnailContainer.widthProperty());
        clip.heightProperty().bind(thumbnailContainer.heightProperty());

        thumbnail.setClip(clip);

        if (!trail.getDifficulty().contains("unknown")) {
            difficultyLabel.setText(StringManipulator.capitaliseFirstLetter(trail.getDifficulty()));
        } else {
            attributesFlowPane.getChildren().remove(difficultyLabel);
        }

        if (trail.getMinCompletionTimeMinutes() != 0) {
            durationLabel.setText(formatCompletionTime(trail));
        } else {
            attributesFlowPane.getChildren().remove(durationLabel);
        }

        if (!trail.getCompletionType().contains("unknown")) {
            typeLabel.setText(StringManipulator.capitaliseFirstLetter(trail.getCompletionType()));
        } else {
            attributesFlowPane.getChildren().remove(typeLabel);
        }

        if (trail.getRegion() != null) {
            regionLabel.setText(trail.getRegion());
        } else {
            regionLabel.setVisible(false);
        }

        if (isUnmatched) {
            matchBar.setVisible(false);
            matchLabel.setVisible(false);
        } else {
            updateMatchBar(trail);
        }
    }

    /**
     * Resets component visibility states for proper reuse.
     */
    private void resetComponentVisibility() {
        attributesFlowPane.getChildren().clear();
        attributesFlowPane.getChildren().addAll(difficultyLabel, durationLabel, typeLabel);
        matchBar.setVisible(true);
        matchLabel.setVisible(true);
    }

    /**
     * Updates the match bar based on the trail's user weight.
     *
     * @param trail The trail object
     */
    private void updateMatchBar(Trail trail) {
        double weight = trail.getUserWeight();
        matchBar.setProgress(weight);
        int matchPercent = (int) Math.round(weight * 100);
        matchLabel.setText(matchPercent + "% match");
    }
}
