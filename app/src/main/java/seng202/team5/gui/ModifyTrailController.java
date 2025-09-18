package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the modify trail screen
 */
public class ModifyTrailController extends Controller {

    private Trail trail;
    private Controller lastController;

    /**
     * Launches the screen with navigator
     *
     * @param navigator screen navigator
     * @param lastController controller of last screen user interacted with
     */
    public ModifyTrailController(ScreenNavigator navigator, Trail trail, Controller lastController) {
        super(navigator);
        this.trail = trail;
        this.lastController = lastController;
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
    private TextArea trailDescriptionTextArea;
    @FXML
    private ImageView mapImage;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;

    /**
     * Initialises the screen with components for user to input data
     * Prefills the boxes if user is updating an existing trail
     * Else leaves boxes blank
     */
    @FXML
    private void initialize() {
        if (trail != null) {
            initializeTextFields();
        }
        List<String> regionList = new ArrayList<>(List.of("Northland", "Auckland",
                "Waikato", "Bay of Plenty", "Gisborne", "Hawke's Bay", "Taranaki",
                "Manawatu-Whanganui", "Tasman", "Wellington", "Nelson", "Marlborough", "West Coast",
                "Canterbury", "Otago", "Southland"));
        regionComboBox.getItems().addAll(regionList);
        difficultyComboBox.getItems().addAll(List.of("easy", "intermediate", "advanced"));
        trailTypeComboBox.getItems().addAll(List.of("one way", "loop", "return"));
        saveButton.setOnAction(e -> onSaveButtonClicked());
        backButton.setOnAction(e -> onBackButtonClicked());
    }

    @FXML
    private void onSaveButtonClicked() {
        DatabaseService databaseService = new DatabaseService();
        SqlBasedTrailRepo sqlBasedTrailRepo = new SqlBasedTrailRepo(databaseService);
        sqlBasedTrailRepo.upsert(getUpdatedTrail());
        super.getNavigator().launchScreen(lastController, lastController.getNavigator().getLastController());
    }

    @FXML
    private void onBackButtonClicked() {
        super.getNavigator().launchScreen(lastController, lastController.getNavigator().getLastController());
    }

    /**
     * Prefills boxes with existing data of the trail
     */
    @FXML
    private void initializeTextFields() {
        trailNameTextField.setText(trail.getName());
        difficultyComboBox.setValue(trail.getDifficulty());
        //trailTypeComboBox.setValue(trail.getType());
        //completionTimeTextField.setText(trail.getCompletionTime());
        trailDescriptionTextArea.setText(trail.getDescription());
    }

    /**
     * Returns Trail object of trail to be updated/added to database
     * @return updatedTrail
     */
    private Trail getUpdatedTrail() {
        int trailId;
        if (trail != null) {
            trailId = trail.getId();
        } else {
            trailId = -1;
        }
        String trailName = trailNameTextField.getText();
        String translation = translationTextField.getText();
        String region = regionComboBox.getValue();
        String difficulty = difficultyComboBox.getValue();
        String trailType = trailTypeComboBox.getValue();
        String completionTime = completionTimeTextField.getText();
        String trailDescription = trailDescriptionTextArea.getText();
        return new Trail(trailId, trailName, difficulty, trailDescription, completionTime, trailType);
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
