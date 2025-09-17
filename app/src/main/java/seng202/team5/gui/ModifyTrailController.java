package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.controlsfx.control.CheckComboBox;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

import java.util.ArrayList;
import java.util.List;

public class ModifyTrailController extends Controller {

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
     */
    public ModifyTrailController(ScreenNavigator navigator) {
        super(navigator);
    }

    @FXML
    private TextField trailNameTextField;
    @FXML
    private TextField translationTextField;
    @FXML
    private CheckComboBox<String> regionCheckComboBox;
    @FXML
    private TextField trailDescriptionTextField;
    @FXML
    private ImageView mapImage;
    @FXML
    private Button saveButton;

    @FXML
    private void initialize() {
        List<String> regionList = new ArrayList<>(List.of("Northland", "Auckland",
                "Waikato", "Bay of Plenty", "Gisborne", "Hawke's Bay", "Taranaki",
                "Manawatu-Whanganui", "Tasman", "Wellington", "Nelson", "Marlborough", "West Coast",
                "Canterbury", "Otago", "Southland"));
        regionCheckComboBox.getItems().addAll(regionList);
        saveButton.setOnAction(e -> onSaveButtonClicked());
    }

    @FXML
    private void onSaveButtonClicked() {
        DatabaseService databaseService = new DatabaseService();
        String databasePath = databaseService.getDatabasePath();
        SqlBasedTrailRepo sqlBasedTrailRepo = new SqlBasedTrailRepo(databaseService);
        Trail updatedTrail = new Trail();

        sqlBasedTrailRepo.upsert(updatedTrail);
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/modify_trail_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "Modify Trail Screen";
    }
}
