package seng202.team5.services;

import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ImageService.
 */
public class ImageServiceTest {

    private ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageService();
    }

    @Test
    @DisplayName("Should return the default trail image")
    void testGetDefaultTrailImage() {
        Image defaultImage = imageService.getDefaultTrailImage();
        assertNotNull(defaultImage);
        assertFalse(defaultImage.isError());
    }

    @Test
    @DisplayName("Should return the default trail image when loading with null URL")
    void testLoadTrailImageWithNullUrl() {
        Image image = imageService.loadTrailImage(null);
        assertNotNull(image);
        Image defaultImage = imageService.getDefaultTrailImage();
        assertEquals(defaultImage, image);
    }

    @Test
    @DisplayName("Should return the default trail image when loading with empty URL")
    void testLoadTrailImageWithEmptyUrl() {
        Image image = imageService.loadTrailImage("");
        assertNotNull(image);
        Image defaultImage = imageService.getDefaultTrailImage();
        assertEquals(defaultImage, image);
    }

    @Test
    @DisplayName("Should return false for image existence check with null or empty URL")
    void testImageExistsWithNullUrl() {
        assertFalse(imageService.imageExists(null));
    }

    @Test
    @DisplayName("Should return false for image existence check with empty URL")
    void testImageExistsWithEmptyUrl() {
        assertFalse(imageService.imageExists(""));
    }
}
