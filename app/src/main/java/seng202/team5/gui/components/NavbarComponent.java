package seng202.team5.gui.components;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.gui.*;
import seng202.team5.services.UserService;

public class NavbarComponent extends HBox {

    private List<Button> navButtons;

    // Components
    @FXML
    private Button homeButton;
    @FXML
    private Button trailsButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button redoQuizButton;

    /**
     * Initialise the NavbarController and put the buttons into the list to easily
     * switch between them.
     *
     * @param navigator         the screen navigator
     * @param userService       the userService
     * @param sqlBasedTrailRepo the trail repo
     */
    public NavbarComponent(ScreenNavigator navigator, UserService userService, SqlBasedTrailRepo sqlBasedTrailRepo) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/navbar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (userService.isGuest()) {
            redoQuizButton.setText("Take Quiz");
        } else {
            redoQuizButton.setText("Redo Quiz");
        }

        navButtons = List.of(homeButton, trailsButton, profileButton);
        homeButton.setOnAction(e -> navigator.launchScreen(new DashboardController(navigator)));
        trailsButton.setOnAction(e -> navigator.launchScreen(new TrailsController(navigator, sqlBasedTrailRepo)));
        profileButton.setOnAction(e -> navigator.launchScreen(new AccountController(navigator)));
        redoQuizButton.setOnAction(e -> navigator.launchScreen(new ProfileSetupGeneralController(navigator)));
    }

    /**
     * Sets the page for the navbar.
     *
     * @param pageIndex The index of the page button to highlight.
     */
    public void setPage(int pageIndex) {
        navButtons.forEach(button -> button.getStyleClass().remove("active"));
        navButtons.get(pageIndex).getStyleClass().add("active");
    }

    @FXML
    private void onLogoClicked() {
        homeButton.fire();
    }

    public Button getProfileButton() {
        return profileButton;
    }
}
