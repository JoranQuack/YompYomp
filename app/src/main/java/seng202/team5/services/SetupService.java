package seng202.team5.services;

import seng202.team5.data.AppDataManager;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.FileBasedKeywordRepo;
import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

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
    private final SqlBasedTrailRepo DbTrailRepo;
    private final FileBasedTrailRepo FileTrailRepo;

    /**
     * Constructor for setup service with custom SQLBasedRepo and FileBasedRepo for
     * testing
     *
     * @param sqlBasedTrailRepo
     * @param fileTrailRepo
     */
    public SetupService(SqlBasedTrailRepo sqlBasedTrailRepo, FileBasedTrailRepo fileTrailRepo) {
        this.DbTrailRepo = sqlBasedTrailRepo;
        this.FileTrailRepo = fileTrailRepo;
        databaseService = null;
    }

    /**
     * Constructor for setup service
     */
    public SetupService() {
        this.databaseService = new DatabaseService();
        this.DbTrailRepo = new SqlBasedTrailRepo(databaseService);
        this.FileTrailRepo = new FileBasedTrailRepo("/datasets/DOC_Walking_Experiences_7994760352369043452.csv");
    }

    /**
     * Checks if the trail table is populated.
     *
     * @return true if the trail table is populated, false otherwise.
     */
    boolean isTrailTablePopulated() {
        return DbTrailRepo.countTrails() >= FileTrailRepo.countTrails();
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

        try {
            // Check if database exists and schema is up to date
            if (!databaseService.databaseExists() || !databaseService.isSchemaUpToDate()) {
                if (databaseService.databaseExists()) {
                    databaseService.deleteDatabase();
                }

                // Create the database and tables
                databaseService.createDatabaseIfNotExists();
            }
        } catch (Exception e) {
            System.err.println("Error setting up database: " + e.getMessage());
            e.printStackTrace();
        }

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
        for (Trail trail : DbTrailRepo.getAllTrails()) {
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
     * Upserts into DB if not up to date
     */
    public void syncDbFromTrailFile() {
        try {
            if (!isTrailTablePopulated()) {
                System.out.println("Trail table being populated");
                List<Trail> source = FileTrailRepo.getAllTrails();
                DbTrailRepo.upsertAll(source);
                if (isTrailTablePopulated()) {
                    System.out.println("Trail table populated");
                }
            }
        } catch (Exception e) {
            System.err.println("Error syncing database from trail file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calls key functions to set up application
     */
    public void setupApplication() {
        setupDatabase();
        syncDbFromTrailFile();
        scrapeAllTrailImages();
    }
}
