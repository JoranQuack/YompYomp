package seng202.team5.gui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import seng202.team5.models.Trail;
import seng202.team5.services.SearchService;

public class TrailsController extends Controller {
    private SearchService searchService = new SearchService();

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
        List<Trail> trails = searchService.searchTrails(null);
        updateTrailsGrid(trails);
    }

    private void clearTrailsGrid() {
        trailsGridPane.getChildren().clear();
        trailsGridPane.addRow(0, new Label("Name"), new Label("Thumbnail URL"), new Label("Description"),
                new Label("Difficulty"), new Label("Completion Time"), new Label("Webpage URL"));
    }

    private void updateTrailsGrid(List<Trail> trails) {
        clearTrailsGrid();
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

    @FXML
    private void onSearchButtonClicked() {
        String query = searchBarTextField.getText();
        List<Trail> filteredTrails = searchService.searchTrails(query);
        updateTrailsGrid(filteredTrails);
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
