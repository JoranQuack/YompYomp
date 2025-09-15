package seng202.team5.services;

import javafx.scene.image.Image;
import seng202.team5.utils.AppDataManager;

import java.io.File;

/**
 * Service class for handling image loading operations.
 * Provides centralized image loading with fallback mechanisms.
 */
public class ImageService {

    private static final String DEFAULT_TRAIL_IMAGE_PATH = "/images/Default Trail Image.png";
    private Image cachedDefaultImage;

    /**
     * Loads a trail image from the given thumbnail URL.
     * Falls back to the default trail image if the specific image cannot be loaded.
     *
     * @param thumbnailUrl The URL of the trail thumbnail image
     * @return The loaded Image object, or the default image if loading fails
     */
    public Image loadTrailImage(String thumbnailUrl) {
        if (thumbnailUrl == null || thumbnailUrl.trim().isEmpty()) {
            return getDefaultTrailImage();
        }

        try {
            String imagePath = getImagePath(thumbnailUrl);

            // Convert file path to file URI for JavaFX Image
            File imageFile = new File(imagePath);
            Image image;
            if (imageFile.exists()) {
                String fileUri = imageFile.toURI().toString();
                image = new Image(fileUri, true);
            } else {
                image = new Image(thumbnailUrl, true);
            }
            if (!image.isError()) {
                return image;
            } else {
                System.err.println("Image loading error for URL: " + thumbnailUrl);
            }
        } catch (Exception e) {
            System.err.println("Error loading trail image: " + e.getMessage());
        }

        // Fallback to default image
        return getDefaultTrailImage();
    }

    /**
     * Gets the default trail image.
     * Caches the image for better performance.
     *
     * @return The default trail image
     */
    public Image getDefaultTrailImage() {
        if (cachedDefaultImage == null || cachedDefaultImage.isError()) {
            try {
                cachedDefaultImage = new Image(getClass().getResourceAsStream(DEFAULT_TRAIL_IMAGE_PATH));
                if (cachedDefaultImage.isError()) {
                    System.err.println("Error loading default trail image from: " + DEFAULT_TRAIL_IMAGE_PATH);
                }
            } catch (Exception e) {
                System.err.println("Exception loading default trail image: " + e.getMessage());
            }
        }
        return cachedDefaultImage;
    }

    /**
     * Extracts the filename from a URL and constructs the local image path.
     *
     * @param thumbnailUrl The thumbnail URL
     * @return The local file path for the image
     */
    private String getImagePath(String thumbnailUrl) {
        String filename = extractFilenameFromUrl(thumbnailUrl);
        return AppDataManager.getAppData("images/" + filename);
    }

    /**
     * Extracts the filename from a URL.
     *
     * @param url The URL to extract filename from
     * @return The filename
     */
    private String extractFilenameFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }
        return url.substring(url.lastIndexOf('/') + 1);
    }

    /**
     * Checks if an image file exists locally for the given thumbnail URL.
     *
     * @param thumbnailUrl The thumbnail URL
     * @return true if the image file exists locally, false otherwise
     */
    public boolean imageExists(String thumbnailUrl) {
        if (thumbnailUrl == null || thumbnailUrl.trim().isEmpty()) {
            return false;
        }

        try {
            String imagePath = getImagePath(thumbnailUrl);
            File imageFile = new File(imagePath);
            return imageFile.exists();
        } catch (Exception e) {
            return false;
        }
    }
}
