package seng202.team5.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import seng202.team5.gui.components.NavbarController;
import seng202.team5.services.UserService;

import java.util.Objects;
import java.util.Optional;

/**
 * Abstract parent class for all UI controller classes.
 */
public abstract class Controller {

    private final ScreenNavigator navigator;
    private final NavbarController navbarController;
    private final UserService userService;

    /**
     * No-argument constructor for FXML compatibility
     */
    protected Controller() {
        this.navigator = null; // Will be set later via setter
        this.navbarController = null; // Will be set later via setter
        this.userService = new UserService(); // Create new instance for FXML controllers
    }

    /**
     * Creates an instance of a ScreenController with the given ScreenNavigator
     *
     * @param navigator The screen navigator used by this ScreenController
     */
    protected Controller(final ScreenNavigator navigator) {
        this.navigator = navigator;
        this.userService = new UserService();
        this.navbarController = new NavbarController(navigator, userService);
    }

    /**
     * Shows an alert dialog to the user.
     *
     * @param type    the type of alert (ERROR, INFORMATION, etc.)
     * @param title   the title of the alert
     * @param content the content message
     */
    public void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setContentText(content);

        alert.setHeaderText(null);
        alert.setGraphic(null);

//        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
//        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));

//        DialogPane dialogPane = alert.getDialogPane();
//        dialogPane.getStylesheets().add(
//                Objects.requireNonNull(getClass().getResource("/styles/global.css")).toExternalForm());

        alert.showAndWait();
    }

    /**
     * Gets the FXML file that will be loaded for this controller.
     *
     * @return The full path to the FXML file for this controller
     */
    protected abstract String getFxmlFile();

    /**
     * Gets the screen title for this controller.
     *
     * @return The title to be displayed for this screen
     */
    protected abstract String getTitle();

    /**
     * Gets the user service associated with this screen controller.
     *
     * @return The user service for this controller
     */
    protected UserService getUserService() {
        return userService;
    }

    /**
     * Gets the screen navigator associated with this screen controller.
     *
     * @return The screen navigator for this controller
     */
    protected ScreenNavigator getNavigator() {
        return navigator;
    }

    /**
     * Gets the navbar controller associated with this screen controller.
     *
     * @return The navbar controller for this controller
     */
    protected NavbarController getNavbarController() {
        return navbarController;
    }

}
