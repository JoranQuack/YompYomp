package seng202.team5.gui;

import seng202.team5.Environment;

public class SetupController extends Controller {
    public SetupController(Environment Environment, ScreenNavigator navigator) {
        super(Environment, navigator);
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
