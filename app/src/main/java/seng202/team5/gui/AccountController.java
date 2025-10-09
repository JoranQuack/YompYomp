package seng202.team5.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import seng202.team5.App;
import seng202.team5.models.Question;
import seng202.team5.models.User;
import seng202.team5.services.AccountStatisticsService;
import seng202.team5.services.MatchmakingService;

import java.util.List;
import java.util.Map;

public class AccountController extends Controller {

    @FXML
    private ImageView profileImage;
    @FXML
    private ImageView clearProfileImage;
    @FXML
    private ImageView optionImage1;
    @FXML
    private ImageView optionImage2;
    @FXML
    private ImageView optionImage3;
    @FXML
    private ImageView optionImage4;
    @FXML
    private ImageView optionImage5;
    @FXML
    private ImageView optionImage6;
    @FXML
    private ImageView optionImage7;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label familyFriendlyLabel;
    @FXML
    private Label accessibleLabel;
    @FXML
    private Label experienceLabel;
    @FXML
    private Label gradientLabel;
    @FXML
    private Label bushLabel;
    @FXML
    private Label reserveLabel;
    @FXML
    private Label lakeRiverLabel;
    @FXML
    private Label coastLabel;
    @FXML
    private Label mountainLabel;
    @FXML
    private Label wildlifeLabel;
    @FXML
    private Label historicLabel;
    @FXML
    private Label waterfallLabel;
    @FXML
    private Button redoQuizButton;
    @FXML
    private Button deleteProfileButton;
    @FXML
    private BarChart<String, Number> preferencesBarChart;
    @FXML
    private PieChart difficultyPieChart;
    @FXML
    private BarChart<String, Number> regionBarChart;
    @FXML
    private Label avgMatchScoreLabel;
    @FXML
    private Label topCategoryLabel;
    @FXML
    private Label totalTrailsLabel;

    User user;
    private AccountStatisticsService statisticsService;

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
        this.user = getUserService().getUser();

        MatchmakingService matchmakingService = new MatchmakingService(App.getKeywordRepo(), App.getTrailRepo());
        this.statisticsService = new AccountStatisticsService(App.getTrailRepo(), App.getTrailLogRepo(),
                matchmakingService, user);
    }

    @FXML
    private void initialize() {
        welcomeLabel.setText("Kia Ora " + user.getName() + "!");
        if (user.getProfilePicture() == null) {
            profileImage.setImage(new Image("/images/profiles/user.png"));
        } else {
            profileImage.setImage(new Image(user.getProfilePicture()));
        }

        Platform.runLater(this::setUpChartsAndStats);

        List<ImageView> optionImages = List.of(optionImage1, optionImage2, optionImage3, optionImage4,
                optionImage5, optionImage6, optionImage7);
        for (ImageView optionImage : optionImages) {
            optionImage.setOnMouseClicked(e -> onOptionImageClicked(optionImage));
        }
        clearProfileImage.setOnMouseClicked(e -> onClearProfileImageClicked());
        redoQuizButton.setOnAction(e -> onRedoQuizButtonClicked());
        deleteProfileButton.setOnAction(e -> onDeleteProfileButtonClicked());
    }

    @FXML
    private void onRedoQuizButtonClicked() {
        super.getNavigator().launchScreen(new ProfileSetupGeneralController(super.getNavigator()));
    }

    @FXML
    private void onDeleteProfileButtonClicked() {
        super.getUserService().clearUser();
        super.getUserService().setGuest(true);
        super.getNavigator().launchScreen(new DashboardController(super.getNavigator()));
    }

    @FXML
    private void onClearProfileImageClicked() {
        profileImage.setImage(new Image("/images/profiles/user.png"));
        user.setProfilePicture("/images/profiles/user.png");
        super.getUserService().saveUser(user);
    }

    @FXML
    private void onOptionImageClicked(ImageView optionImage) {
        profileImage.setImage(optionImage.getImage());
        user.setProfilePicture(optionImage.getImage().getUrl());
        super.getNavbarController().getProfileImage().setImage(new Image(user.getProfilePicture()));
        super.getUserService().saveUser(user);
    }

    /**
     * Sets up charts and statistics labels
     */
    private void setUpChartsAndStats() {
        setPreferenceLabels();
        setPreferencesBarChart();
        setDifficultyPieChart();
        setRegionBarChart();
        setStatisticsLabels();
    }

    /**
     * Sets user preference labels based on their saved preferences
     */
    private void setPreferenceLabels() {
        familyFriendlyLabel.setText(user.isFamilyFriendly() ? "Yes" : "No");
        accessibleLabel.setText(user.isAccessible() ? "Yes" : "No");
        experienceLabel.setText(getPreferenceLabel(user.getExperienceLevel(), Question.ONE.sliderLabels));
        gradientLabel.setText(getPreferenceLabel(user.getGradientPreference(), Question.TWO.sliderLabels));
        bushLabel.setText(getPreferenceLabel(user.getBushPreference(), Question.THREE.sliderLabels));
        reserveLabel.setText(getPreferenceLabel(user.getReservePreference(), Question.FOUR.sliderLabels));
        lakeRiverLabel.setText(getPreferenceLabel(user.getLakeRiverPreference(), Question.FIVE.sliderLabels));
        coastLabel.setText(getPreferenceLabel(user.getCoastPreference(), Question.SIX.sliderLabels));
        mountainLabel.setText(getPreferenceLabel(user.getMountainPreference(), Question.SEVEN.sliderLabels));
        wildlifeLabel.setText(getPreferenceLabel(user.getWildlifePreference(), Question.EIGHT.sliderLabels));
        historicLabel.setText(getPreferenceLabel(user.getHistoricPreference(), Question.NINE.sliderLabels));
        waterfallLabel.setText(getPreferenceLabel(user.getWaterfallPreference(), Question.TEN.sliderLabels));
    }

    /**
     * Sets up the preferences bar chart with user data
     */
    private void setPreferencesBarChart() {
        if (preferencesBarChart == null || statisticsService == null)
            return;

        try {
            Map<String, Integer> preferences = statisticsService.getUserPreferencesData();

            if (preferences == null)
                return;

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Your Preferences");

            for (Map.Entry<String, Integer> entry : preferences.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            preferencesBarChart.getData().clear();
            preferencesBarChart.getData().add(series);
            preferencesBarChart.setTitle("Your Preference Strength (1-5 scale)");
            preferencesBarChart.setLegendVisible(false);

            // Configure Y-axis for integer ticks and max value of 5
            if (preferencesBarChart.getYAxis() instanceof NumberAxis) {
                NumberAxis yAxis = (NumberAxis) preferencesBarChart.getYAxis();
                yAxis.setLowerBound(0);
                yAxis.setUpperBound(5);
                yAxis.setTickUnit(1);
                yAxis.setMinorTickVisible(false);
                yAxis.setAutoRanging(false);
            }

            // Apply CSS classes to bars for styling
            applyBarChartColors(preferencesBarChart);

        } catch (Exception e) {
            // Clear chart if there's an error
            preferencesBarChart.getData().clear();
        }
    }

    /**
     * Sets up the difficulty pie chart with user data
     */
    private void setDifficultyPieChart() {
        if (difficultyPieChart == null || statisticsService == null)
            return;

        try {
            Map<String, Object> difficultyStats = statisticsService.getDifficultyStatistics();

            if (difficultyStats == null)
                return;

            @SuppressWarnings("unchecked")
            Map<String, Integer> perceivedDifficulties = (Map<String, Integer>) difficultyStats.get("perceived");

            if (perceivedDifficulties != null && !perceivedDifficulties.isEmpty()) {
                ObservableList<PieChart.Data> difficultyData = FXCollections.observableArrayList();
                for (Map.Entry<String, Integer> entry : perceivedDifficulties.entrySet()) {
                    difficultyData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }

                difficultyPieChart.setData(difficultyData);
                difficultyPieChart.setTitle("Your Perceived Difficulty Levels");
            } else {
                PieChart.Data noDataSlice = new PieChart.Data("No difficulty data available", 1);
                difficultyPieChart.setData(FXCollections.observableArrayList(noDataSlice));
            }

            // Apply CSS classes to pie slices for styling
            applyPieChartColors(difficultyPieChart);

        } catch (Exception e) {
            PieChart.Data errorSlice = new PieChart.Data("Error loading data", 1);
            difficultyPieChart.setData(FXCollections.observableArrayList(errorSlice));
        }
    }

    /**
     * Sets statistics labels with user data
     */
    private void setStatisticsLabels() {
        if (statisticsService == null) {
            setDefaultStatistics();
            return;
        }

        try {
            // Total trails in logbook
            Integer totalTrails = statisticsService.getTotalLoggedTrails();
            totalTrailsLabel.setText(totalTrails != null ? totalTrails.toString() : "0");

            // Average match score from logged trails
            Double avgScore = statisticsService.getAverageMatchScore();
            avgMatchScoreLabel.setText(avgScore != null ? String.format("%.1f%%", avgScore) : "0.0%");

            // Top category from logged trails
            String topCategory = statisticsService.getTopCategory();
            topCategoryLabel.setText(topCategory != null ? topCategory : "None");

        } catch (Exception e) {
            setDefaultStatistics();
        }
    }

    /**
     * Sets default statistics values when no data is available
     */
    private void setDefaultStatistics() {
        totalTrailsLabel.setText("0");
        avgMatchScoreLabel.setText("0.0%");
        topCategoryLabel.setText("None");
    }

    /**
     * Sets up the region bar chart with user data
     */
    private void setRegionBarChart() {
        if (regionBarChart == null || statisticsService == null)
            return;

        try {
            Map<String, Integer> regionStats = statisticsService.getRegionalStatistics();

            if (regionStats != null && !regionStats.isEmpty()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Logged by Region");

                for (Map.Entry<String, Integer> entry : regionStats.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }

                regionBarChart.getData().clear();
                regionBarChart.getData().add(series);
                regionBarChart.setTitle("Top Regions in Your Logged Trails");
                regionBarChart.setLegendVisible(false);

                // Configure Y-axis for integer ticks only
                if (regionBarChart.getYAxis() instanceof NumberAxis) {
                    NumberAxis yAxis = (NumberAxis) regionBarChart.getYAxis();
                    yAxis.setLowerBound(0);
                    yAxis.setTickUnit(1);
                    yAxis.setMinorTickVisible(false);
                }

                // Apply CSS classes to bars for styling
                applyBarChartColors(regionBarChart);
            } else {
                regionBarChart.getData().clear();
            }

        } catch (Exception e) {
            // Clear chart if there's an error
            regionBarChart.getData().clear();
        }
    }

    /**
     * Applies CSS classes to bar chart bars for proper styling
     *
     * @param barChart the bar chart to apply CSS classes to
     */
    private void applyBarChartColors(BarChart<String, Number> barChart) {
        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                int index = 0;
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        data.getNode().getStyleClass().add("default-color" + index);
                    }
                    index++;
                }
            }
        });
    }

    /**
     * Applies CSS classes to pie chart slices for proper styling
     *
     * @param pieChart the pie chart to apply CSS classes to
     */
    private void applyPieChartColors(PieChart pieChart) {
        Platform.runLater(() -> {
            for (PieChart.Data data : pieChart.getData()) {
                if (data.getNode() != null) {
                    data.getNode().getStyleClass().add("difficulty-" + data.getName().toLowerCase());
                }
            }
        });
    }

    /**
     * Returns string label to be set for each user preference
     *
     * @param preference
     * @param sliderLabels
     * @return string to be set for user preference label
     */
    private String getPreferenceLabel(int preference, String[] sliderLabels) {
        if (preference == 1) {
            return sliderLabels[0];
        } else if (preference == 5) {
            return sliderLabels[2];
        } else {
            return sliderLabels[1];
        }
    }

    @Override
    protected String getFxmlFile() {
        return "/fxml/account_screen.fxml";
    }

    @Override
    protected String getTitle() {
        return "Account Screen";
    }

    @Override
    protected boolean shouldShowNavbar() {
        return true;
    }

    @Override
    protected int getNavbarPageIndex() {
        return -1; // No navbar button active
    }

    @Override
    public void onLoadFailed(Exception e) {
        showAlert("Account Failed To Load",
                "Account page failed to load, please close and reload the application.",
                "", "OK", null, null);
    }
}
