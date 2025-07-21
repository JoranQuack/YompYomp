package seng202.team5.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import seng202.team5.Environment;

/**
 * Class that starts the JavaFX application thread.
 */
public class FXAppEntry extends Application {

    /**
     * Constructs a new {@link FXAppEntry} instance.
     */
    public FXAppEntry() {
        // No initialization required
    }

    /**
     * Creates the {@link Environment} with a {@link ScreenNavigator} for the
     * given {@link Stage}
     *
     * @param primaryStage The current fxml stage, handled by this JavaFX
     *                     Application class
     */
    @Override
    public void start(Stage primaryStage) {
        // TODO: Uncomment and set the icon when available
        // Image icon = new
        // Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png")));
        // primaryStage.getIcons().add(icon);
        primaryStage.setTitle("YompYomp");
        primaryStage.show();

        new Environment(new ScreenNavigator(primaryStage));
    }
}
