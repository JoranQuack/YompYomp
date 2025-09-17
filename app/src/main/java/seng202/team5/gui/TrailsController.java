package seng202.team5.gui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.exceptions.LoadingTrailsFailedException;
import seng202.team5.gui.components.NavbarController;
import seng202.team5.gui.components.TrailCardController;
import seng202.team5.models.Trail;
import seng202.team5.services.SearchService;

/**
 * Controller for the trails display screen.
 * Handles trail search, pagination, and card display.
 */
public class TrailsController extends Controller {
    /** Service for searching and filtering trails */
    private SearchService searchService;

    @FXML
    private VBox navbarContainer;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchBarTextField;

    @FXML
    private FlowPane trailsContainer;

    @FXML
    private Label resultsLabel;

    @FXML
    private ChoiceBox<String> pageChoiceBox;

    /**
     * Default constructor required by JavaFX FXML loading.
     */
    public TrailsController() {
        super();
    }

    /**
     * Creates controller with navigator.
     *
     * @param navigator Screen navigator
     */
    public TrailsController(ScreenNavigator navigator) {
        super(navigator);
        initializeSearchService();
    }

    /**
     * Initializes the search service.
     */
    private void initializeSearchService() {
        this.searchService = new SearchService(new SqlBasedTrailRepo(new DatabaseService()));
    }

    /**
     * Initializes the trails view with default data.
     */
    @FXML
    private void initialize() {
        // Initialize the navbar from parent coz you can't have your kid running around
        NavbarController navbar = super.getNavbarController();
        navbar.setPage(1);
        navbarContainer.getChildren().add(navbar);

        // Initialize search service if not already done
        if (searchService == null) {
            initializeSearchService();
        }

        // Check if searchService is still null and handle gracefully
        if (searchService == null) {
            resultsLabel.setText("No trails available");
            return;
        }

        try {
            List<Trail> trails = searchService.getTrails(null, 0);
            initializePageChoiceBox();
            updateTrailsDisplay(trails);
            resultsLabel.setText(trails.size() + "/" + searchService.getNumberOfTrails() + " trails loaded");
        } catch (LoadingTrailsFailedException e) {
            showAlert(Alert.AlertType.ERROR, "Loading Trails Failed", "Failed to load trails, please close the application and try again");
            super.getNavigator().launchScreen(new DashboardController(super.getNavigator())); //TODO this should take user to respective dashboard screen
        }


    }

    /**
     * Updates display with trail cards.
     *
     * @param trails List of trails to display
     */
    private void updateTrailsDisplay(List<Trail> trails) {
        trailsContainer.getChildren().clear();

        for (Trail trail : trails) {
            TrailCardController trailCard = new TrailCardController();
            trailCard.setData(trail);

            // Add some spacing between cards
            VBox.setMargin(trailCard, new Insets(10));

            trailsContainer.getChildren().add(trailCard);
        }
    }

    /**
     * Sets up pagination choice box.
     */
    private void initializePageChoiceBox() {
        if (searchService == null)
            return;

        for (int i = 0; i < searchService.getNumberOfPages(null); i++) {
            pageChoiceBox.getItems().add(String.valueOf(i + 1));
        }
        pageChoiceBox.setValue("1");
        pageChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        onSearchButtonClickedOrPageSelected();
                    } catch (LoadingTrailsFailedException e) {
                        showAlert(Alert.AlertType.ERROR, "Search is Really Broken", "How did you even get this error?");
                    }
                });
    }

    /**
     * Handles search button click or page selection change
     */
    @FXML
    private void onSearchButtonClickedOrPageSelected() throws LoadingTrailsFailedException {
        if (searchService == null) {
            return;
        }

        String query = searchBarTextField.getText();
        int page = Integer.parseInt(pageChoiceBox.getValue()) - 1;
        List<Trail> filteredTrails = searchService.getTrails(query, page);
        updateTrailsDisplay(filteredTrails);
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
