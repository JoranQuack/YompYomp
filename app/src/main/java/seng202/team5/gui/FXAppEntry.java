package seng202.team5.gui;

import java.util.Objects;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import seng202.team5.Environment;

/**
 * Class that starts the JavaFX application thread.
 */
public class FXAppEntry extends Application {

    /**
     * Creates the {@link Environment} with a {@link ScreenNavigator} for the
     * given {@link Stage}
     *
     * @param primaryStage The current fxml stage, handled by this JavaFX
     *                     Application class
     */
    @Override
    public void start(Stage primaryStage) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/YompYompIcon.png")));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("YompYomp");
        primaryStage.show();

        new Environment(new ScreenNavigator(primaryStage));
    }
}
