package seng202.team5.gui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seng202.team5.Environment;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.gui.components.NavbarController;
import seng202.team5.gui.components.TrailCardController;
import seng202.team5.models.Trail;
import seng202.team5.services.SearchService;

/**
 * Controller for the dashboard screen.
 */
public class DashboardController extends Controller {
    /** Service for searching and filtering trails */
    private SearchService searchService;

    @FXML
    private VBox navbarContainer;

    @FXML
    private FlowPane trailsContainer;

    /**
     * Default constructor required by JavaFX FXML loading.
     */
    public DashboardController() {
        super();
    }

    /**
     * Creates controller with environment.
     *
     * @param environment Application environment
     * @param navigator   Screen navigator
     */
    public DashboardController(Environment environment, ScreenNavigator navigator) {
        super(environment, navigator);
        initializeSearchService();
    }

    /**
     * Initializes the search service.
     */
    private void initializeSearchService() {
        this.searchService = new SearchService(new SqlBasedTrailRepo(new DatabaseService()));
        searchService.setMaxResults(8);
    }

    /**
     * Initialises the trails view with default data.
     */
    @FXML
    private void initialize() {
        // Initialize the navbar
        NavbarController navbar = super.getNavbarController();
        navbar.setPage(0);
        navbarContainer.getChildren().add(navbar);

        // Initialize search service if not already done
        if (searchService == null) {
            initializeSearchService();
        }

        // TODO: use proper recommended trail fetching once implemented
        List<Trail> trails = searchService.getTrails(null, 0);
        initializeRecommendedTrails(trails);
    }

    @FXML
    private void onViewAllClicked() {
        super.getNavigator().launchScreen(new TrailsController(super.getEnvironment(), super.getNavigator()));
    }

    /**
     * Updates display with trail cards.
     *
     * @param trails List of trails to display
     */
    private void initializeRecommendedTrails(List<Trail> trails) {
        trailsContainer.getChildren().clear();

        for (Trail trail : trails) {
            TrailCardController trailCard = new TrailCardController();
            trailCard.setData(trail);

            // Add some spacing between cards
            VBox.setMargin(trailCard, new Insets(10));

            trailsContainer.getChildren().add(trailCard);
        }
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/dashboard.fxml";
    }

    @Override
    protected String getTitle() {
        return "Dashboard";
    }
}
