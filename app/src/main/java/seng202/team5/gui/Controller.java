package seng202.team5.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import seng202.team5.App;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.gui.components.NavbarComponent;
import seng202.team5.services.UserService;

/**
 * Abstract parent class for all UI controller classes.
 */
public abstract class Controller {

    private final ScreenNavigator navigator;
    private final NavbarComponent navbarController;
    private final SqlBasedTrailRepo sqlBasedTrailRepo;

    /**
     * No-argument constructor for FXML compatibility
     */
    protected Controller() {
        this.navigator = null; // Will be set later via setter
        this.navbarController = null; // Will be set later via setter
        this.sqlBasedTrailRepo = new SqlBasedTrailRepo(App.getDatabaseService());
    }

    /**
     * Creates an instance of a ScreenController with the given ScreenNavigator
     *
     * @param navigator The screen navigator used by this ScreenController
     */
    protected Controller(final ScreenNavigator navigator) {
        this.navigator = navigator;
        this.sqlBasedTrailRepo = new SqlBasedTrailRepo(App.getDatabaseService());
        this.navbarController = new NavbarComponent(navigator, App.getUserService(), sqlBasedTrailRepo);
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

        // Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        // stage.getIcons().add(new
        // Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));

        // DialogPane dialogPane = alert.getDialogPane();
        // dialogPane.getStylesheets().add(
        // Objects.requireNonNull(getClass().getResource("/styles/global.css")).toExternalForm());

        alert.showAndWait();
    }

    /**
     * Called when the navigator fails to load the FXML file associated
     * with this controller
     *
     * By default, this method logs the exception details to the error
     * output. Controllers may override this method to provide
     * screen-specific error handling, such as displaying an alert
     * message to the user
     *
     * @param e the exception thrown while attempting to load the screen
     */
    public void onLoadFailed(Exception e) {
        // default: just log
        System.err.println("Failed to load screen: " + e.getMessage());
        e.printStackTrace();
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
        return App.getUserService();
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
    protected NavbarComponent getNavbarController() {
        return navbarController;
    }

    /**
     * Determines whether this screen should show the navbar.
     *
     * @return true if the navbar should be shown
     */
    protected abstract boolean shouldShowNavbar();

    /**
     * Gets the page index for the navbar highlighting.
     *
     * @return the index of the navbar button to highlight (0 for Dashboard, 1 for
     *         Trails, and -1 for none)
     */
    protected abstract int getNavbarPageIndex();

    /**
     * Saves the current state of this controller.
     * Override this to preserve controller-specific state.
     */
    public void saveState() {
        // Default does nothing
    }

    /**
     * Restores the previously saved state of this controller.
     * Override this to restore controller-specific state.
     */
    public void restoreState() {
        // Default does nothing
    }

}
