package seng202.team5.gui;

import java.util.Objects;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Class that starts the JavaFX application thread.
 */
public class FXAppEntry extends Application {

    /**
     * Creates the application with a {@link ScreenNavigator} for the
     * given {@link Stage}
     *
     * @param primaryStage The current fxml stage, handled by this JavaFX
     *                     Application class
     */
    @Override
    public void start(Stage primaryStage) {
        // font smoothing
        System.setProperty("prism.lcdtext", "false");

        loadCustomFonts();

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/YompYompIcon.png")));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("YompYomp");
        primaryStage.show();

        ScreenNavigator navigator = new ScreenNavigator(primaryStage);
        WelcomeController welcome = new WelcomeController(navigator);
        navigator.launchScreen(welcome, null);
    }

    /**
     * Loads custom Quicksand fonts
     */
    private void loadCustomFonts() {
        try {
            Font.loadFont(getClass().getResourceAsStream("/fonts/Quicksand-Light.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Quicksand-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Quicksand-Medium.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Quicksand-SemiBold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Quicksand-Bold.ttf"), 14);
        } catch (Exception e) {
            System.err.println("Warning: Could not load custom fonts: " + e.getMessage());
        }
    }
}
