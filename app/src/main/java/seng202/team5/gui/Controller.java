package seng202.team5.gui;

import java.util.Objects;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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

    /**
     * No-argument constructor for FXML compatibility
     */
    protected Controller() {
        this.navigator = null; // Will be set later via setter
        this.navbarController = null; // Will be set later via setter
    }

    /**
     * Creates an instance of a ScreenController with the given ScreenNavigator
     *
     * @param navigator The screen navigator used by this ScreenController
     */
    protected Controller(final ScreenNavigator navigator) {
        this.navigator = navigator;
        this.navbarController = new NavbarComponent(navigator, App.getUserService());
    }

    /**
     * Shows a confirmation dialog with more customisation!
     *
     * @param title           the title of the dialog
     * @param headerText      the header text of the dialog
     * @param contentText     the content message
     * @param confirmText     the text for the confirm button
     * @param cancelText      the text for the cancel button
     * @param confirmCssClass optional CSS class for the confirm button
     * @return true if the user clicked confirm, false otherwise
     */
    public boolean showAlert(String title, String headerText, String contentText,
            String confirmText, String cancelText, String confirmCssClass) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(navigator.getPrimaryStage());
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons()
                .add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/YompYompIcon.png"))));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        dialogPane.setGraphic(null);

        ButtonType confirmButton = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
        if (cancelText == null) {
            alert.getButtonTypes().setAll(confirmButton);
        } else {
            ButtonType cancelButton = new ButtonType(cancelText, ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(confirmButton, cancelButton);
        }

        // Apply CSS class to confirm button if provided
        if (confirmCssClass != null && !confirmCssClass.isEmpty()) {
            alert.getDialogPane().lookupButton(confirmButton).getStyleClass().add(confirmCssClass);
        }

        return alert.showAndWait()
                .map(response -> response == confirmButton)
                .orElse(false);
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
