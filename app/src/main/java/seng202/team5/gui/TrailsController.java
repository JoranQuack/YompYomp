package seng202.team5.gui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
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
    private String searchText;

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

    @FXML
    private ChoiceBox<String> completionTypeChoiceBox;

    @FXML
    private ChoiceBox<String> timeUnitChoiceBox;

    /**
     * Creates controller with navigator.
     *
     * @param navigator Screen navigator
     */
    public TrailsController(ScreenNavigator navigator) {
        super(navigator);
    }

    /**
     *
     */
    public TrailsController(ScreenNavigator navigator, String searchText) {
        super(navigator);
        this.searchText = searchText;
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

        initializeSearchService();

        // Check if searchService is still null and handle gracefully
        if (searchService == null) {
            resultsLabel.setText("No trails available");
            return;
        }

        pageChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onPageSelected());
        initializeFilterChoiceBoxes();

        // check if the user searched from dashboard
        if (searchText != null) {
            executeDashboardSearch();
        } else {
            updateSearchDisplay();
        }

    }

    /**
     * This method is used to populate and click the search button on the trails
     * page a search is made from
     * the dashboard page
     */
    private void executeDashboardSearch() {
        searchBarTextField.setText(searchText);
        searchButton.fire();
    }

    /**
     * Initialises the filter choice boxes.
     */
    private void initializeFilterChoiceBoxes() {
        completionTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onFilterChanged());
        timeUnitChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onFilterChanged());

        completionTypeChoiceBox.getItems().add("All types");
        completionTypeChoiceBox.setValue("All types");
        for (String completionType : searchService.getAllCompletionTypes()) {
            completionTypeChoiceBox.getItems()
                    .add(completionType.substring(0, 1).toUpperCase() + completionType.substring(1));
        }

        timeUnitChoiceBox.getItems().addAll("All durations", "Minutes", "Hours", "Days");
        timeUnitChoiceBox.setValue("All durations");
    }

    /**
     * Method to update the search results display.
     */
    private void updateSearchDisplay() {
        List<Trail> trails = searchService.getPage(0);
        updateTrailsDisplay(trails);
        resultsLabel.setText(trails.size() + "/" + searchService.getNumberOfTrails() + " trails loaded");
        resetPageChoiceBox();
    }

    /**
     * Updates display with trail cards.
     *
     * @param trails List of trails to display
     */
    private void updateTrailsDisplay(List<Trail> trails) {
        trailsContainer.getChildren().clear();

        for (Trail trail : trails) {
            TrailCardController trailCard = new TrailCardController(super.getUserService().isGuest());
            trailCard.setData(trail);

            // Add some spacing between cards
            VBox.setMargin(trailCard, new Insets(10));

            trailsContainer.getChildren().add(trailCard);
        }
    }

    /**
     * Sets up pagination choice box.
     */
    private void resetPageChoiceBox() {
        pageChoiceBox.getItems().clear();
        for (int i = 0; i < searchService.getNumberOfPages(); i++) {
            pageChoiceBox.getItems().add(String.valueOf(i + 1));
        }
        pageChoiceBox.setValue("1");
    }

    /**
     * Handles filter change event.
     */
    private void onFilterChanged() {
        searchService.updateFilter("completionType", completionTypeChoiceBox.getValue());
        searchService.updateFilter("timeUnit", timeUnitChoiceBox.getValue());
        updateSearchDisplay();
    }

    /**
     * Handles search button click or page selection change
     */
    @FXML
    private void onSearchButtonClicked() {
        searchService.setCurrentQuery(searchBarTextField.getText());
        updateSearchDisplay();
    }

    /**
     * Handles page selection change.
     */
    private void onPageSelected() {
        String selectedPage = pageChoiceBox.getValue();
        if (selectedPage != null) {
            int pageIndex = Integer.parseInt(selectedPage) - 1;
            List<Trail> trails = searchService.getPage(pageIndex);
            updateTrailsDisplay(trails);
            resultsLabel.setText(trails.size() + "/" + searchService.getNumberOfTrails() + " trails loaded");
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
