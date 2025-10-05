package seng202.team5.gui.components;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import seng202.team5.data.DatabaseService;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import seng202.team5.services.ImageService;
import seng202.team5.services.LogService;
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
    @FXML
    private VBox infoContainer;
    @FXML
    private StackPane matchContainer;
    @FXML
    private ImageView starIcon;
    @FXML
    private Label starLabel;
    @FXML
    private ImageView bookmark;
    @FXML
    private ImageView bookmarkFill;
    @FXML
    private ImageView trashIcon;

    private Trail trail;
    private TrailLog trailLog;

    private Consumer<Trail> onBookmarkClickedHandler;
    private Consumer<Trail> onBookmarkFillClickedHandler;
    private Consumer<TrailLog> onTrashClickedHandler;

    private final ImageService imageService;

    private boolean isUnmatched;
    private boolean logMode;
    private boolean inLogBook = false;
    private boolean bookmarked = false;

    /**
     * Constructor for the trail card component.
     * @param isUnmatched
     * @param logMode
     */
    public TrailCardComponent(boolean isUnmatched, boolean logMode) {
        this.isUnmatched = isUnmatched;
        this.logMode = logMode;
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
     * Log trail card constructor.
     *
     */
    public TrailCardComponent() {
        this.isUnmatched = false;
        this.logMode = true;
        this.inLogBook = true;
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

    public void setTrail(Trail trail) {
        this.trail = trail;
    }

    public void setOnBookmarkClickedHandler(Consumer<Trail> handler) {
        this.onBookmarkClickedHandler = handler;
    }

    public void setOnBookmarkFillClickedHandler(Consumer<Trail> handler) {
        this.onBookmarkFillClickedHandler = handler;
    }

    public void setOnTrashClickedHandler(Consumer<TrailLog> handler) {
        this.onTrashClickedHandler = handler;
    }

    @FXML
    private void onBookmarkClicked(MouseEvent event) {
        event.consume();
        if (onBookmarkClickedHandler != null && trail != null) {
            onBookmarkClickedHandler.accept(trail);
        }
    }

    @FXML
    private void onBookmarkFillClicked(MouseEvent event) {
        event.consume();
        if (onBookmarkFillClickedHandler != null && trail != null) {
            onBookmarkFillClickedHandler.accept(trail);
        }
    }

    @FXML
    private void onTrashIconClicked(MouseEvent event) {
        event.consume();
        if (onTrashClickedHandler != null && trailLog != null) {
            onTrashClickedHandler.accept(trailLog);
        }
    }

    public void setBookmarked(boolean value) {
        bookmarked = value;
        updateBookmarkIcon();
    }

    public void updateBookmarkIcon() {
        if (bookmarked) {
            bookmarkFill.setVisible(true);
            bookmark.setVisible(false);
        } else {
            bookmarkFill.setVisible(false);
            bookmark.setVisible(true);
        }
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
        this.trail = trail;
        this.trailLog = log;
        bookmark.setVisible(true);
        bookmarkFill.setVisible(false);

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
        if (trail.getRegion() != null) {
            regionLabel.setText(trail.getRegion());
        } else {
            regionLabel.setVisible(false);
        }

        if (!logMode) {
            LogService logService = new LogService(new DatabaseService());
            boolean isLogged = logService.isTrailLogged(trail.getId());
            bookmark.setVisible(!isLogged);
            bookmarkFill.setVisible(isLogged);

            starLabel.setVisible(false);
            starIcon.setVisible(false);
            trashIcon.setVisible(false);

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
        }
        else if (logMode) {
            matchBar.setVisible(false);
            matchLabel.setVisible(false);
            typeLabel.setVisible(false);
            trashIcon.setVisible(false);

            starLabel.setVisible(true);
            starIcon.setVisible(true);
            if (log.getRating() != null) {
                starLabel.setText(String.valueOf(log.getRating()));
            } else {
                starLabel.setText("Rate Me!");
            }

            if (log.getPerceivedDifficulty() != null) {
                difficultyLabel.setText(StringManipulator.capitaliseFirstLetter(log.getPerceivedDifficulty()));
            } else {
                attributesFlowPane.getChildren().remove(difficultyLabel);
            }

            if (inLogBook) {
               trashIcon.setVisible(true);
               bookmark.setVisible(false);
               bookmarkFill.setVisible(false);
            }

            // durationLabel.setText(StringManipulator.capitaliseFirstLetter();
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

    public ImageView getTrashIcon() {
        return trashIcon;
    }
}
