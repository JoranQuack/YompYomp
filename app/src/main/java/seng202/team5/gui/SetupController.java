package seng202.team5.gui;

public class SetupController extends Controller {
    public SetupController(ScreenNavigator navigator) {
        super(navigator);
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/setup.fxml";
    }

    @Override
    protected String getTitle() {
        return "Setup";
    }
}
