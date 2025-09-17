package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import seng202.team5.models.Trail;
import seng202.team5.services.ImageService;

public class ViewTrailController extends Controller {

    private final ImageService imageService;
    private Trail trail;

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
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
    // hut labels and images currently invisible

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
        // TODO: add icon for map
        // TODO: show completion time
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
}
