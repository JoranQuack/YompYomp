package seng202.team5;

import seng202.team5.gui.ScreenNavigator;
import seng202.team5.gui.WelcomeController;
import seng202.team5.models.User;

public class Environment {

    private final ScreenNavigator navigator;
    private User user = new User();

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

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
