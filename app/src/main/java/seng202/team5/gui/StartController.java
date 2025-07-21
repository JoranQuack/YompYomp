package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StartController extends Controller {
    protected StartController(seng202.team5.Environment Environment) {
        super(Environment);
    }

    @FXML
    private Label yompLabel;

    @FXML
    private void onYompButtonClicked() {
        yompLabel.setText("Yomp" + yompLabel.getText());
    }

    @FXML
    private void initialize() {
        // TODO: Initialise any necessary components or data here
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/start.fxml";
    }

    @Override
    protected String getTitle() {
        return "Start Screen";
    }
}
