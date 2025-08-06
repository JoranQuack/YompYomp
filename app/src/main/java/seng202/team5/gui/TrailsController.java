package seng202.team5.gui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
// import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import seng202.team5.models.Trail;

public class TrailsController extends Controller {

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchBarTextField;

    @FXML
    private GridPane trailsGridPane;

    protected TrailsController(seng202.team5.Environment Environment) {
        super(Environment);
    }

    @FXML
    private void initialize() {
        List<Trail> trails = getEnvironment().getTrails();

        // Populate the trails grid with trail data
        for (int i = 0; i < trails.size(); i++) {
            Trail trail = trails.get(i);
            trailsGridPane.addRow(i + 1,
                    new Label(trail.getName()),
                    new Label(trail.getThumbnailURL()),
                    new Label(trail.getDescription()),
                    new Label(trail.getDifficulty()),
                    new Label(trail.getCompletionTime()),
                    new Label(trail.getWebpageURL()));
        }
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/trails.fxml";
    }

    @Override
    protected String getTitle() {
        return "Trails Screen";
    }
}
