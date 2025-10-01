package seng202.team5.services;

import seng202.team5.utils.AppDataManager;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.FileBasedKeywordRepo;
import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.data.SqlBasedFilterOptionsRepo;
import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.Trail;
import seng202.team5.utils.TrailsProcessor;

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
    private volatile boolean databaseSetupComplete = false;

    /**
     * Main constructor with database service
     *
     * @param databaseService
     */
    public SetupService(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.sqlTrailRepo = new SqlBasedTrailRepo(databaseService);
        this.fileTrailRepo = new FileBasedTrailRepo("/datasets/DOC_Walking_Experiences_-2195374600472221140.csv");
    }

    /**
     * Checks if the category table is populated.
     *
     * @param sqlBasedKeywordRepo keyword repo to be used
     * @return true if the category table is populated, false otherwise.
     */
    boolean isCategoryTablePopulated(SqlBasedKeywordRepo sqlBasedKeywordRepo) {
        System.out.println(sqlBasedKeywordRepo.countCategories());
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

        createDbActions();
        syncDbFromTrailFile();
        syncKeywords();
        syncFilterOptions();

        databaseSetupComplete = true;
        System.out.println("db setup complete");
    }

    /**
     * Scrapes trail image from its URL and downloads it to the data/images/
     * directory.
     *
     * @param url URL of image to scrape
     */
    void scrapeTrailImage(String url) {
        String filename = extractFilenameFromUrl(url);
        if (filename.contains("no-photo")) {
            return;
        }

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
     * Creates the database if it doesn't exist.
     */
    private void createDbActions() {
        if (databaseService.databaseExists() && databaseService.isSchemaUpToDate()) {
            return;
        }

        try {
            if (databaseService.databaseExists()) {
                System.out.println("Database schema is outdated. Deleting database.");
                databaseService.deleteDatabase();
            }
            databaseService.createDatabaseIfNotExists();
        } catch (Exception e) {
            System.err.println("Error setting up database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Syncs the database from the trail file if trail doesn't exist
     */
    public void syncDbFromTrailFile() {
        try {
            List<Trail> source = fileTrailRepo.getAllTrails();
            List<Trail> trails = TrailsProcessor.processTrails(source);
            sqlTrailRepo.insertOrIgnoreAll(trails);
            System.out.println("Sync trails complete, " + sqlTrailRepo.countTrails() + " trails.");
        } catch (Exception e) {
            System.err.println("Error syncing database from trail file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Syncs filter options in the database.
     */
    public void syncFilterOptions() {
        SqlBasedFilterOptionsRepo filterOptionsRepo = new SqlBasedFilterOptionsRepo(databaseService);
        filterOptionsRepo.refreshAllFilterOptions();
    }

    /**
     * Syncs keywords in the database.
     */
    public void syncKeywords() {
        SqlBasedKeywordRepo sqlBasedKeywordRepo = new SqlBasedKeywordRepo(databaseService);
        FileBasedKeywordRepo fileBasedKeywordRepo = new FileBasedKeywordRepo(
                "/datasets/Categories_and_Keywords.csv");
        sqlBasedKeywordRepo.insertCategoriesAndKeywords(fileBasedKeywordRepo.getKeywords());
        MatchmakingService matchmakingService = new MatchmakingService(databaseService);
        try {
            matchmakingService.categoriseAllTrails();
        } catch (MatchmakingFailedException e) {
            System.err.println("Error generating trail weights: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calls key functions to set up application
     */
    public void setupApplication() {
        setupDatabase();
        scrapeAllTrailImages();
    }

    /**
     * Checks if database setup is complete
     *
     * @return true if database setup is complete, false otherwise
     */
    public boolean isDatabaseSetupComplete() {
        return databaseSetupComplete;
    }

    /**
     * Waits for database setup to complete
     */
    public void waitForDatabaseSetup() {
        while (!databaseSetupComplete) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
