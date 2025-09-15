package seng202.team5.gui;

public class ViewTrailController extends Controller {

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
     */
    public ViewTrailController(ScreenNavigator navigator) {
        super(navigator);
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
