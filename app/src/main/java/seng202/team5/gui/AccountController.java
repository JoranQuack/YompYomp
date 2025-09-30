package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import seng202.team5.gui.components.NavbarComponent;

public class AccountController extends Controller {

    @FXML
    private VBox navBarContainer;

    /**
     * Default constructor required by JavaFX FXML loading.
     */
    public AccountController() {
        super();
    }

    /**
     * Creates controller with navigator.
     *
     * @param navigator Screen navigator
     */
    public AccountController(ScreenNavigator navigator) {
        super(navigator);
    }

    @FXML
    private void initialize() {
        NavbarComponent navbar = super.getNavbarController();
        navbar.setPage();
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/account_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "Account Screen";
    }
}
