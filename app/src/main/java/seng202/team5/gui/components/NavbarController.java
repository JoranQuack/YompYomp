package seng202.team5.gui.components;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import seng202.team5.Environment;
import seng202.team5.gui.ScreenNavigator;
import seng202.team5.gui.DashboardController;
import seng202.team5.gui.TrailsController;

public class NavbarController extends HBox {

    private List<Button> navButtons;

    // Components
    @FXML
    private Button homeButton;
    @FXML
    private Button trailsButton;
    @FXML
    private Button loggedButton;
    @FXML
    private Button toDoButton;

    /**
     * Initialise the NavbarController and put the buttons into the list to easily
     * switch between them.
     */
    public NavbarController(Environment environment, ScreenNavigator navigator) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/navbar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        navButtons = List.of(homeButton, trailsButton, loggedButton, toDoButton);
        homeButton.setOnAction(e -> navigator.launchScreen(new DashboardController(environment, navigator)));
        trailsButton.setOnAction(e -> navigator.launchScreen(new TrailsController(environment, navigator)));
        // TODO: Implement actions for the remaining buttons when we're ready to rock
    }

    /**
     * Sets the page for the navbar.
     *
     * @param pageIndex The index of the page button to highlight.
     */
    public void setPage(int pageIndex) {
        navButtons.forEach(button -> button.setStyle(""));
        navButtons.get(pageIndex).setStyle("-fx-background-color: #0078D4; -fx-text-fill: white;");
    }
}
