package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng202.team5.gui.components.NavbarComponent;
import seng202.team5.models.Question;
import seng202.team5.models.User;

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

    User user;

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
        this.user = getUserService().getUser();
    }

    @FXML
    private void initialize() {
        // Initialize the navbar
        NavbarComponent navbar = super.getNavbarController();
        navbar.setPage(2);
        navBarContainer.getChildren().add(navbar);

        welcomeLabel.setText("Kia Ora " + user.getName() + "!");
        setPreferenceLabels();
    }

    @FXML
    private void setPreferenceLabels() {
        familyFriendlyLabel.setText(user.isFamilyFriendly() ? "Yes" : "No");
        accessibleLabel.setText(user.isAccessible() ? "Yes" : "No");
        experienceLabel.setText(getPreferenceLabel(user.getExperienceLevel(), Question.ONE.sliderLabels));
        gradientLabel.setText(getPreferenceLabel(user.getGradientPreference(), Question.TWO.sliderLabels));
        bushLabel.setText(getPreferenceLabel(user.getBushPreference(), Question.THREE.sliderLabels));
        reserveLabel.setText(getPreferenceLabel(user.getReservePreference(), Question.FOUR.sliderLabels));
        lakeRiverLabel.setText(getPreferenceLabel(user.getLakeRiverPreference(), Question.FIVE.sliderLabels));
        coastLabel.setText(getPreferenceLabel(user.getCoastPreference(), Question.SIX.sliderLabels));
        mountainLabel.setText(getPreferenceLabel(user.getMountainPreference(), Question.SEVEN.sliderLabels));
        wildlifeLabel.setText(getPreferenceLabel(user.getWildlifePreference(), Question.EIGHT.sliderLabels));
        historicLabel.setText(getPreferenceLabel(user.getHistoricPreference(), Question.NINE.sliderLabels));
        waterfallLabel.setText(getPreferenceLabel(user.getWaterfallPreference(), Question.TEN.sliderLabels));
    }

    /**
     * Returns string label to be set for each user preference
     * @param preference
     * @param sliderLabels
     * @return string to be set for user preference label
     */
    private String getPreferenceLabel(int preference, String[] sliderLabels) {
        if (preference == 1) {
            return sliderLabels[0];
        } else if (preference == 5) {
            return sliderLabels[2];
        } else {
            return sliderLabels[1];
        }
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
