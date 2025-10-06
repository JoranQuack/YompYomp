package seng202.team5.gui;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailLogRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.gui.components.TrailCardComponent;
import seng202.team5.models.Trail;
import seng202.team5.models.TrailLog;
import seng202.team5.services.LogService;
import seng202.team5.services.SearchService;

/**
 * Controller for the dashboard screen.
 */
public class DashboardController extends Controller {
    /** Service for searching and filtering trails */
    private SearchService searchService;
    private SqlBasedTrailLogRepo trailLogRepo;
    private LogService logService;
    private SqlBasedTrailRepo repo;

    @FXML
    private FlowPane trailsContainer;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchBarTextField;

    @FXML
    private Hyperlink DOCLink;

    /**
     * Default constructor required by JavaFX FXML loading.
     */
    public DashboardController() {
        super();
    }

    /**
     * Creates controller with navigator.
     *
     * @param navigator Screen navigator
     */
    public DashboardController(ScreenNavigator navigator) {
        super(navigator);
        initializeSearchService();
        this.logService = new LogService(new DatabaseService());
        this.trailLogRepo = new SqlBasedTrailLogRepo(new DatabaseService());
    }

    /**
     * Initializes the search service.
     */
    private void initializeSearchService() {
        this.repo = new SqlBasedTrailRepo(new DatabaseService());
        this.searchService = new SearchService(repo);
        searchService.setMaxResults(8);
    }

    /**
     * Initialises the trails view with default data.
     */
    @FXML
    private void initialize() {
        // Initialize search service if not already done
        if (searchService == null) {
            initializeSearchService();
        }

        List<Trail> trails = repo.getRecommendedTrails();
        initializeRecommendedTrails(trails);
        // addTrailButton.setOnAction(e -> onAddTrailButtonClicked());

        searchBarTextField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchButton.fire();
                event.consume();
            }
        });

        DOCLink.setOnAction(e -> super.getNavigator().openWebPage("https://www.doc.govt.nz/"));
    }

    @FXML
    private void onViewAllClicked() {
        super.getNavigator().launchScreen(new TrailsController(super.getNavigator(), repo));
    }

    @FXML
    private void onAddTrailButtonClicked() {
        super.getNavigator().launchScreen(new ModifyTrailController(super.getNavigator(), null, repo));
    }

    @FXML
    private void onSearchButtonClicked() {
        super.getNavigator()
                .launchScreen(new TrailsController(super.getNavigator(), searchBarTextField.getText(), repo));
    }

    /**
     * Updates display with trail cards.
     *
     * @param trails List of trails to display
     */
    private void initializeRecommendedTrails(List<Trail> trails) {
        trailsContainer.getChildren().clear();
        trails.forEach(this::createAndAddTrailCard);
    }

    /**
     * Creates and adds a TrailCardComponent to the container.
     */
    private void createAndAddTrailCard(Trail trail) {
        TrailCardComponent trailCard = createTrailCard(trail);
        setupTrailCardHandlers(trailCard, trail);
        VBox.setMargin(trailCard, new Insets(10));
        trailsContainer.getChildren().add(trailCard);
    }

    /**
     * Creates the trail card for a specific trail
     * @param trail The trail that the card needs to be created for
     * @return the Trail card
     */
    private TrailCardComponent createTrailCard(Trail trail) {
        TrailCardComponent trailCard = new TrailCardComponent(
                super.getUserService().isGuest(), false, false
        );
        trailCard.setData(trail, null);
        trailCard.setTrail(trail);
        return trailCard;
    }

    /**
     * Sets up all the events for a given trail card
     * @param trailCard The card that needs to be set up
     * @param trail The respective trail
     */
    private void setupTrailCardHandlers(TrailCardComponent trailCard, Trail trail) {
        trailCard.setOnMouseClicked(e -> onTrailCardClicked(trail));
        trailCard.setOnBookmarkClickedHandler(clickedTrail -> handleBookmarkClick(clickedTrail));
        trailCard.setOnBookmarkFillClickedHandler(clickedTrail -> handleBookmarkFillClick(clickedTrail, trailCard));
        trailCard.setOnTrashClickedHandler(clickedLog -> handleTrashClick(clickedLog, trailCard));
    }

    /**
     * Handles when a user bookmarks a trail
     * @param clickedTrail the trail that the user wants to bookmark
     */
    private void handleBookmarkClick(Trail clickedTrail) {
        TrailLog newLog = new TrailLog(
                trailLogRepo.getNewTrailLogId(),
                clickedTrail.getId(),
                LocalDate.now(),
                null, null, null, null, null, null
        );

        LogTrailController logController = new LogTrailController(
                super.getNavigator(),
                clickedTrail,
                newLog
        );
        super.getNavigator().launchScreen(logController);
    }

    /**
     * Handles when a user clicks on the filled bookmark icon (to unfill)
     * @param clickedTrail The trail that was clicked on
     * @param trailCard The respective card that was clicked on
     */
    private void handleBookmarkFillClick(Trail clickedTrail, TrailCardComponent trailCard) {
        boolean confirmed = showAlert(
                "Delete Log",
                "Are you sure you want to delete this log?",
                "This action cannot be undone.",
                "Delete",
                "Cancel",
                "danger-button"
        );

        if (confirmed) {
            logService.getLogByTrailId(clickedTrail.getId()).ifPresent(log -> {
                logService.deleteLog(log.getId());
                trailCard.setBookmarked(false);
            });
        }
    }

    /**
     * Handles when a user clicks on the trash button, on a log card
     * @param clickedLog The log that was clicked on
     * @param trailCard The respective card
     */
    private void handleTrashClick(TrailLog clickedLog, TrailCardComponent trailCard) {
        logService.deleteLog(clickedLog.getId());
        trailsContainer.getChildren().remove(trailCard);
    }

    @FXML
    private void onTrailCardClicked(Trail trail) {
        super.getNavigator().launchScreen(new ViewTrailController(super.getNavigator(), trail, repo));
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/dashboard.fxml";
    }

    @Override
    protected String getTitle() {
        return "Dashboard";
    }

    @Override
    protected boolean shouldShowNavbar() {
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return 0; // Dashboard is the first tab
    }
}
