package seng202.team5.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.concurrent.Task;
import javafx.application.Platform;
import org.controlsfx.control.CheckComboBox;
import seng202.team5.App;
import seng202.team5.gui.components.TrailCardComponent;
import seng202.team5.models.Trail;
import seng202.team5.models.User;
import seng202.team5.services.RegionFinder;
import seng202.team5.services.SearchService;

/**
 * Controller for the trails display screen.
 * Handles trail search, pagination, and card display with performance
 * optimizations.
 */
public class TrailsController extends Controller {

    private SearchService searchService;
    private String searchText;
    private final List<TrailCardComponent> trailCardPool = new ArrayList<>();

    private boolean isUpdating = false;

    // FXML components
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchBarTextField;
    @FXML
    private FlowPane trailsContainer;
    @FXML
    private Label resultsLabel;
    @FXML
    private CheckComboBox<String> regionCheckComboBox;
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
     * Creates controller with navigator and initial search text.
     *
     * @param navigator  Screen navigator
     * @param searchText Initial search text
     */
    public TrailsController(ScreenNavigator navigator, String searchText) {
        super(navigator);
        this.searchText = searchText;
    }

    /**
     * Initializes the trails view with default data.
     */
    @FXML
    private void initialize() {
        isUpdating = true;

        initializeSearchService();
        if (searchService == null) {
            handleInitializationFailure();
            return;
        }

        setupUIComponents();
        isUpdating = false;

        // show loading stuff straight away
        showLoadingState();

        // Load data asynchronously
        loadInitialDataAsync();
    }

    /**
     * Shows a loading state while trails are being fetched.
     */
    private void showLoadingState() {
        trailsContainer.getChildren().clear();
        Label loadingLabel = new Label("Loading trails...");
        trailsContainer.getChildren().add(loadingLabel);
        resultsLabel.setText("Loading...");
    }

    /**
     * Loads initial data asynchronously
     */
    private void loadInitialDataAsync() {
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (searchText != null) {
                    searchService.setCurrentQuery(searchText);
                }
                searchService.getPage(0);
                return null;
            }

            @Override
            protected void succeeded() {
                // JavaFX app thread
                Platform.runLater(() -> {
                    if (searchText != null) {
                        executeDashboardSearch();
                    } else {
                        updateSearchDisplay();
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    trailsContainer.getChildren().clear();
                    Label errorLabel = new Label("Failed to load trails. Please try again.");
                    trailsContainer.getChildren().add(errorLabel);
                    resultsLabel.setText("Error loading trails");
                });
            }
        };

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    /**
     * Initializes the search service.
     */
    private void initializeSearchService() {
        this.searchService = new SearchService(App.getDatabaseService());
    }

    /**
     * Handles initialization failures by displaying an error message.
     */
    private void handleInitializationFailure() {
        resultsLabel.setText("No trails available");
        Label noResultsLabel = new Label(
                "There are no trails available, as the application has failed. Please close the application and try again.");
        trailsContainer.getChildren().add(noResultsLabel);
        showAlert(Alert.AlertType.ERROR, "Trails failed to load",
                "Failed to load trails, please close the application and try again.");
    }

    /**
     * Sets up the user interface components.
     * Filter, sort, and pagination controls.
     */
    private void setupUIComponents() {
        setupRegionCheckComboBox();
        setupFilterChoiceBoxes();
        setupSortChoiceBox();
        setupToggleButton();
        setupPageChoiceBoxListener();
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
    private void setupFilterChoiceBoxes() {
        setupChoiceBox(completionTypeChoiceBox, "completionType");
        setupChoiceBox(timeUnitChoiceBox, "timeUnit");
        setupChoiceBox(difficultyChoiceBox, "difficulty");
        setupChoiceBox(multiDayChoiceBox, "multiDay");
    }

    /**
     * Sets up the region CheckComboBox with available regions.
     * Preselects user's preferred regions if not guest
     */
    private void setupRegionCheckComboBox() {
        RegionFinder regionFinder = new RegionFinder();
        List<String> regionList = regionFinder.getRegionNames();

        regionCheckComboBox.getItems().addAll(regionList);
        regionCheckComboBox.setTitle("Regions");

        // preselection
        if (!super.getUserService().isGuest()) {
            User user = super.getUserService().getUser();
            if (user != null && user.getRegion() != null && !user.getRegion().isEmpty()) {
                for (String region : user.getRegion()) {
                    regionCheckComboBox.getCheckModel().check(region);
                }
            } else {
                // Default to all regions selected if user has no preferences
                regionCheckComboBox.getCheckModel().checkAll();
            }
        } else {
            // Guest user
            regionCheckComboBox.getCheckModel().checkAll();
        }

        regionCheckComboBox.getCheckModel().getCheckedItems().addListener(
                (javafx.collections.ListChangeListener.Change<? extends String> change) -> {
                    if (!isUpdating) {
                        onFilterChanged();
                    }
                });
    }

    /**
     * Generic method to initialise a choice box
     *
     * @param choiceBox  The ChoiceBox to initialise
     * @param filterType The filter type identifier
     */
    private void setupChoiceBox(ChoiceBox<String> choiceBox, String filterType) {
        List<String> options = searchService.getFilterOptions(filterType);

        if (filterType.equals("difficulty")) {
            sortDifficultyOptions(options);
        }

        choiceBox.getItems().addAll(options);
        choiceBox.setValue(searchService.getDefaultFilterValue(filterType));

        choiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!isUpdating) {
                        onFilterChanged();
                    }
                });
    }

    /**
     * Sets up the sort choice box with the available sort options that we have
     */
    private void setupSortChoiceBox() {
        List<String> sortOptions = searchService.getSortOptions();
        sortChoiceBox.getItems().addAll(sortOptions);

        if (super.getUserService().isGuest()) {
            sortChoiceBox.getItems().remove("Match");
            sortChoiceBox.setValue("Name");
            searchService.setSortBy("name");
        } else {
            sortChoiceBox.setValue("Match");
            searchService.setSortBy("match");
        }

        sortChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!isUpdating) {
                        onSortChanged();
                    }
                });
    }

    /**
     * Sets up the asc/desc toggle button.
     */
    private void setupToggleButton() {
        ascDescToggleButton.setSelected(true);
        updateToggleButtonText();
        ascDescToggleButton.setOnAction(event -> onToggleButtonClicked());
    }

    /**
     * Sets up the page choice box listener for pagination.
     */
    private void setupPageChoiceBoxListener() {
        pageChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!isUpdating) {
                        onPageSelected();
                    }
                });
    }

    @FXML
    private void onSearchButtonClicked() {
        searchService.setCurrentQuery(searchBarTextField.getText());
        updateSearchDisplay();
    }

    /**
     * Handles filter changes by updating the search service and refreshing the
     * display.
     */
    private void onFilterChanged() {
        searchService.updateFilter("regions", String.join(",", regionCheckComboBox.getCheckModel().getCheckedItems()));
        searchService.updateFilter("completionType", completionTypeChoiceBox.getValue());
        searchService.updateFilter("timeUnit", timeUnitChoiceBox.getValue());
        searchService.updateFilter("difficulty", difficultyChoiceBox.getValue());
        searchService.updateFilter("multiDay", multiDayChoiceBox.getValue());
        updateSearchDisplay();
    }

    /**
     * Handles sort changes by updating the search service and refreshing the
     * display.
     */
    private void onSortChanged() {
        String selectedSort = sortChoiceBox.getValue();
        if (selectedSort != null) {
            searchService.setSortBy(selectedSort.toLowerCase());
            updateSearchDisplay();
        }
    }

    /**
     * Handles asc/desc toggle button clicks by updating the search service and
     * refreshing the display.
     */
    private void onToggleButtonClicked() {
        searchService.setSortAscending(ascDescToggleButton.isSelected());
        updateToggleButtonText();
        updateSearchDisplay();
    }

    /**
     * Handles page selection changes by updating the displayed trails.
     */
    private void onPageSelected() {
        String selectedPage = pageChoiceBox.getValue();
        if (selectedPage != null) {
            int pageIndex = Integer.parseInt(selectedPage) - 1;
            List<Trail> trails = searchService.getPage(pageIndex);
            updateTrailsDisplay(trails);
        }
    }

    @FXML
    private void onTrailCardClicked(Trail trail) {
        super.getNavigator().launchScreen(new ViewTrailController(super.getNavigator(), trail, searchService), this);
    }

    /**
     * Updates the displayed trails based on the current search, filter, sort, and
     * pagination settings.
     */
    private void updateSearchDisplay() {
        List<Trail> trails = searchService.getPage(0);
        updateTrailsDisplay(trails);
        resetPageChoiceBox();
    }

    /**
     * Updates the trails displayed in the UI, reusing TrailCardComponents from a
     * pool for performance.
     *
     * @param trails List of trails to display
     */
    private void updateTrailsDisplay(List<Trail> trails) {
        trailsContainer.getChildren().clear();

        if (trails.isEmpty()) {
            showNoResultsMessage();
            return;
        }

        boolean isGuest = super.getUserService().isGuest();
        Insets cardMargin = new Insets(10);

        for (int i = 0; i < trails.size(); i++) {
            Trail trail = trails.get(i);
            TrailCardComponent trailCard = getOrCreateTrailCard(i, isGuest, cardMargin);

            trailCard.setData(trail);
            trailCard.setOnMouseClicked(e -> onTrailCardClicked(trail));
            trailsContainer.getChildren().add(trailCard);
        }

        updateResultsLabel(trails.size());
    }

    /**
     * Updates labels to show to user there are no results matching their search.
     */
    private void showNoResultsMessage() {
        Label noResultsLabel = new Label(
                "No trails match your search. Please try adjusting your search and filter preferences.");
        trailsContainer.getChildren().add(noResultsLabel);
        resultsLabel.setText("No trails found.");
    }

    /**
     * Retrieves a TrailCardComponent from the pool or creates a new one if needed.
     *
     * @param index      The index of the trail card
     * @param isGuest    Whether the user is a guest
     * @param cardMargin The margin to apply to the card
     * @return A TrailCardComponent
     */
    private TrailCardComponent getOrCreateTrailCard(int index, boolean isGuest, Insets cardMargin) {
        if (index < trailCardPool.size()) {
            return trailCardPool.get(index);
        } else {
            TrailCardComponent trailCard = new TrailCardComponent(isGuest);
            trailCardPool.add(trailCard);
            VBox.setMargin(trailCard, cardMargin);
            return trailCard;
        }
    }

    /**
     * Updates the results label to show the number of trails currently displayed
     *
     * @param trailCount Number of trails currently displayed
     */
    private void updateResultsLabel(int trailCount) {
        resultsLabel.setText(trailCount + "/" + searchService.getNumberOfTrails() + " trails showing");
    }

    /**
     * Resets the page selection dropdown to the initial state.
     */
    private void resetPageChoiceBox() {
        isUpdating = true;

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

        isUpdating = false;
    }

    /**
     * Updates the asc/desc toggle button text based on its state.
     */
    private void updateToggleButtonText() {
        if (ascDescToggleButton.isSelected()) {
            ascDescToggleButton.setText("↑ Asc");
        } else {
            ascDescToggleButton.setText("↓ Desc");
        }
    }

    /**
     * Difficulty sorting logic
     *
     * @param options List of difficulty options
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

    @Override
    protected String getFxmlFile() {
        return "/fxml/trails.fxml";
    }

    @Override
    protected String getTitle() {
        return "Trails Screen";
    }

    @Override
    protected boolean shouldShowNavbar() {
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return 1; // Trails is the second tab
    }
}