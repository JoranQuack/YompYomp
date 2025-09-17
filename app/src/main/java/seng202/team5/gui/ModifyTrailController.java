package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
    private ComboBox<String> regionComboBox;
    @FXML
    private ComboBox<String> difficultyComboBox;
    @FXML
    private ComboBox<String> trailTypeComboBox;
    @FXML
    private TextField completionTimeTextField;
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
        regionComboBox.getItems().addAll(regionList);
        difficultyComboBox.getItems().addAll(List.of("Easy", "Intermediate", "Advanced"));
        trailTypeComboBox.getItems().addAll(List.of("One way", "Loop", "Return"));
        saveButton.setOnAction(e -> onSaveButtonClicked());
    }

    @FXML
    private void onSaveButtonClicked() {
        DatabaseService databaseService = new DatabaseService();
        SqlBasedTrailRepo sqlBasedTrailRepo = new SqlBasedTrailRepo(databaseService);
        String trailName = trailNameTextField.getText();
        String translation = translationTextField.getText();
        String region = regionComboBox.getValue();
        String difficulty = difficultyComboBox.getValue();
        String trailType = trailTypeComboBox.getValue();
        String completionTime = completionTimeTextField.getText();
        String trailDescription = trailDescriptionTextField.getText();
        Trail updatedTrail = new Trail(trailName, difficulty, trailDescription, completionTime, trailType);
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
