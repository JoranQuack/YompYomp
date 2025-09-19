package seng202.team5.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.utils.StringManipulator;
import seng202.team5.utils.TrailsProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the modify trail screen
 */
public class ModifyTrailController extends Controller {

    private Trail trail;
    private Controller lastController;
    private DatabaseService databaseService;
    private SqlBasedTrailRepo sqlBasedTrailRepo;

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
        this.databaseService = new DatabaseService();
        this.sqlBasedTrailRepo = new SqlBasedTrailRepo(databaseService);
    }

    @FXML
    private TextField trailNameTextField;
    @FXML
    private TextField translationTextField;
    @FXML
    private ComboBox<String> difficultyComboBox;
    @FXML
    private ComboBox<String> trailTypeComboBox;
    @FXML
    private TextField completionTimeTextField;
    @FXML
    private TextArea trailDescriptionTextArea;
    @FXML
    private TextField cultureUrlTextField;
    @FXML
    private Label regionLabel;
    @FXML
    private ComboBox<String> regionComboBox;
    @FXML
    private ImageView mapImage;
    @FXML
    private Label emptyFieldLabel;
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
            regionLabel.setVisible(false);
            regionComboBox.setVisible(false);
        } else {
            regionLabel.setVisible(true);
            regionComboBox.setVisible(true);
        }
        List<String> regionList = new ArrayList<>(List.of("Northland", "Auckland",
                "Waikato", "Bay of Plenty", "Gisborne", "Hawke's Bay", "Taranaki",
                "Manawatu-Whanganui", "Tasman", "Wellington", "Nelson", "Marlborough", "West Coast",
                "Canterbury", "Otago", "Southland"));
        regionComboBox.getItems().addAll(regionList);
        difficultyComboBox.getItems().addAll(List.of("Easy", "Intermediate", "Advanced"));
        trailTypeComboBox.getItems().addAll(List.of("One way", "Loop", "Return"));
        emptyFieldLabel.setText("");
        saveButton.setOnAction(e -> onSaveButtonClicked());
        backButton.setOnAction(e -> onBackButtonClicked());
    }

    @FXML
    private void onSaveButtonClicked() {
        if (userInputValidation()) {
            sqlBasedTrailRepo.upsert(getUpdatedTrail());
            super.getNavigator().launchScreen(lastController, lastController.getNavigator().getLastController());
        } else {
            emptyFieldLabel.setText("Please make sure all required fields are filled!");
            emptyFieldLabel.setTextFill(Color.RED);
        }
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
        Trail foundTrail = sqlBasedTrailRepo.findById(trail.getId()).get();
        trailNameTextField.setText(foundTrail.getName());
        difficultyComboBox.setValue(StringManipulator.capitaliseFirstLetter(foundTrail.getDifficulty()));
        trailTypeComboBox.setValue(StringManipulator.capitaliseFirstLetter(foundTrail.getCompletionType()));
        trailDescriptionTextArea.setText(foundTrail.getDescription());
        completionTimeTextField.setText(foundTrail.getCompletionInfo());
        cultureUrlTextField.setText(foundTrail.getCultureUrl());
        translationTextField.setText(foundTrail.getTranslation());
    }

    /**
     * Validates user input
     * @return whether inputs are valid
     */
    private boolean userInputValidation() {
        if (trailNameTextField.getText().isEmpty() || difficultyComboBox.getValue() == null ||
            trailTypeComboBox.getValue() == null || completionTimeTextField.getText().isEmpty() ||
            trailDescriptionTextArea.getText().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Returns Trail object of trail to be updated/added to database
     * @return updatedTrail
     */
    private Trail getUpdatedTrail() {
        int trailId;
        String region;
        String thumbUrl;
        String webUrl;
        double userWeight;
        if (trail != null) {
            trailId = trail.getId();
            region = "";
            thumbUrl = trail.getThumbnailURL();
            webUrl = trail.getWebpageURL();
            userWeight = trail.getUserWeight();
        } else {
            trailId = -1;
            region = regionComboBox.getValue();
            thumbUrl = "";
            webUrl = "";
            userWeight = 0.5;
            // TODO: implement calculation for new trail
        }
        String trailName = trailNameTextField.getText();
        String translation = translationTextField.getText();
        String difficulty = difficultyComboBox.getValue();
        String trailType = trailTypeComboBox.getValue();
        String completionTime = completionTimeTextField.getText();
        String trailDescription = trailDescriptionTextArea.getText();
        String cultureUrl = cultureUrlTextField.getText();
        List<Trail> updatedTrail = TrailsProcessor.processTrails(List.of(new Trail(trailId, trailName, translation,
                region, difficulty, trailType, completionTime, trailDescription, thumbUrl, webUrl, cultureUrl, userWeight)));
        return updatedTrail.getFirst();
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
