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
import seng202.team5.utils.CompletionTimeParser;

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
    @FXML
    private Label typeLabel;

    private final ImageService imageService;

    private boolean isUnmatched;

    public TrailCardController(boolean isUnmatched) {
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
     * Capitalise the first letter of a string.
     *
     * @param str The input string
     * @return The capitalised string
     */
    private String capitaliseFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Completion time formatting.
     *
     * @param trail
     */
    private String formatCompletionTime(Trail trail) {
        if (trail.getMinCompletionTimeMinutes() == trail.getMaxCompletionTimeMinutes()) {
            return capitaliseFirstLetter(
                    CompletionTimeParser.formatMinutesToString(trail.getMinCompletionTimeMinutes()));
        }
        return capitaliseFirstLetter(CompletionTimeParser.formatMinutesToString(trail.getMinCompletionTimeMinutes()))
                + " - "
                + capitaliseFirstLetter(
                        CompletionTimeParser.formatMinutesToString(trail.getMaxCompletionTimeMinutes()));
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

        regionLabel.setVisible(false); // Hide region label for now

        if (!trail.getDifficulty().contains("unknown")) {
            difficultyLabel.setText(capitaliseFirstLetter(trail.getDifficulty()));
        } else {
            difficultyLabel.setVisible(false);
        }

        if (trail.getMinCompletionTimeMinutes() != 0) {
            durationLabel.setText(formatCompletionTime(trail));
        } else {
            durationLabel.setVisible(false);
        }

        if (!trail.getCompletionType().contains("unknown")) {
            typeLabel.setText(capitaliseFirstLetter(trail.getCompletionType()));
        } else {
            typeLabel.setVisible(false);
        }

        if (isUnmatched) {
            matchBar.setVisible(false);
            matchLabel.setVisible(false);
        } else {
            updateMatchBar(trail);
        }
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
