package seng202.team5.services;

import seng202.team5.data.FileBasedTrailRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;
import java.io.File;

/**
 * Service class for setting up the application.
 * Populates database if needed and scrapes images from external sources if
 * needed.
 */
public class SetupService {

    private SqlBasedTrailRepo DbTrailRepo;
    private FileBasedTrailRepo FileTrailRepo;

    /**
     * Checks if the trail table is populated.
     *
     * @return true if the trail table is populated, false otherwise.
     */
    boolean isTrailTablePopulated() {
        return DbTrailRepo.countTrails() >= FileTrailRepo.countTrails();
    }

    boolean areImagesScraped() {
        for (Trail trail : DbTrailRepo.getAllTrails()) {
            if (trail.getThumbnailURL() != null && !trail.getThumbnailURL().isEmpty()) {
                String filename = extractFilenameFromUrl(trail.getThumbnailURL());
                File imageFile = new File("data/images/" + filename);
                if (!imageFile.exists()) {
                    return false;
                }
            }
        }
        return true;
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
}
