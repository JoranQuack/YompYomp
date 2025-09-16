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

    /** Ordered list of difficulty levels for proper sorting */
    private final List<String> difficultyOrder = List.of("easiest", "easy", "intermediate", "advanced", "expert");
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

    @FXML
    private ChoiceBox<String> difficultyChoiceBox;

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
        initializeChoiceBox(completionTypeChoiceBox, "completionType");
        initializeChoiceBox(timeUnitChoiceBox, "timeUnit");
        initializeChoiceBox(difficultyChoiceBox, "difficulty");
    }

    /**
     * Generic method to initialise a choice box
     *
     * @param choiceBox  The ChoiceBox to initialise
     * @param filterType The filter type identifier
     */
    private void initializeChoiceBox(ChoiceBox<String> choiceBox, String filterType) {
        choiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onFilterChanged());

        List<String> options = searchService.getFilterOptions(filterType);

        // Special sorting for difficulty
        if (filterType.equals("difficulty")) {
            sortDifficultyOptions(options);
        }

        choiceBox.getItems().addAll(options);
        choiceBox.setValue(searchService.getDefaultFilterValue(filterType));
    }

    /**
     * Difficulty sorting logic
     */
    private void sortDifficultyOptions(List<String> options) {
        options.sort((a, b) -> {
            if (a.equals("All difficulties"))
                return -1;
            if (b.equals("All difficulties"))
                return 1;

            int indexA = difficultyOrder.indexOf(a.toLowerCase());
            int indexB = difficultyOrder.indexOf(b.toLowerCase());

            if (indexA != -1 && indexB != -1) {
                return Integer.compare(indexA, indexB);
            }
            if (indexA != -1)
                return -1;
            if (indexB != -1)
                return 1;
            return a.compareToIgnoreCase(b);
        });
    }

    /**
     * Method to update the search results display.
     */
    private void updateSearchDisplay() {
        List<Trail> trails = searchService.getPage(0);
        updateTrailsDisplay(trails);

        if (trails.isEmpty()) {
            resultsLabel.setText("No trails found.");
        } else {
            resultsLabel.setText(trails.size() + "/" + searchService.getNumberOfTrails() + " trails loaded");
        }

        resetPageChoiceBox();
    }

    /**
     * Updates display with trail cards.
     *
     * @param trails List of trails to display
     */
    private void updateTrailsDisplay(List<Trail> trails) {
        trailsContainer.getChildren().clear();

        if (trails.isEmpty()) {
            Label noResultsLabel = new Label("No trails match your search. Please try adjusting your search and filter preferences.");
            trailsContainer.getChildren().add(noResultsLabel);
            return;
        }

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
        searchService.updateFilter("difficulty", difficultyChoiceBox.getValue());
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

            if (trails.isEmpty()) {
                resultsLabel.setText("No trails found.");
            } else {
                resultsLabel.setText(trails.size() + "/" + searchService.getNumberOfTrails() + " trails loaded");
            }
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
