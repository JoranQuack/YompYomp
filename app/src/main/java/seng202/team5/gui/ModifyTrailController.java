package seng202.team5.gui;

import seng202.team5.models.Trail;
import seng202.team5.services.ImageService;

public class ModifyTrailController extends Controller {

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
     */
    public ModifyTrailController(ScreenNavigator navigator) {
        super(navigator);
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/modify_trail_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "Modify Trail Screen";
    }
}
