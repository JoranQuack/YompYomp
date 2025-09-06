package seng202.team5.gui;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.services.SetupService;

/**
 * Class that starts the JavaFX application thread.
 */
public class FXAppEntry extends Application {

    private ExecutorService setupExec;

    /**
     * Creates the application with a {@link ScreenNavigator} for the
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

        ScreenNavigator navigator = new ScreenNavigator(primaryStage);
        WelcomeController welcome = new WelcomeController(navigator);
        navigator.launchScreen(welcome);
    }
}
