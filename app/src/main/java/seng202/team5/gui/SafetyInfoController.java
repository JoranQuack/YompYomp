package seng202.team5.gui;

public class SafetyInfoController extends Controller {


    public SafetyInfoController(ScreenNavigator navigator) {
        super(navigator);
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
