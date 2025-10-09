package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import seng202.team5.utils.AppDataManager;
import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SetupServiceTest {

    @Mock
    private DatabaseService mockDatabaseService;
    @Mock
    private SqlBasedTrailRepo mockSqlBasedTrailRepo;

    private SetupService setupService;
    private String testDbPath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testDbPath = tempDir.resolve("test.db").toString();
        setupService = new SetupService(mockSqlBasedTrailRepo, mockDatabaseService);
    }

    @Test
    @DisplayName("Should download image when file doesn't exist")
    void testScrapeTrailImage() throws Exception {
        String testUrl = "https://example.com/test.jpg";
        String tempImagePath = tempDir.resolve("test.jpg").toString();

        try (MockedStatic<AppDataManager> mockedAppDataManager = mockStatic(AppDataManager.class)) {
            mockedAppDataManager.when(() -> AppDataManager.getAppData("images/test.jpg"))
                    .thenReturn(tempImagePath);

            URL mockUrl = mock(URL.class);
            InputStream mockInputStream = new ByteArrayInputStream("fake image data".getBytes());

            try (MockedStatic<URI> mockedURI = mockStatic(URI.class)) {
                URI mockUri = mock(URI.class);
                mockedURI.when(() -> URI.create(testUrl)).thenReturn(mockUri);
                when(mockUri.toURL()).thenReturn(mockUrl);
                when(mockUrl.openStream()).thenReturn(mockInputStream);

                setupService.scrapeTrailImage(testUrl);

                assertTrue(Files.exists(Path.of(tempImagePath)));
            }
        }
    }

    @Test
    @DisplayName("Should not download image when file already exists")
    void testScrapeTrailImage_FileAlreadyExists() throws Exception {
        String testUrl = "https://example.com/existing.jpg";
        String tempImagePath = tempDir.resolve("existing.jpg").toString();

        Files.write(Path.of(tempImagePath), "existing data".getBytes());

        try (MockedStatic<AppDataManager> mockedAppDataManager = mockStatic(AppDataManager.class)) {
            mockedAppDataManager.when(() -> AppDataManager.getAppData("images/existing.jpg"))
                    .thenReturn(tempImagePath);

            setupService.scrapeTrailImage(testUrl);

            assertEquals("existing data", Files.readString(Path.of(tempImagePath)));
            mockedAppDataManager.verify(() -> AppDataManager.getAppData("images/existing.jpg"));
        }
    }

    @Test
    @DisplayName("Should handle IOException when downloading image fails")
    void testScrapeTrailImage_IOExceptionHandling() throws Exception {
        String testUrl = "https://invalid.url/test.jpg";
        String tempImagePath = tempDir.resolve("test.jpg").toString();

        try (MockedStatic<AppDataManager> mockedAppDataManager = mockStatic(AppDataManager.class)) {
            mockedAppDataManager.when(() -> AppDataManager.getAppData("images/test.jpg"))
                    .thenReturn(tempImagePath);

            try (MockedStatic<URI> mockedURI = mockStatic(URI.class)) {
                URI mockUri = mock(URI.class);
                URL mockUrl = mock(URL.class);
                mockedURI.when(() -> URI.create(testUrl)).thenReturn(mockUri);
                when(mockUri.toURL()).thenReturn(mockUrl);
                when(mockUrl.openStream()).thenThrow(new IOException("Connection failed"));

                assertDoesNotThrow(() -> setupService.scrapeTrailImage(testUrl));
                assertFalse(Files.exists(Path.of(tempImagePath)));
            }
        }
    }

    @Test
    @DisplayName("Should extract filename correctly from URL")
    void testExtractFilenameFromUrl_StandardCase() throws Exception {
        String testUrl = "https://example.com/path/to/image.jpg";
        String tempImagePath = tempDir.resolve("image.jpg").toString();

        try (MockedStatic<AppDataManager> mockedAppDataManager = mockStatic(AppDataManager.class)) {
            mockedAppDataManager.when(() -> AppDataManager.getAppData("images/image.jpg"))
                    .thenReturn(tempImagePath);

            URL mockUrl = mock(URL.class);
            InputStream mockInputStream = new ByteArrayInputStream("test data".getBytes());

            try (MockedStatic<URI> mockedURI = mockStatic(URI.class)) {
                URI mockUri = mock(URI.class);
                mockedURI.when(() -> URI.create(testUrl)).thenReturn(mockUri);
                when(mockUri.toURL()).thenReturn(mockUrl);
                when(mockUrl.openStream()).thenReturn(mockInputStream);

                setupService.scrapeTrailImage(testUrl);

                mockedAppDataManager.verify(() -> AppDataManager.getAppData("images/image.jpg"));
            }
        }
    }

    @Test
    @DisplayName("Should handle URL with no path separator")
    void testExtractFilenameFromUrl_NoPathSeparator() throws Exception {
        String testUrl = "filename.png";
        String tempImagePath = tempDir.resolve("filename.png").toString();

        try (MockedStatic<AppDataManager> mockedAppDataManager = mockStatic(AppDataManager.class)) {
            mockedAppDataManager.when(() -> AppDataManager.getAppData("images/filename.png"))
                    .thenReturn(tempImagePath);

            URL mockUrl = mock(URL.class);
            InputStream mockInputStream = new ByteArrayInputStream("test data".getBytes());

            try (MockedStatic<URI> mockedURI = mockStatic(URI.class)) {
                URI mockUri = mock(URI.class);
                mockedURI.when(() -> URI.create(testUrl)).thenReturn(mockUri);
                when(mockUri.toURL()).thenReturn(mockUrl);
                when(mockUrl.openStream()).thenReturn(mockInputStream);

                setupService.scrapeTrailImage(testUrl);

                mockedAppDataManager.verify(() -> AppDataManager.getAppData("images/filename.png"));
            }
        }
    }

    @Test
    @DisplayName("Should scrape images for all trails")
    void testScrapeAllTrailImages() throws Exception {
        // Set up test database with trails
        DatabaseService testDbService = new DatabaseService(testDbPath);
        testDbService.createDatabaseIfNotExists();

        SqlBasedTrailRepo testTrailRepo = new SqlBasedTrailRepo(testDbService);

        Trail trail1 = new Trail(1, "Trail1", "Description1", "Easy", "1hr",
                "https://example.com/image1.jpg", "url1", 0.0, 0.0);
        Trail trail2 = new Trail(2, "Trail2", "Description2", "Medium", "2hr",
                "https://example.com/image2.jpg", "url2", 0.0, 0.0);

        List<Trail> trails = Arrays.asList(trail1, trail2);
        testTrailRepo.upsertAll(trails);

        SetupService testSetupService = new SetupService(testTrailRepo, testDbService);
        SetupService spySetupService = spy(testSetupService);
        doNothing().when(spySetupService).scrapeTrailImage(anyString());

        spySetupService.scrapeAllTrailImages();

        verify(spySetupService).scrapeTrailImage("https://example.com/image1.jpg");
        verify(spySetupService).scrapeTrailImage("https://example.com/image2.jpg");
    }

    @Test
    @DisplayName("Should handle empty trail list when scraping images")
    void testScrapeAllTrailImages_WithEmptyList() throws Exception {
        // Set up test database with no trails
        DatabaseService testDbService = new DatabaseService(testDbPath);
        testDbService.createDatabaseIfNotExists();

        SetupService testSetupService = new SetupService(mockSqlBasedTrailRepo, testDbService);
        SetupService spySetupService = spy(testSetupService);

        spySetupService.scrapeAllTrailImages();

        verify(spySetupService, never()).scrapeTrailImage(anyString());
    }

    @Test
    @DisplayName("Should sync DB from file when table is not populated")
    void testSyncDbFromTrailFile() throws Exception {
        // Set up test database
        DatabaseService testDbService = new DatabaseService(testDbPath);
        testDbService.createDatabaseIfNotExists();

        SetupService testSetupService = new SetupService(mockSqlBasedTrailRepo, testDbService);

        // This test verifies that syncDbFromTrailFile doesn't throw exceptions
        // The actual file loading behavior is tested elsewhere
        assertDoesNotThrow(() -> testSetupService.syncDbFromTrailFile());
    }

    @Test
    @DisplayName("Should always sync DB regardless of current population")
    void testSyncDbFromTrailFile_WhenTableAlreadyPopulated() throws Exception {
        // Set up test database with some existing data
        DatabaseService testDbService = new DatabaseService(testDbPath);
        testDbService.createDatabaseIfNotExists();

        SqlBasedTrailRepo testTrailRepo = new SqlBasedTrailRepo(testDbService);
        Trail existingTrail = new Trail(1, "ExistingTrail", "Description", "Easy", "1hr",
                "url", "url", 0.0, 0.0);
        testTrailRepo.upsertAll(Arrays.asList(existingTrail));

        SetupService testSetupService = new SetupService(mockSqlBasedTrailRepo, testDbService);

        // This test verifies that syncDbFromTrailFile works even when DB already has
        // data
        assertDoesNotThrow(() -> testSetupService.syncDbFromTrailFile());
    }

    @Test
    @DisplayName("Should handle empty file trail list during sync")
    void testSyncDbFromTrailFile_WithEmptyFileTrails() throws Exception {
        // Set up test database
        DatabaseService testDbService = new DatabaseService(testDbPath);
        testDbService.createDatabaseIfNotExists();

        SetupService testSetupService = new SetupService(mockSqlBasedTrailRepo, testDbService);

        // This test verifies that syncDbFromTrailFile handles cases where file has no
        // trails
        assertDoesNotThrow(() -> testSetupService.syncDbFromTrailFile());
    }

    @Test
    @DisplayName("Should call both setup database and scrape methods in setupApplication")
    void testSetupApplication_CallsBothMethods() {
        DatabaseService mockDatabaseService = mock(DatabaseService.class);
        SetupService spySetupService = spy(new SetupService(mockSqlBasedTrailRepo, mockDatabaseService));

        doNothing().when(spySetupService).scrapeAllTrailImages();
        doNothing().when(spySetupService).setupDatabase();

        spySetupService.setupApplication();

        verify(spySetupService, times(1)).setupDatabase();
        verify(spySetupService, times(1)).scrapeAllTrailImages();
    }

    @Test
    @DisplayName("Should handle null thumbnail URL in scrapeAllTrailImages")
    void testScrapeAllTrailImages_WithNullThumbnailURL() throws Exception {
        // Set up test database with a trail that has null thumbnail URL
        DatabaseService testDbService = new DatabaseService(testDbPath);
        testDbService.createDatabaseIfNotExists();

        SqlBasedTrailRepo testTrailRepo = new SqlBasedTrailRepo(testDbService);

        Trail trailWithNullUrl = new Trail(1, "Trail1", "Description1", "Easy", "1hr",
                null, "url1", 0.0, 0.0);

        List<Trail> trails = Arrays.asList(trailWithNullUrl);
        testTrailRepo.upsertAll(trails);

        SetupService testSetupService = new SetupService(testTrailRepo, testDbService);
        SetupService spySetupService = spy(testSetupService);
        doNothing().when(spySetupService).scrapeTrailImage(any());

        assertDoesNotThrow(() -> spySetupService.scrapeAllTrailImages());

        verify(spySetupService).scrapeTrailImage(null);
    }
}
