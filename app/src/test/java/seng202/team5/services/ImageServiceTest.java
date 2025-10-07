package seng202.team5.services;

import com.google.common.io.FileBackedOutputStream;
import javafx.application.Platform;
import javafx.scene.image.Image;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("Should handle exception in loadTrailImage gracefully")
    void testLoadTrailImageExceptionThrown() {
        Image image = imageService.loadTrailImage("::bad::url");
        assertNotNull(image);
        assertEquals(imageService.getDefaultTrailImage(), image);
    }

    @Test
    @DisplayName("Should return default trail image for 'no-photo' URL")
    void testLoadTrailImageWithNoPhotoUrl() {
        Image image = imageService.loadTrailImage("http://example.com/no-photo.png");
        assertEquals(imageService.getDefaultTrailImage(), image);
    }

    @Test
    @DisplayName("Should cache default image and not reload it")
    void testGetDefaultTrailImageCaching() {
        Image first = imageService.getDefaultTrailImage();
        Image second = imageService.getDefaultTrailImage();
        assertSame(first, second, "Default image should be cached");
    }

    @Test
    @DisplayName("Should return false when exception thrown inside imageExists")
    void testImageExistsThrowsException() {
        ImageService spyService = Mockito.spy(new ImageService());

        // Make getImagePath throw an exception
        Mockito.doThrow(new RuntimeException("boom"))
                .when(spyService)
                .getImagePath(anyString());

        assertFalse(spyService.imageExists("http://fake.url"));
    }

    @Test
    @DisplayName("Should handle broken cached default image gracefully")
    void testGetDefaultTrailImageWithErrorImage() {
        ImageService spyService = Mockito.spy(new ImageService());
        Image badImage = Mockito.mock(Image.class);

        // Simulate cached image exists but isError() = true
        when(badImage.isError()).thenReturn(true);

        // Replace the internal cached image with the bad one
        try {
            var field = ImageService.class.getDeclaredField("cachedDefaultImage");
            field.setAccessible(true);
            field.set(spyService, badImage);
        } catch (Exception e) {
            fail("Reflection injection failed: " + e.getMessage());
        }

        Image result = spyService.getDefaultTrailImage();
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should return empty string when extracting filename from null or empty URL")
    void testExtractFilenameFromUrl() throws Exception {

        var method = ImageService.class.getDeclaredMethod("extractFilenameFromUrl", String.class);
        method.setAccessible(true);

        String result1 = (String) method.invoke(imageService, (String) null);
        assertEquals("", result1);

        String result2 = (String) method.invoke(imageService, "");
        assertEquals("", result2);
    }
}
