package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng202.team5.gui.components.NavbarComponent;

public class AccountController extends Controller {

    @FXML
    private VBox navBarContainer;
    @FXML
    private ImageView profileImage;
    @FXML
    private ImageView optionImage1;
    @FXML
    private ImageView optionImage2;
    @FXML
    private ImageView optionImage3;
    @FXML
    private ImageView optionImage4;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label familyFriendlyLabel;
    @FXML
    private Label accessibleLabel;
    @FXML
    private Label experienceLabel;
    @FXML
    private Label gradientLabel;
    @FXML
    private Label bushLabel;
    @FXML
    private Label reserveLabel;
    @FXML
    private Label lakeRiverLabel;
    @FXML
    private Label coastLabel;
    @FXML
    private Label mountainLabel;
    @FXML
    private Label wildlifeLabel;
    @FXML
    private Label historicLabel;
    @FXML
    private Label waterfallLabel;
    @FXML
    private PieChart logTrailPieChart;
    @FXML
    private HBox legendLabelContainer1;
    @FXML
    private HBox legendLabelContainer2;
    @FXML
    private HBox legendLabelContainer3;
    @FXML
    private HBox legendLabelContainer4;
    @FXML
    private HBox legendLabelContainer5;

    /**
     * Default constructor required by JavaFX FXML loading.
     */
    public AccountController() {
        super();
    }

    /**
     * Creates controller with navigator.
     *
     * @param navigator Screen navigator
     */
    public AccountController(ScreenNavigator navigator) {
        super(navigator);
    }

    @FXML
    private void initialize() {
        // Initialize the navbar
        NavbarComponent navbar = super.getNavbarController();
        navbar.setPage(2);
        navBarContainer.getChildren().add(navbar);
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/account_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "Account Screen";
    }
}
