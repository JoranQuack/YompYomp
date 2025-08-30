package seng202.team5.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class that handles navigation between various {@link Controller}s. This
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
        Scene scene = new Scene(rootPane, 1200, 800);
        stage.setScene(scene);

        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.setResizable(true);
        rootPane.prefWidthProperty().bind(scene.widthProperty());
        rootPane.prefHeightProperty().bind(scene.heightProperty());
    }

    /**
     * Opens a web page in the default browser using the provided URL.
     *
     * @param url The URL of the web page to open
     */
    public void openWebPage(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (IOException e) {
            System.err.println("Failed to open web page: " + e.getMessage());
        }
    }

    /**
     * Replaces the root border pane's center component with the screen defined by
     * the given
     * {@link Controller}.
     *
     * @param controller The JavaFX screen controller for the screen to be launched
     */
    public void launchScreen(Controller controller) {
        try {
            FXMLLoader setupLoader = new FXMLLoader(getClass().getResource(controller.getFxmlFile()));
            setupLoader.setControllerFactory(param -> controller);
            Parent setupParent = setupLoader.load();
            rootPane.setCenter(setupParent);
            stage.setTitle(controller.getTitle());
        } catch (IOException e) {
            System.err.println("Could not load screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
