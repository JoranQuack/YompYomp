package seng202.team5;

import seng202.team5.gui.ScreenNavigator;
import seng202.team5.gui.WelcomeController;

public class Environment {

    private final ScreenNavigator navigator;

    /**
     * Constructor for the GameEnvironment class. Initializes the game environment
     * with a ScreenNavigator instance. Sets the initial bank balance, available
     * cars and parts, and initializes some game data.
     *
     * @param navigator The ScreenNavigator instance for navigating between screens
     */
    public Environment(ScreenNavigator navigator) {
        this.navigator = navigator;
        navigator.launchScreen(new WelcomeController(this, navigator));
    }

    /**
     * Gets the screen navigator for this environment.
     *
     * @return The ScreenNavigator instance
     */
    public ScreenNavigator getNavigator() {
        return navigator;
    }
}
