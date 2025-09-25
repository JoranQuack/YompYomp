package seng202.team5.gui;

import java.util.List;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seng202.team5.App;
import seng202.team5.gui.components.NavbarComponent;
import seng202.team5.gui.components.TrailCardComponent;
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

    /** Pool of reusable trail card components to avoid recreating them */
    private final List<TrailCardComponent> trailCardPool = new ArrayList<>();

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

    @FXML
    private ChoiceBox<String> multiDayChoiceBox;

    @FXML
    private ChoiceBox<String> sortChoiceBox;

    @FXML
    private ToggleButton ascDescToggleButton;

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
        this.searchService = new SearchService(App.getDatabaseService());
    }

    /**
     * Initializes the trails view with default data.
     */
    @FXML
    private void initialize() {
        // Initialize the navbar from parent coz you can't have your kid running around
        NavbarComponent navbar = super.getNavbarController();
        navbar.setPage(1);
        navbarContainer.getChildren().add(navbar);

        initializeSearchService();

        // Check if searchService is still null and handle gracefully
        if (searchService == null) {
            resultsLabel.setText("No trails available");
            Label noResultsLabel = new Label(
                    "There are no trails available, as the application has failed. Please close the application and try again.");
            trailsContainer.getChildren().add(noResultsLabel);
            showAlert(Alert.AlertType.ERROR, "Trails failed to load",
                    "Failed to load trails, please close the application and try again.");
            return;
        }

        initializeFilterChoiceBoxes();

        // check if the user searched from dashboard
        if (searchText != null) {
            executeDashboardSearch();
        }
        updateSearchDisplay();
    }

    /**
     * This method is used to populate and execute search on the trails
     * page if a search is made from the dashboard page
     */
    private void executeDashboardSearch() {
        searchBarTextField.setText(searchText);
        searchService.setCurrentQuery(searchText);
        updateSearchDisplay();
    }

    /**
     * Initialises the filter choice boxes.
     */
    private void initializeFilterChoiceBoxes() {
        initializeChoiceBox(completionTypeChoiceBox, "completionType");
        initializeChoiceBox(timeUnitChoiceBox, "timeUnit");
        initializeChoiceBox(difficultyChoiceBox, "difficulty");
        initializeChoiceBox(multiDayChoiceBox, "multiDay");
        initializeSortChoiceBox();
        initializeToggleButton();
    }

    /**
     * Generic method to initialise a choice box
     *
     * @param choiceBox  The ChoiceBox to initialise
     * @param filterType The filter type identifier
     */
    private void initializeChoiceBox(ChoiceBox<String> choiceBox, String filterType) {
        List<String> options = searchService.getFilterOptions(filterType);

        // Special sorting for difficulty
        if (filterType.equals("difficulty")) {
            sortDifficultyOptions(options);
        }

        choiceBox.getItems().addAll(options);
        choiceBox.setValue(searchService.getDefaultFilterValue(filterType));
        choiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onFilterChanged());
    }

    /**
     * Difficulty sorting logic
     */
    private void sortDifficultyOptions(List<String> options) {
        List<String> difficultyOrder = SearchService.getDifficultyOrder();
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
     * Initialises the sort choice box with the available sort options that we have
     */
    private void initializeSortChoiceBox() {
        List<String> sortOptions = searchService.getSortOptions();
        sortChoiceBox.getItems().addAll(sortOptions);

        if (super.getUserService().isGuest()) {
            sortChoiceBox.getItems().remove("Match");
            sortChoiceBox.setValue("Name");
        } else {
            sortChoiceBox.setValue("Match");
        }

        sortChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onSortChanged());
    }

    /**
     * Initialises the toggle button for asc/desc order
     */
    private void initializeToggleButton() {
        ascDescToggleButton.setSelected(true);
        updateToggleButtonText();

        ascDescToggleButton.setOnAction(event -> onToggleButtonClicked());
    }

    /**
     * Updates the toggle button text based on current state
     * Cute arrows for the win
     */
    private void updateToggleButtonText() {
        if (ascDescToggleButton.isSelected()) {
            ascDescToggleButton.setText("↑ Asc");
        } else {
            ascDescToggleButton.setText("↓ Desc");
        }
    }

    /**
     * Method to update the search results display.
     */
    private void updateSearchDisplay() {
        List<Trail> trails = searchService.getPage(0);
        updateTrailsDisplay(trails);
        resetPageChoiceBox();
    }

    /**
     * Updates display with trail cards using component reuse for better
     * performance.
     *
     * @param trails List of trails to display
     */
    private void updateTrailsDisplay(List<Trail> trails) {
        System.out.println("updating trails display");
        trailsContainer.getChildren().clear();

        if (trails.isEmpty()) {
            Label noResultsLabel = new Label(
                    "No trails match your search. Please try adjusting your search and filter preferences.");
            trailsContainer.getChildren().add(noResultsLabel);
            resultsLabel.setText("No trails found.");
            return;
        }

        boolean isGuest = super.getUserService().isGuest();
        Insets cardMargin = new Insets(10);

        for (int i = 0; i < trails.size(); i++) {
            Trail trail = trails.get(i);
            TrailCardComponent trailCard;

            // Reuse existing component or create new one if needed
            if (i < trailCardPool.size()) {
                trailCard = trailCardPool.get(i);
            } else {
                trailCard = new TrailCardComponent(isGuest);
                trailCardPool.add(trailCard);
                VBox.setMargin(trailCard, cardMargin);
            }

            trailCard.setData(trail);
            trailCard.setOnMouseClicked(e -> onTrailCardClicked(trail));
            trailsContainer.getChildren().add(trailCard);
        }

        // Update results label
        resultsLabel.setText(trails.size() + "/" + searchService.getNumberOfTrails() + " trails showing");
    }

    @FXML
    private void onTrailCardClicked(Trail trail) {
        super.getNavigator().launchScreen(new ViewTrailController(super.getNavigator(), trail), this);
    }

    /**
     * Sets up pagination choice box.
     */
    private void resetPageChoiceBox() {
        pageChoiceBox.getItems().clear();
        int numPages = searchService.getNumberOfPages();

        if (numPages > 0) {
            String[] pageItems = new String[numPages];
            for (int i = 0; i < numPages; i++) {
                pageItems[i] = String.valueOf(i + 1);
            }
            pageChoiceBox.getItems().addAll(pageItems);
            pageChoiceBox.setValue("1");
        }

        pageChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onPageSelected());
    }

    /**
     * Handles filter change event.
     */
    private void onFilterChanged() {
        searchService.updateFilter("completionType", completionTypeChoiceBox.getValue());
        searchService.updateFilter("timeUnit", timeUnitChoiceBox.getValue());
        searchService.updateFilter("difficulty", difficultyChoiceBox.getValue());
        searchService.updateFilter("multiDay", multiDayChoiceBox.getValue());
        updateSearchDisplay();
    }

    /**
     * Handles sort change event.
     */
    private void onSortChanged() {
        String selectedSort = sortChoiceBox.getValue();
        if (selectedSort != null) {
            searchService.setSortBy(selectedSort.toLowerCase());
            updateSearchDisplay();
        }
    }

    /**
     * Handles toggle button click for asc/desc order
     */
    private void onToggleButtonClicked() {
        searchService.setSortAscending(ascDescToggleButton.isSelected());
        updateToggleButtonText();
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
