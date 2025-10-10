package seng202.team5.gui.components;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import seng202.team5.gui.ScreenNavigator;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import seng202.team5.services.ImageService;
import seng202.team5.utils.CompletionTimeParser;
import seng202.team5.utils.StringManipulator;

public class TrailCardComponent extends VBox {

    // UI Components
    @FXML
    private VBox trailCardContainer;
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
    private FlowPane ratingFlowPane;
    @FXML
    private Label difficultyLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private HBox thumbnailContainer;
    @FXML
    private VBox infoContainer;
    @FXML
    private StackPane matchContainer;

    private final ImageService imageService;
    private final ScreenNavigator navigator;

    private final boolean isUnmatched;
    private final boolean logMode;
    private final boolean isSingle;

    public TrailCardComponent(boolean isUnmatched, boolean isSingle, boolean logMode, ScreenNavigator navigator) {
        this.isUnmatched = isUnmatched;
        this.isSingle = isSingle;
        this.logMode = logMode;
        this.imageService = new ImageService();
        this.navigator = navigator;

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
     * @param trail the trail to format
     */
    private String formatCompletionTime(Trail trail) {
        return CompletionTimeParser.formatTimeRange(trail.getMinCompletionTimeMinutes(),
                trail.getMaxCompletionTimeMinutes());
    }

    /**
     * Sets the trail or log data for this card component.
     *
     * @param trail The trail object to display
     * @param log   The log object to display
     */
    public void setData(Trail trail, TrailLog log) {
        resetComponentVisibility();

        if (!isSingle) {
            trailCardContainer.getStyleClass().add("hoverable");
        }

        title.setText(trail.getName());
        title.setWrapText(isSingle);

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
        if (trail.getRegion() != null) {
            regionLabel.setText(trail.getRegion());
        } else {
            regionLabel.setVisible(false);
        }

        if (!logMode) {
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
                infoContainer.getChildren().remove(matchContainer);
            } else {
                updateMatchBar(trail);
            }

            ratingFlowPane.getStyleClass().clear();

        } else {
            matchBar.setVisible(false);
            matchLabel.setVisible(false);
            typeLabel.setVisible(false);

            if (log.getPerceivedDifficulty() != null) {
                difficultyLabel.setText(StringManipulator.capitaliseFirstLetter(log.getPerceivedDifficulty()));
            } else {
                attributesFlowPane.getChildren().remove(difficultyLabel);
            }

            if (log.getCompletionTime() != null) {
                durationLabel.setText(log.getCompletionTime() + " " + log.getTimeUnit());
            } else {
                attributesFlowPane.getChildren().remove(durationLabel);
            }

            fillStarRating(log);
        }

        if (isSingle)
            setSingleProperties(trail);

    }

    /**
     * Sets properties specific to single trail card view.
     */
    private void setSingleProperties(Trail trail) {
        trailCardContainer.getStyleClass().add("single");
        infoContainer.setPadding(new Insets(0, 0, 0, 0));
        Label description = new Label(trail.getDescription());
        description.setWrapText(true);
        infoContainer.getChildren().add(3, description);
        VBox.setMargin(description, new Insets(0, 0, 10, 0));
        if (trail.getCultureUrl() != null && !trail.getCultureUrl().isEmpty()) {
            Hyperlink culturalUrl = new Hyperlink("Cultural Information");
            culturalUrl.setOnAction(e -> navigator.openWebPage((trail.getCultureUrl())));
            infoContainer.getChildren().add(4, culturalUrl);
            VBox.setMargin(culturalUrl, new Insets(0, 0, 10, 0));
        }

        if (trail.getTranslation() != null && !trail.getTranslation().isEmpty()) {
            infoContainer.getChildren().add(1, new Label(trail.getTranslation()));
        }
    }

    /**
     * Resets component visibility states for proper reuse.
     */
    private void resetComponentVisibility() {
        attributesFlowPane.getChildren().clear();
        attributesFlowPane.getChildren().addAll(difficultyLabel, durationLabel, typeLabel);

        // Ensure matchContainer is in the infoContainer
        if (!infoContainer.getChildren().contains(matchContainer)) {
            infoContainer.getChildren().add(matchContainer);
        }
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

    /** Fills the star rating content */
    private void fillStarRating(TrailLog log) {
        infoContainer.getChildren().remove(matchContainer);
        ratingFlowPane.getChildren().clear();
        ratingFlowPane.getStyleClass().add("darken");
        ImageView star = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/star_icon.png"))));
        star.setFitWidth(20);
        star.setFitHeight(20);
        ratingFlowPane.getChildren().add(star);
        int rating = log.getRating();
        Label label = new Label(String.valueOf(rating));
        label.getStyleClass().add("text-light");
        label.getStyleClass().add("heading");
        ratingFlowPane.getChildren().add(label);
    }
}
