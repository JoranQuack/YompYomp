package seng202.team5.gui.components;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
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
    private ImageView starIcon;
    @FXML
    private Label starLabel;
    @FXML
    private ImageView bookmark;
    @FXML
    private ImageView bookmarkFill;


    private final ImageService imageService;

    private boolean isUnmatched;
    private boolean logMode;

    public TrailCardComponent(boolean isUnmatched, boolean logMode) {
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
     * Sets the trail or log data for this card component.
     *
     * @param trail The trail object to display
     * @param log The log object to display
     */
    public void setData(Trail trail, TrailLog log) {
        //these are being set to not visible as they havent been implemented
        //TODO get rid of and implement the bookmarks to be shown
        bookmark.setVisible(true);
        bookmarkFill.setVisible(false);

        resetComponentVisibility();

        title.setText(trail.getName());

        Image trailImage = imageService.loadTrailImage(trail.getThumbnailURL());
        thumbnail.setImage(trailImage);

        if (trail.getRegion() != null) {
            regionLabel.setText(trail.getRegion());
        } else {
            regionLabel.setVisible(false);
        }

        if (!logMode) {
            starLabel.setVisible(false);
            starIcon.setVisible(false);
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

            if (isUnmatched) {
                matchBar.setVisible(false);
                matchLabel.setVisible(false);
            } else {
                updateMatchBar(trail);
            }
        }
        else {
            matchBar.setVisible(false);
            matchLabel.setVisible(false);
            typeLabel.setVisible(false);
            starLabel.setVisible(true);
            starIcon.setVisible(true);
            starLabel.setText(String.valueOf(log.getRating()));

            difficultyLabel.setText(StringManipulator.capitaliseFirstLetter(log.getPerceivedDifficulty()));
            //TODO implement the label for the duration when the model is updated
            //durationLabel.setText(StringManipulator.capitaliseFirstLetter())
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
