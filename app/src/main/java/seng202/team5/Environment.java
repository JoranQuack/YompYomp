package seng202.team5;

import seng202.team5.gui.ScreenNavigator;
import seng202.team5.gui.WelcomeController;

public class Environment {

    private final ScreenNavigator navigator;

    /**
     * Constructor for the Environment class. Initializes the environment
     * with a ScreenNavigator instance.
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
