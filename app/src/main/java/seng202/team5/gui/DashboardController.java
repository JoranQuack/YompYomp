package seng202.team5.gui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seng202.team5.App;
import seng202.team5.gui.components.TrailCardComponent;
import seng202.team5.models.Trail;
import seng202.team5.services.DashboardService;

/**
 * Controller for the dashboard screen.
 */
public class DashboardController extends Controller {
    /** Service for dashboarding trails */
    private final DashboardService dashboardService;

    @FXML
    private FlowPane trailsContainer;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchBarTextField;

    @FXML
    private Hyperlink DOCLink;

    @FXML
    private CheckBox regionCheckBox;

    /**
     * Creates controller with navigator.
     *
     * @param navigator Screen navigator
     */
    public DashboardController(ScreenNavigator navigator) {
        super(navigator);
        this.dashboardService = new DashboardService(App.getTrailRepo());
    }

    /**
     * Initialises the trails view with default data.
     */
    @FXML
    private void initialize() {

        initializeTrails();

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
        super.getNavigator().launchScreen(new TrailsController(super.getNavigator()));
    }

    @FXML
    private void onAddTrailButtonClicked() {
        super.getNavigator().launchScreen(new ModifyTrailController(super.getNavigator(), null));
    }

    @FXML
    private void onSearchButtonClicked() {
        super.getNavigator()
                .launchScreen(new TrailsController(super.getNavigator(), searchBarTextField.getText()));
    }

    /**
     * Updates display with trail cards. Depending on the situation
     *
     */
    private void initializeTrails() {
        List<Trail> trails;
        if (App.getUserService().isGuest()) {
            regionCheckBox.setVisible(false);
            trails = dashboardService.getRandomTrails();
        } else if (!regionCheckBox.isSelected()) {
            regionCheckBox.setVisible(true);
            trails = dashboardService.getRecommendedTrails();
        } else {
            regionCheckBox.setVisible(true);
            trails = dashboardService.getPopularTrailsByRegions(App.getUserService().getUser().getRegion());
        }
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
     *
     * @param trail The trail that the card needs to be created for
     * @return the Trail card
     */
    private TrailCardComponent createTrailCard(Trail trail) {
        TrailCardComponent trailCard = new TrailCardComponent(
                App.getUserService().isGuest(), false, false, super.getNavigator());
        trailCard.setData(trail, null);
        return trailCard;
    }

    /**
     * Sets up all the events for a given trail card
     *
     * @param trailCard The card that needs to be set up
     * @param trail     The respective trail
     */
    private void setupTrailCardHandlers(TrailCardComponent trailCard, Trail trail) {
        trailCard.setOnMouseClicked(e -> onTrailCardClicked(trail));
    }

    @FXML
    private void onTrailCardClicked(Trail trail) {
        super.getNavigator().launchScreen(new ViewTrailController(super.getNavigator(), trail));
    }

    /**
     * When the regions checkbox is toggled re call the method that shows the trails
     */
    @FXML
    private void onRegionCheckBoxToggle() {
        initializeTrails();
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
