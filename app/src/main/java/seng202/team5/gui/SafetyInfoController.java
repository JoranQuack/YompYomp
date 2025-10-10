package seng202.team5.gui;

import javafx.fxml.FXML;

public class SafetyInfoController extends Controller {


    public SafetyInfoController(ScreenNavigator navigator) {
        super(navigator);
    }

    @FXML
    private void onLinkClicked() {
        super.getNavigator().openWebPage("https://www.mountainsafety.org.nz/learn/skills/emergency");
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/safety_info.fxml";
    }

    @Override
    public String getTitle() {
        return "Safety Information";
    }

    @Override
    protected boolean shouldShowNavbar() {
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return 3;
    }
}
