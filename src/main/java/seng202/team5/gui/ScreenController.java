package seng202.team5.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import seng202.team5.Environment;

import java.util.Objects;

/**
 * Abstract parent class for all {@link Environment} UI controller classes.
 */
public abstract class ScreenController {

    /**
     * The {@link Environment} instance used by this controller.
     */
    private final Environment Environment;

    /**
     * Creates an instance of a ScreenController with the given
     * {@link GameEnvironment}
     *
     * @param gameEnvironment The game environment used by this ScreenController
     */
    protected ScreenController(final Environment Environment) {
        this.Environment = Environment;
    }

    /**
     * Shows an alert dialog to the user
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

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/styles/global.css")).toExternalForm());

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
     * Gets the game environment associated with this screen controller.
     *
     * @return The game environment for this controller
     */
    protected Environment getEnvironment() {
        return Environment;
    }
}
