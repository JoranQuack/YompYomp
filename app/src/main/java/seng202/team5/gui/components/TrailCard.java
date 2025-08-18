package seng202.team5.gui.components;

import java.io.IOException;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import seng202.team5.models.Trail;

public class TrailCard extends VBox{

    // UI Components
    @FXML private ImageView thumbnail;
    @FXML private Label title;
    @FXML private Label subtitle;
    @FXML private ProgressBar matchBar;
    @FXML private Label matchLabel;

    public TrailCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TrailCard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this); //FXML elements
        try {fxmlLoader.load();} catch (IOException e) {throw new RuntimeException(e);} //If not work, crash?
    }

    // Sets Title for the TrailCard Component
    public void setData(Trail trail) {
        title.setText(trail.getName());

        try {
            Image image = new Image(trail.getThumbnailURL(), true);
            thumbnail.setImage(image);
        } catch (RuntimeException e) {
            thumbnail.setImage(null);
        }
    }

    private String basicInfo(Trail trail) {
        String diff = trail.getDifficulty();
        String time = trail.getCompletionTime();
        return diff + "\t" + time;
    }






}
