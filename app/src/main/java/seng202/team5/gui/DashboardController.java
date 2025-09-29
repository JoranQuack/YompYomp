package seng202.team5.gui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.gui.components.TrailCardComponent;
import seng202.team5.models.Trail;
import seng202.team5.services.SearchService;

/**
 * Controller for the dashboard screen.
 */
public class DashboardController extends Controller {
    /** Service for searching and filtering trails */
    private SearchService searchService;

    @FXML
    private FlowPane trailsContainer;

    @FXML
    private Button addTrailButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchBarTextField;

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
        // Initialize search service if not already done
        if (searchService == null) {
            initializeSearchService();
        }

        SqlBasedTrailRepo repo = new SqlBasedTrailRepo(new DatabaseService());
        List<Trail> trails = repo.getRecommendedTrails();
        initializeRecommendedTrails(trails);
        addTrailButton.setOnAction(e -> onAddTrailButtonClicked());
    }

    @FXML
    private void onViewAllClicked() {
        super.getNavigator().launchScreen(new TrailsController(super.getNavigator()), this);
    }

    @FXML
    private void onAddTrailButtonClicked() {
        super.getNavigator().launchScreen(new ModifyTrailController(super.getNavigator(), null, searchService), this);
    }

    @FXML
    private void onSearchButtonClicked() {
        super.getNavigator().launchScreen(new TrailsController(super.getNavigator(), searchBarTextField.getText()), this);
    }

    /**
     * Updates display with trail cards.
     *
     * @param trails List of trails to display
     */
    private void initializeRecommendedTrails(List<Trail> trails) {
        trailsContainer.getChildren().clear();

        for (Trail trail : trails) {
            TrailCardComponent trailCard = new TrailCardComponent(super.getUserService().isGuest());
            trailCard.setData(trail);

            // Add some spacing between cards
            VBox.setMargin(trailCard, new Insets(10));

            trailsContainer.getChildren().add(trailCard);
            trailCard.setOnMouseClicked(e -> onTrailCardClicked(trail));
        }
    }

    @FXML
    private void onTrailCardClicked(Trail trail) {
        super.getNavigator().launchScreen(new ViewTrailController(super.getNavigator(), trail, searchService), this);
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
