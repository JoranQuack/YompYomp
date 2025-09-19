package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import seng202.team5.models.Trail;
import seng202.team5.services.ImageService;

/**
 * Controller for the view trail screen
 */
public class ViewTrailController extends Controller {

    private final ImageService imageService;
    private Trail trail;

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
     * @param trail trail object to be displayed on screen
     */
    public ViewTrailController(ScreenNavigator navigator, Trail trail) {
        super(navigator);
        this.imageService = new ImageService();
        this.trail = trail;
    }

    @FXML
    private Label trailNameLabel;
    @FXML
    private Label translationLabel;
    @FXML
    private Label matchLabel;
    @FXML
    private ProgressBar matchBar;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button editInfoButton;
    @FXML
    private ImageView trailMap;
    @FXML
    private ImageView trailThumbnail;
    @FXML
    private Button backButton;

    /**
     * Initialises the view trail screen with data retrieved from database
     */
    @FXML
    private void initialize() {
        trailNameLabel.setText(trail.getName());
        Image trailImage = imageService.loadTrailImage(trail.getThumbnailURL());
        trailThumbnail.setImage(trailImage);
        double weight = trail.getUserWeight();
        matchBar.setProgress(weight);
        int matchPercent = (int) Math.round(weight * 100);
        matchLabel.setText(matchPercent + "% match");
        descriptionLabel.setText(trail.getDescription());
        backButton.setOnAction(e -> onBackButtonClicked());
        editInfoButton.setOnAction(e -> onEditInfoButtonClicked());
        if (!trail.getTranslation().isEmpty()) {
            translationLabel.setText(trail.getTranslation());
            translationLabel.setVisible(true);
        } else {
            translationLabel.setVisible(false);
        }
        // TODO: add icon for map
    }

    @FXML
    private void onBackButtonClicked() {
        Controller lastController = super.getNavigator().getLastController();
        super.getNavigator().launchScreen(lastController, null);
    }

    @FXML
    private void onEditInfoButtonClicked() {
        super.getNavigator().launchScreen(new ModifyTrailController(super.getNavigator(), trail, this), null);
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/view_trail_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "View Trail Screen";
    }

    /**
     * Handles the case where the View Trail Screen fails to load
     * by displaying an alert to the user
     * @param e the exception that occurred while loading the trail screen
     */
    @Override
    public void onLoadFailed(Exception e) {
        showAlert(Alert.AlertType.ERROR, "Trail Card Failed To Load", "Loading selected trail failed, please close the application and try again.");
    }
}
