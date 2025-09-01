package seng202.team5.gui.components;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class NavbarController extends HBox {

    // UI Components
    @FXML
    private Button homeButton;
    @FXML
    private Button trailsButton;
    @FXML
    private Button loggedButton;
    @FXML
    private Button toDoButton;

    private List<Button> navButtons;

    /**
     * Initialise the NavbarController and put the buttons into the list to easily
     * switch between them.
     */
    public NavbarController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/navbar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            navButtons = List.of(homeButton, trailsButton, loggedButton, toDoButton);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
