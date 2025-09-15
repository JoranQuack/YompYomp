package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

public class ViewTrailController extends Controller {

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
     */
    public ViewTrailController(ScreenNavigator navigator) {
        super(navigator);
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
    // hut labels and images currently invisible

    @Override
    protected String getFxmlFile() {
        return "/fxml/view_trail_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "View Trail Screen";
    }
}
