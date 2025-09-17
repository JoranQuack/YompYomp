package seng202.team5.services;

import seng202.team5.utils.AppDataManager;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.FileBasedKeywordRepo;
import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import seng202.team5.utils.CompletionTimeParser;
import seng202.team5.utils.DifficultyParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Service class for setting up the application.
 * Populates database if needed and scrapes images from external sources if
 * needed.
 */
public class SetupService {
    private final DatabaseService databaseService;
    private final SqlBasedTrailRepo sqlTrailRepo;
    private final FileBasedTrailRepo fileTrailRepo;

    /**
     * Constructor for setup service with custom SQLBasedRepo and FileBasedRepo for
     * testing
     *
     * @param sqlBasedTrailRepo
     * @param fileTrailRepo
     */
    public SetupService(SqlBasedTrailRepo sqlBasedTrailRepo, FileBasedTrailRepo fileTrailRepo) {
        this.sqlTrailRepo = sqlBasedTrailRepo;
        this.fileTrailRepo = fileTrailRepo;
        databaseService = null;
    }

    /**
     * Constructor for setup service
     */
    public SetupService() {
        this.databaseService = new DatabaseService();
        this.sqlTrailRepo = new SqlBasedTrailRepo(databaseService);
        this.fileTrailRepo = new FileBasedTrailRepo("/datasets/DOC_Walking_Experiences_7994760352369043452.csv");
    }

    /**
     * Checks if the category table is populated.
     *
     * @param sqlBasedKeywordRepo
     * @return true if the category table is populated, false otherwise.
     */
    boolean isCategoryTablePopulated(SqlBasedKeywordRepo sqlBasedKeywordRepo) {
        return sqlBasedKeywordRepo.countCategories() > 0;
    }

    /**
     * Sets up the database by creating it if it doesn't exist.
     */
    public void setupDatabase() {
        if (databaseService == null) {
            System.err.println("Database service is null - cannot setup database");
            return;
        }

        if (databaseService.databaseExists() || databaseService.isSchemaUpToDate()) {
            return;
        }

        try {
            if (databaseService.databaseExists()) {
                System.out.println("Database schema is outdated. Deleting database.");
                databaseService.deleteDatabase();
            }

            // Create the database and tables
            databaseService.createDatabaseIfNotExists();
        } catch (Exception e) {
            System.err.println("Error setting up database: " + e.getMessage());
            e.printStackTrace();
        }

        syncDbFromTrailFile();

        // Populate keywords table coz we need dat stuff later
        SqlBasedKeywordRepo sqlBasedKeywordRepo = new SqlBasedKeywordRepo(databaseService);
        if (!isCategoryTablePopulated(sqlBasedKeywordRepo)) {
            FileBasedKeywordRepo fileBasedKeywordRepo = new FileBasedKeywordRepo(
                    "/datasets/Categories_and_Keywords.csv");
            sqlBasedKeywordRepo.insertCategoriesAndKeywords(fileBasedKeywordRepo.getKeywords());
        }
    }

    /**
     * Scrapes trail image from its URL and downloads it to the data/images/
     * directory.
     *
     * @param url
     */
    void scrapeTrailImage(String url) {
        String filename = extractFilenameFromUrl(url);
        String imagePath = AppDataManager.getAppData("images/" + filename);
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            try {
                // Create the images directory if it doesn't exist
                imageFile.getParentFile().mkdirs();
                // Download the image from the URL
                URL imageUrl = URI.create(url).toURL();
                try (InputStream in = imageUrl.openStream()) {
                    Files.copy(in, imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                System.err.println("Failed to download image from " + url + ": " + e.getMessage());
            }
        }
    }

    /**
     * Scrapes images for all trails.
     */
    public void scrapeAllTrailImages() {
        for (Trail trail : sqlTrailRepo.getAllTrails()) {
            scrapeTrailImage(trail.getThumbnailURL());
        }
    }

    /**
     * Extracts the filename from a URL.
     *
     * @param url the URL to extract filename from
     * @return the filename
     */
    private String extractFilenameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    /**
     * Upserts into DB to keep up to date
     */
    public void syncDbFromTrailFile() {
        try {
            List<Trail> source = fileTrailRepo.getAllTrails();
            List<Trail> trails = processTrails(source);
            sqlTrailRepo.upsertAll(trails);
        } catch (Exception e) {
            System.err.println("Error syncing database from trail file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processes trails to populate time-related fields
     *
     * @param trails List of trails to process
     * @return List of processed trails
     */
    private List<Trail> processTrails(List<Trail> trails) {
        for (Trail trail : trails) {
            String completionInfo = trail.getCompletionInfo();

            if (completionInfo != null && !completionInfo.trim().isEmpty()) {
                try {
                    CompletionTimeParser.CompletionTimeResult result = CompletionTimeParser
                            .parseCompletionTime(completionInfo);

                    // Update trail with parsed time information
                    trail.setMinCompletionTimeMinutes(result.getMinCompletionTimeMinutes());
                    trail.setMaxCompletionTimeMinutes(result.getMaxCompletionTimeMinutes());
                    trail.setCompletionType(result.getCompletionType());
                    trail.setTimeUnit(result.getTimeUnit());
                    trail.setMultiDay(result.isMultiDay());
                    trail.setHasVariableTime(result.hasVariableTime());

                } catch (Exception e) {
                    System.err.println("Error parsing completion time for trail " + trail.getId() +
                            " ('" + completionInfo + "'): " + e.getMessage());
                    // Keep default values if parsing fails
                }
            }

            trail.setDifficulty(DifficultyParser.parseDifficulty(trail.getDifficulty()));
        }
        return trails;
    }

    /**
     * Calls key functions to set up application
     */
    public void setupApplication() {
        setupDatabase();
        scrapeAllTrailImages();
    }
}
