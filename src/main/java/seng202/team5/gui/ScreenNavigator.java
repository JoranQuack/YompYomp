package seng202.team5.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import seng202.team5.Environment;

import java.io.IOException;

/**
 * Class that handles navigation between various {@link ScreenController}s. This
 * navigator
 * uses a {@link BorderPane} layout for the root pane. A launched screen is
 * placed in the
 * center area of the border pane, replacing the previous screen if any.
 */
public class ScreenNavigator {

    private final Stage stage;
    private final BorderPane rootPane;

    /**
     * Constructor for ScreenNavigator
     *
     * @param stage The primary stage for the application
     */
    public ScreenNavigator(Stage stage) {
        this.stage = stage;
        this.rootPane = new BorderPane();
        Scene scene = new Scene(rootPane, 800, 600); // TODO: Adjust dimensions as needed
        stage.setScene(scene);
    }

    /**
     * Launches the start screen of the application
     *
     * @param environment The environment instance
     */
    public void launchStartScreen(Environment environment) {
        // TODO: Implement the logic to launch the start screen
    }

    /**
     * Replaces the root border pane's center component with the screen defined by
     * the given
     * {@link ScreenController}.
     *
     * @param controller The JavaFX screen controller for the screen to be launched
     */
    public void launchScreen(ScreenController controller) {
        try {
            FXMLLoader setupLoader = new FXMLLoader(getClass().getResource(controller.getFxmlFile()));
            setupLoader.setControllerFactory(param -> controller);
            Parent setupParent = setupLoader.load();
            rootPane.setCenter(setupParent);
            stage.setTitle(controller.getTitle());
        } catch (IOException e) {
            System.err.println("Could not load screen: " + e.getMessage());
        }
    }
}
