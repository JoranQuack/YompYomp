package seng202.team5.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.gui.components.TrailCardComponent;
import seng202.team5.models.Trail;
import seng202.team5.services.SearchService;

/**
 * Controller for the trails display screen.
 * Handles trail search, pagination, and card display with performance
 * optimizations.
 */
public class TrailsController extends Controller {

    private SearchService searchService;
    private SqlBasedTrailRepo sqlBasedTrailRepo;
    private String searchText;
    private final List<TrailCardComponent> trailCardPool = new ArrayList<>();

    private boolean isUpdating = false;
    private boolean isRestoringState = false;

    // State preservation
    private String savedSearchText = "";
    private String savedSelectedPage = "1";
    private String savedSortBy = null;
    private boolean savedSortAscending = true;
    private String savedRegionFilters = "Select All";
    private String savedCompletionTypeFilters = "Select All";
    private String savedTimeUnitFilters = "Select All";
    private String savedDifficultyFilters = "Select All";
    private double savedScrollPosition = 0.0;

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
    private CheckComboBox<String> completionTypeCheckComboBox;
    @FXML
    private CheckComboBox<String> timeUnitCheckComboBox;
    @FXML
    private CheckComboBox<String> difficultyCheckComboBox;
    @FXML
    private ChoiceBox<String> sortChoiceBox;
    @FXML
    private ToggleButton ascDescToggleButton;
    @FXML
    private Label resetButton;
    @FXML
    private javafx.scene.control.ScrollPane trailsScrollPane;

    /**
     * Creates a controller with navigator.
     *
     * @param navigator         Screen navigator
     * @param sqlBasedTrailRepo the trail repo
     */
    public TrailsController(ScreenNavigator navigator, SqlBasedTrailRepo sqlBasedTrailRepo) {
        super(navigator);
        this.sqlBasedTrailRepo = sqlBasedTrailRepo;
    }

    /**
     * Creates controller with navigator and initial search text.
     *
     * @param navigator         Screen navigator
     * @param searchText        Initial search text
     * @param sqlBasedTrailRepo The trail repo
     */
    public TrailsController(ScreenNavigator navigator, String searchText, SqlBasedTrailRepo sqlBasedTrailRepo) {
        super(navigator);
        this.searchText = searchText;
        this.sqlBasedTrailRepo = sqlBasedTrailRepo;
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

    @FXML
    private void onAddTrailButtonClicked() {
        super.getNavigator().launchScreen(new ModifyTrailController(super.getNavigator(), null, sqlBasedTrailRepo));
    }

    @FXML
    private void onResetClicked() {
        isUpdating = true;

        // Reset search bar
        searchBarTextField.clear();
        searchService.setCurrentQuery("");

        // Reset filters
        regionCheckComboBox.getCheckModel().clearChecks();
        completionTypeCheckComboBox.getCheckModel().clearChecks();
        timeUnitCheckComboBox.getCheckModel().clearChecks();
        difficultyCheckComboBox.getCheckModel().clearChecks();

        regionCheckComboBox.getCheckModel().check("Select All");
        completionTypeCheckComboBox.getCheckModel().check("Select All");
        timeUnitCheckComboBox.getCheckModel().check("Select All");
        difficultyCheckComboBox.getCheckModel().check("Select All");

        isUpdating = false;
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
                        onFilterChanged();
                        updateSearchDisplay();
                    }

                    // page restoration after trails are loaded
                    if (isRestoringState) {
                        restorePageAndScrollPosition();
                        isRestoringState = false;
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
        showAlert("Trails failed to load",
                "Failed to load trails, please close the application and try again.",
                "OK", "Cancel", null, null);
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
        setupCheckComboBox(completionTypeCheckComboBox, "completionType");
        setupCheckComboBox(timeUnitCheckComboBox, "timeUnit");
        setupCheckComboBox(difficultyCheckComboBox, "difficulty");
    }

    /**
     * Sets up the region CheckComboBox with available regions.
     * Preselects user's preferred regions if not guest
     */
    private void setupRegionCheckComboBox() {
        List<String> regionList = searchService.getFilterOptions("regions");

        // Remove the default "All regions" option as we handle that separately
        regionList = regionList.stream()
                .filter(region -> !region.equals("All regions"))
                .collect(Collectors.toList());

        // Add Select All option at the top
        List<String> allRegions = new ArrayList<>();
        allRegions.add("Select All");
        allRegions.addAll(regionList);

        regionCheckComboBox.getItems().addAll(allRegions);
        regionCheckComboBox.setTitle("Regions");

        regionCheckComboBox.getCheckModel().getCheckedItems().addListener(
                (javafx.collections.ListChangeListener.Change<? extends String> change) -> {
                    if (!isUpdating) {
                        handleSelectAllLogic(regionCheckComboBox, change);
                        onFilterChanged();
                    }
                });

        isUpdating = true;

        // Default: select user's preferred regions if not guest, else select all
        if (!super.getUserService().isGuest()) {
            List<String> preferredRegions = super.getUserService().getUser().getRegion();
            boolean anyMatched = false;
            for (String region : preferredRegions) {
                if (regionList.contains(region)) {
                    regionCheckComboBox.getCheckModel().check(region);
                    anyMatched = true;
                }
            }
            if (!anyMatched) {
                regionCheckComboBox.getCheckModel().check("Select All");
            }
        } else {
            regionCheckComboBox.getCheckModel().check("Select All");
        }

        isUpdating = false;
    }

    /**
     * Generic method to initialise a CheckComboBox with Select All functionality
     *
     * @param checkComboBox The CheckComboBox to initialise
     * @param filterType    The filter type identifier
     */
    private void setupCheckComboBox(CheckComboBox<String> checkComboBox, String filterType) {
        List<String> options = searchService.getFilterOptions(filterType);

        // Remove the default "All" option since we'll have Select All
        options.removeIf(option -> option.startsWith("All "));

        // Add Select All option at the top
        List<String> allOptions = new ArrayList<>();
        allOptions.add("Select All");
        allOptions.addAll(options);

        checkComboBox.getItems().addAll(allOptions);
        checkComboBox.setTitle(getFilterTitle(filterType));

        // Add listener first
        checkComboBox.getCheckModel().getCheckedItems().addListener(
                (javafx.collections.ListChangeListener.Change<? extends String> change) -> {
                    if (!isUpdating) {
                        handleSelectAllLogic(checkComboBox, change);
                        onFilterChanged();
                    }
                });

        isUpdating = true;
        // Default: only check "Select All"
        checkComboBox.getCheckModel().check("Select All");
        isUpdating = false;
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
        searchService.updateFilter("regions", getSelectedFilterValues(regionCheckComboBox));
        searchService.updateFilter("completionType", getSelectedFilterValues(completionTypeCheckComboBox));
        searchService.updateFilter("timeUnit", getSelectedFilterValues(timeUnitCheckComboBox));
        searchService.updateFilter("difficulty", getSelectedFilterValues(difficultyCheckComboBox));
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
        super.getNavigator().launchScreen(new ViewTrailController(super.getNavigator(), trail, sqlBasedTrailRepo));
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
        VBox noResultsContainer = new VBox();

        Label noResultsLabel = new Label(
                "No trails match your search. Please try adjusting your search and filter preferences.");

        noResultsContainer.getChildren().add(noResultsLabel);
        noResultsContainer.getChildren().add(createResetButton());
        noResultsContainer.setSpacing(10);
        noResultsContainer.setPadding(new Insets(20));
        noResultsContainer.alignmentProperty().set(javafx.geometry.Pos.CENTER);
        trailsContainer.getChildren().add(noResultsContainer);

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
            TrailCardComponent trailCard = new TrailCardComponent(isGuest, false);
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
     * Gets the title for a filter type
     */
    private String getFilterTitle(String filterType) {
        switch (filterType) {
            case "completionType":
                return "Types";
            case "timeUnit":
                return "Time Units";
            case "difficulty":
                return "Difficulties";
            default:
                return filterType;
        }
    }

    /**
     * Handles Select All logic for CheckComboBox (cool stuff)
     */
    private void handleSelectAllLogic(CheckComboBox<String> checkComboBox,
            javafx.collections.ListChangeListener.Change<? extends String> change) {
        isUpdating = true;

        while (change.next()) {
            if (change.wasAdded()) {
                for (String added : change.getAddedSubList()) {
                    if ("Select All".equals(added)) {
                        // When Select All is checked, uncheck all other items
                        for (String item : checkComboBox.getItems()) {
                            if (!"Select All".equals(item)) {
                                checkComboBox.getCheckModel().clearCheck(item);
                            }
                        }
                        break;
                    } else {
                        // When any specific item is checked, uncheck Select All
                        if (checkComboBox.getCheckModel().isChecked("Select All")) {
                            checkComboBox.getCheckModel().clearCheck("Select All");
                        }
                    }
                }
            } else if (change.wasRemoved()) {
                for (String removed : change.getRemoved()) {
                    if ("Select All".equals(removed)) {
                        // Check if Select All was the only thing selected
                        boolean anySpecificItemChecked = false;
                        for (String item : checkComboBox.getItems()) {
                            if (!"Select All".equals(item) && checkComboBox.getCheckModel().isChecked(item)) {
                                anySpecificItemChecked = true;
                                break;
                            }
                        }
                        // If no items are checked, re-check "Select All" coz we always want it by
                        // default
                        if (!anySpecificItemChecked) {
                            checkComboBox.getCheckModel().check("Select All");
                        }
                    } else {
                        // automatically check "Select All" to prevent having nothing selected
                        boolean anySpecificItemChecked = false;
                        for (String item : checkComboBox.getItems()) {
                            if (!"Select All".equals(item) && checkComboBox.getCheckModel().isChecked(item)) {
                                anySpecificItemChecked = true;
                                break;
                            }
                        }
                        if (!anySpecificItemChecked && !checkComboBox.getCheckModel().isChecked("Select All")) {
                            checkComboBox.getCheckModel().check("Select All");
                        }
                    }
                }
            }
        }

        // Ensure Select All is checked if all other items are checked
        boolean allSpecificItemsSelected = true;
        boolean anySpecificItemSelected = false;
        for (String item : checkComboBox.getItems()) {
            if (!"Select All".equals(item)) {
                if (checkComboBox.getCheckModel().isChecked(item)) {
                    anySpecificItemSelected = true;
                } else {
                    allSpecificItemsSelected = false;
                }
            }
        }

        // If all specific items are selected, check Select All
        if (allSpecificItemsSelected && anySpecificItemSelected
                && !checkComboBox.getCheckModel().isChecked("Select All")) {
            checkComboBox.getCheckModel().check("Select All");
            for (String item : checkComboBox.getItems()) {
                if (!"Select All".equals(item)) {
                    checkComboBox.getCheckModel().clearCheck(item);
                }
            }
        }

        isUpdating = false;
    }

    /**
     * Gets selected filter values, handling "Select All" case properly
     */
    private String getSelectedFilterValues(CheckComboBox<String> checkComboBox) {
        // If "Select All" is checked, all items should pass
        if (checkComboBox.getCheckModel().isChecked("Select All")) {
            return "Select All";
        }

        // Otherwise return the selected specific values
        String selectedValues = checkComboBox.getCheckModel().getCheckedItems().stream()
                .filter(item -> !"Select All".equals(item))
                .collect(Collectors.joining(","));

        // Default to "Select All"
        return selectedValues.isEmpty() ? "Select All" : selectedValues;
    }

    /**
     * Creates a reset button
     *
     * @return A configured reset button Label
     */
    private Label createResetButton() {
        Label resetBtn = new Label("Reset");
        resetBtn.setAlignment(javafx.geometry.Pos.CENTER);
        resetBtn.setGraphicTextGap(2.0);
        resetBtn.setOnMouseClicked(event -> onResetClicked());

        javafx.scene.image.ImageView resetIcon = new javafx.scene.image.ImageView();
        resetIcon.setFitHeight(17.0);
        resetIcon.setFitWidth(17.0);
        resetIcon.setPickOnBounds(true);
        resetIcon.setPreserveRatio(true);
        resetIcon.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/reset.png")));
        resetBtn.setGraphic(resetIcon);

        resetBtn.getStyleClass().addAll("special-button", "regular-text");
        return resetBtn;
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

    /**
     * Saves the current state of the trails controller
     */
    @Override
    public void saveState() {
        // search text
        if (searchBarTextField != null) {
            savedSearchText = searchBarTextField.getText() != null ? searchBarTextField.getText() : "";
        }

        // current page
        if (pageChoiceBox != null && pageChoiceBox.getValue() != null) {
            savedSelectedPage = pageChoiceBox.getValue();
        }

        // sort settings
        if (sortChoiceBox != null && sortChoiceBox.getValue() != null) {
            savedSortBy = sortChoiceBox.getValue();
        }
        if (ascDescToggleButton != null) {
            savedSortAscending = ascDescToggleButton.isSelected();
        }

        // filter states
        if (regionCheckComboBox != null) {
            savedRegionFilters = getSelectedFilterValues(regionCheckComboBox);
        }
        if (completionTypeCheckComboBox != null) {
            savedCompletionTypeFilters = getSelectedFilterValues(completionTypeCheckComboBox);
        }
        if (timeUnitCheckComboBox != null) {
            savedTimeUnitFilters = getSelectedFilterValues(timeUnitCheckComboBox);
        }
        if (difficultyCheckComboBox != null) {
            savedDifficultyFilters = getSelectedFilterValues(difficultyCheckComboBox);
        }

        // scroll position
        if (trailsScrollPane != null) {
            savedScrollPosition = trailsScrollPane.getVvalue();
        }
    }

    /**
     * Restores the previously saved state of the trails controller.
     */
    @Override
    public void restoreState() {
        if (searchService == null) {
            return;
        }

        isRestoringState = true;
        isUpdating = true;

        // search text
        if (searchBarTextField != null && savedSearchText != null) {
            searchBarTextField.setText(savedSearchText);
            searchService.setCurrentQuery(savedSearchText);
        }

        // sort settings
        if (sortChoiceBox != null && savedSortBy != null) {
            sortChoiceBox.setValue(savedSortBy);
            searchService.setSortBy(savedSortBy.toLowerCase());
        }
        if (ascDescToggleButton != null) {
            ascDescToggleButton.setSelected(savedSortAscending);
            searchService.setSortAscending(savedSortAscending);
            updateToggleButtonText();
        }

        // filter states
        restoreCheckComboBoxState(regionCheckComboBox, savedRegionFilters);
        restoreCheckComboBoxState(completionTypeCheckComboBox, savedCompletionTypeFilters);
        restoreCheckComboBoxState(timeUnitCheckComboBox, savedTimeUnitFilters);
        restoreCheckComboBoxState(difficultyCheckComboBox, savedDifficultyFilters);

        // Apply filters
        searchService.updateFilter("regions", savedRegionFilters);
        searchService.updateFilter("completionType", savedCompletionTypeFilters);
        searchService.updateFilter("timeUnit", savedTimeUnitFilters);
        searchService.updateFilter("difficulty", savedDifficultyFilters);

        isUpdating = false;

        // The page restoration will happen in loadInitialDataAsync's success
    }

    /**
     * Helper method to restore CheckComboBox state from saved filter string.
     *
     * @param checkComboBox The CheckComboBox to restore
     * @param savedFilters  The saved filter string
     */
    private void restoreCheckComboBoxState(CheckComboBox<String> checkComboBox, String savedFilters) {
        if (checkComboBox == null || savedFilters == null) {
            return;
        }

        // Clear all selections
        checkComboBox.getCheckModel().clearChecks();

        if ("Select All".equals(savedFilters)) {
            checkComboBox.getCheckModel().check("Select All");
        } else {
            // Check items
            String[] filters = savedFilters.split(",");
            for (String filter : filters) {
                String trimmedFilter = filter.trim();
                if (checkComboBox.getItems().contains(trimmedFilter)) {
                    checkComboBox.getCheckModel().check(trimmedFilter);
                }
            }
        }
    }

    /**
     * Restores the page selection and scroll position AFTER trails load in
     * otherwise issues happen.
     */
    private void restorePageAndScrollPosition() {
        // page selection
        if (pageChoiceBox != null && savedSelectedPage != null) {
            isUpdating = true;
            pageChoiceBox.setValue(savedSelectedPage);
            onPageSelected();
            isUpdating = false;
        }

        // scroll position after short delay
        if (trailsScrollPane != null) {
            Platform.runLater(() -> {
                Platform.runLater(() -> { // Double runLater to ensure UI is done loading
                    trailsScrollPane.setVvalue(savedScrollPosition);
                });
            });
        }
    }
}