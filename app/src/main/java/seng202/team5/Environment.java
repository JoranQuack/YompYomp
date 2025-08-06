package seng202.team5;

import seng202.team5.gui.ScreenNavigator;
import seng202.team5.models.Trail;
import seng202.team5.services.DataService;

// import java.net.URL;
import java.util.List;

public class Environment {

    private final ScreenNavigator navigator;

    // Created a dataservice variable
    private static DataService dataService;

    private List<Trail> trails;

    /**
     * Constructor for the GameEnvironment class. Initializes the game environment
     * with a ScreenNavigator instance. Sets the initial bank balance, available
     * cars and parts, and initializes some game data.
     *
     * @param navigator The ScreenNavigator instance for navigating between screens
     */
    public Environment(ScreenNavigator navigator) {
        this.navigator = navigator;

        // temporary --------
        String currentPath = System.getProperty("user.dir");
        String fullPath = currentPath
                + "/app/src/main/resources/datasets/DOC_Walking_Experiences_7994760352369043452.csv";
        dataService = new DataService(fullPath);
        // trails list :)
        trails = dataService.getTrails();

        navigator.launchTrailsScreen(this);

        // navigator.launchStartScreen(this);
    }

    public List<Trail> getTrails() {
        return trails;
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
