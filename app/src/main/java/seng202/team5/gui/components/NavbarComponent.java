package seng202.team5.gui.components;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import seng202.team5.gui.*;
import seng202.team5.services.UserService;

public class NavbarComponent extends HBox {

    private final List<Button> navButtons;

    // Components
    @FXML
    private ImageView backButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button trailsButton;
    @FXML
    private ImageView profileImage;
    @FXML
    private Button takeQuizButton;
    @FXML
    private Button logbookButton;
    @FXML
    private Button safetyButton;

    /**
     * Initialise the NavbarController and put the buttons into the list to easily
     * switch between them.
     *
     * @param navigator   the screen navigator
     * @param userService the userService
     */
    public NavbarComponent(ScreenNavigator navigator, UserService userService) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/navbar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (userService.isGuest()) {
            takeQuizButton.setDisable(false);
            takeQuizButton.setVisible(true);
            profileImage.setDisable(true);
            profileImage.setVisible(false);
            takeQuizButton.setOnAction(e -> navigator.launchScreen(new ProfileSetupGeneralController(navigator)));
        } else {
            profileImage.setDisable(false);
            profileImage.setVisible(true);
            takeQuizButton.setDisable(true);
            takeQuizButton.setVisible(false);
            if (userService.getUser() != null) {
                String profileUrl = userService.getUser().getProfilePicture();
                if (profileUrl == null) {
                    profileUrl = "/images/profiles/user.png";
                }
                profileImage.setImage(new Image(profileUrl));
            }
            profileImage.setOnMouseClicked(e -> navigator.launchScreen(new AccountController(navigator)));
        }

        navButtons = List.of(homeButton, trailsButton, logbookButton, safetyButton);
        homeButton.setOnAction(e -> navigator.launchScreen(new DashboardController(navigator)));
        trailsButton.setOnAction(e -> navigator.launchScreen(new TrailsController(navigator)));
        logbookButton.setOnAction(e -> navigator.launchScreen(new LogBookController(navigator)));
        safetyButton.setOnAction(e -> navigator.launchScreen(new SafetyInfoController(navigator)));
        if (navigator.hasPreviousScreen()) {
            backButton.setOnMouseClicked(e -> navigator.goBack());
            backButton.setVisible(true);
            Tooltip.install(backButton, new Tooltip("Go Back"));
        } else {
            backButton.setVisible(false);
        }
    }

    /**
     * Sets the page for the navbar.
     *
     * @param pageIndex The index of the page button to highlight. Use -1 for no
     *                  active button.
     */
    public void setPage(int pageIndex) {
        navButtons.forEach(button -> button.getStyleClass().remove("active"));
        if (pageIndex >= 0 && pageIndex < navButtons.size()) {
            navButtons.get(pageIndex).getStyleClass().add("active");
        }
    }

    @FXML
    private void onLogoClicked() {
        homeButton.fire();
    }

    public ImageView getProfileImage() {
        return profileImage;
    }
}
