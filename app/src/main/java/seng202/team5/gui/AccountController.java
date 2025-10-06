package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng202.team5.gui.components.NavbarComponent;
import seng202.team5.models.Question;
import seng202.team5.models.User;

import java.util.List;

public class AccountController extends Controller {

    @FXML
    private VBox navbarContainer;
    @FXML
    private ImageView profileImage;
    @FXML
    private ImageView clearProfileImage;
    @FXML
    private ImageView optionImage1;
    @FXML
    private ImageView optionImage2;
    @FXML
    private ImageView optionImage3;
    @FXML
    private ImageView optionImage4;
    @FXML
    private ImageView optionImage5;
    @FXML
    private ImageView optionImage6;
    @FXML
    private ImageView optionImage7;
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
    private Button redoQuizButton;
    @FXML
    private Button deleteProfileButton;
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
        NavbarComponent navbar = super.getNavbarController();
        navbar.setPage(2);
        navbarContainer.getChildren().add(navbar);

        welcomeLabel.setText("Kia Ora " + user.getName() + "!");
        setPreferenceLabels();

        List<ImageView> optionImages = List.of(optionImage1, optionImage2, optionImage3, optionImage4,
                optionImage5, optionImage6, optionImage7);
        for (ImageView optionImage : optionImages) {
            optionImage.setOnMouseClicked(e -> onOptionImageClicked(optionImage));
        }
        clearProfileImage.setOnMouseClicked(e -> onClearProfileImageClicked());
        redoQuizButton.setOnAction(e -> onRedoQuizButtonClicked());
        deleteProfileButton.setOnAction(e -> onDeleteProfileButtonClicked());
    }

    @FXML
    private void onRedoQuizButtonClicked() {
        super.getNavigator().launchScreen(new ProfileSetupGeneralController(super.getNavigator()));
    }

    @FXML
    private void onDeleteProfileButtonClicked() {
        super.getUserService().clearUser();
        super.getUserService().setGuest(true);
        super.getNavigator().launchScreen(new DashboardController(super.getNavigator()));
    }

    @FXML
    private void onClearProfileImageClicked() {
        profileImage.setImage(new Image("./images/profiles/user.png"));
    }

    @FXML
    private void onOptionImageClicked(ImageView optionImage) {
        profileImage.setImage(optionImage.getImage());
        user.setProfilePicture(optionImage.getImage().getUrl());
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

    @Override
    protected boolean shouldShowNavbar() {
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return 2;
    }

    @Override
    public void onLoadFailed(Exception e) {
        showAlert("Account Failed To Load",
                "Account page failed to load, please close and reload the application.",
                "", "OK", null, null);
    }
}
