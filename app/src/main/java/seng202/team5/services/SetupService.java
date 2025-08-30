package seng202.team5.services;

import seng202.team5.data.AppDataManager;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.FileBasedTrailRepo;
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

    private final SqlBasedTrailRepo DbTrailRepo;
    private final FileBasedTrailRepo FileTrailRepo;

    public SetupService(SqlBasedTrailRepo sqlBasedTrailRepo, FileBasedTrailRepo fileTrailRepo) {
        this.DbTrailRepo = sqlBasedTrailRepo;
        this.FileTrailRepo = fileTrailRepo;
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
        if (!isTrailTablePopulated()) {
            System.out.println("Trail table being populated");
            List<Trail> source = FileTrailRepo.getAllTrails();
            DbTrailRepo.upsertAll(source);
            if (isTrailTablePopulated()) {
                System.out.println("Trail table populated");
            }
        }
    }

    /**
     * Calls key functions to set up application
     */
    public void setupApplication() {
        syncDbFromTrailFile();
        scrapeAllTrailImages();
    }
}
