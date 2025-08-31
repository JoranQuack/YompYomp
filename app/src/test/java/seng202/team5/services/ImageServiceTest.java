package seng202.team5.services;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ImageService.
 */
public class ImageServiceTest {

    private ImageService imageService;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Initialise JavaFX platform for testing
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();
    }

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

    @Test
    @DisplayName("Should handle valid URL format without exceptions")
    void testLoadTrailImageWithValidUrl() {
        String testUrl = "https://www.doc.govt.nz/thumbs/large/link/981e1fce972c45e0973eb1832ba8ff49.jpg";
        Image image = imageService.loadTrailImage(testUrl);
        assertNotNull(image, "Should return an image");
        assertFalse(image.isError(), "Image should not have loading errors");
        boolean exists = imageService.imageExists(testUrl);
        assertTrue(exists);
        assertDoesNotThrow(() -> imageService.loadTrailImage(testUrl),
                "Should handle valid URL format without exceptions");
    }
}
