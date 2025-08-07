package seng202.team5.gui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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

    @FXML
    private Label resultsLabel;

    @FXML
    private ChoiceBox<String> pageChoiceBox;

    protected TrailsController(seng202.team5.Environment Environment) {
        super(Environment);
    }

    @FXML
    private void initialize() {
        List<Trail> trails = searchService.searchTrails(null, 0);
        initializePageChoiceBox();
        updateTrailsGrid(trails);
        resultsLabel.setText(trails.size() + "/" + searchService.getNumberOfTrails() + " trails loaded");
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
            Hyperlink link = new Hyperlink("Link");
            link.setOnAction(event -> {
                // Open the trail's webpage URL in a browser
                getEnvironment().getNavigator().openWebPage(trail.getWebpageURL());
            });
            trailsGridPane.addRow(i + 1,
                    new Label(trail.getName()),
                    new ImageView(new Image(trail.getThumbnailURL())),
                    // new Label(trail.getThumbnailURL()),
                    new TextFlow(new Text(trail.getDescription())),
                    new Label(trail.getDifficulty()),
                    new Label(trail.getCompletionTime()),
                    link);
        }

    }

    private void initializePageChoiceBox() {
        for (int i = 0; i < searchService.getNumberOfPages(null); i++) {
            pageChoiceBox.getItems().add(String.valueOf(i + 1));
        }
        pageChoiceBox.setValue("1");
        pageChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onPageSelected();
        });
    }

    @FXML
    private void onSearchButtonClicked() {
        String query = searchBarTextField.getText();
        int page = Integer.parseInt(pageChoiceBox.getValue()) - 1;
        List<Trail> filteredTrails = searchService.searchTrails(query, page);
        updateTrailsGrid(filteredTrails);
    }

    @FXML
    private void onPageSelected() {
        String query = searchBarTextField.getText();
        int page = Integer.parseInt(pageChoiceBox.getValue()) - 1;
        List<Trail> filteredTrails = searchService.searchTrails(query, page);
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
