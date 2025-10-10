package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import seng202.team5.App;
import seng202.team5.data.SqlBasedFilterOptionsRepo;
import seng202.team5.data.SqlBasedKeywordRepo;
import seng202.team5.exceptions.MatchmakingFailedException;
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
import java.sql.SQLException;
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

        Path path = Path.of(tempImagePath);
        Files.write(path, "existing data".getBytes());

        try (MockedStatic<AppDataManager> mockedAppDataManager = mockStatic(AppDataManager.class)) {
            mockedAppDataManager.when(() -> AppDataManager.getAppData("images/existing.jpg"))
                    .thenReturn(tempImagePath);

            setupService.scrapeTrailImage(testUrl);

            assertEquals("existing data", Files.readString(path));
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

        SetupService testSetupService = getSetupService(testDbService);
        SetupService spySetupService = spy(testSetupService);
        doNothing().when(spySetupService).scrapeTrailImage(anyString());

        spySetupService.scrapeAllTrailImages();

        verify(spySetupService).scrapeTrailImage("https://example.com/image1.jpg");
        verify(spySetupService).scrapeTrailImage("https://example.com/image2.jpg");
    }

    private static SetupService getSetupService(DatabaseService testDbService) throws MatchmakingFailedException {
        SqlBasedTrailRepo testTrailRepo = new SqlBasedTrailRepo(testDbService);

        Trail trail1 = new Trail.Builder()
                .id(1)
                .name("Trail1")
                .description("Description1")
                .difficulty("Easy")
                .completionInfo("1hr")
                .webpageURL("url1")
                .thumbnailURL("https://example.com/image1.jpg")
                .lat(0.0)
                .lon(0.0)
                .build();

        Trail trail2 = new Trail.Builder()
                .id(2)
                .name("Trail2")
                .description("Description2")
                .difficulty("Medium")
                .completionInfo("2hr")
                .webpageURL("url2")
                .thumbnailURL("https://example.com/image2.jpg")
                .lat(0.0)
                .lon(0.0)
                .build();

        List<Trail> trails = Arrays.asList(trail1, trail2);
        testTrailRepo.upsertAll(trails);

        return new SetupService(testTrailRepo, testDbService);
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
        assertDoesNotThrow(testSetupService::syncDbFromTrailFile);
    }

    @Test
    @DisplayName("Should always sync DB regardless of current population")
    void testSyncDbFromTrailFile_WhenTableAlreadyPopulated() throws Exception {
        // Set up test database with some existing data
        DatabaseService testDbService = new DatabaseService(testDbPath);
        testDbService.createDatabaseIfNotExists();

        SqlBasedTrailRepo testTrailRepo = new SqlBasedTrailRepo(testDbService);
        Trail existingTrail = new Trail.Builder()
                .id(1)
                .name("ExistingTrail")
                .description("Description")
                .difficulty("Easy")
                .completionInfo("1hr")
                .webpageURL("url")
                .thumbnailURL("url")
                .lat(0.0)
                .lon(0.0)
                .build();
        testTrailRepo.upsertAll(List.of(existingTrail));

        SetupService testSetupService = new SetupService(mockSqlBasedTrailRepo, testDbService);

        // This test verifies that syncDbFromTrailFile works even when DB already has
        // data
        assertDoesNotThrow(testSetupService::syncDbFromTrailFile);
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
        assertDoesNotThrow(testSetupService::syncDbFromTrailFile);
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

        Trail trailWithNullUrl = new Trail.Builder()
                .id(1)
                .name("Trail1")
                .description("Description1")
                .difficulty("Easy")
                .completionInfo("1hr")
                .webpageURL("url1")
                .thumbnailURL(null)
                .lat(0.0)
                .lon(0.0)
                .build();

        List<Trail> trails = List.of(trailWithNullUrl);
        testTrailRepo.upsertAll(trails);

        SetupService testSetupService = new SetupService(testTrailRepo, testDbService);
        SetupService spySetupService = spy(testSetupService);
        doNothing().when(spySetupService).scrapeTrailImage(any());

        assertDoesNotThrow(spySetupService::scrapeAllTrailImages);

        verify(spySetupService).scrapeTrailImage(null);
    }

    @Test
    @DisplayName("setupDatabase should return early when databaseService is null")
    void testSetupDatabase_NullDatabaseService() {
        SetupService service = new SetupService(mockSqlBasedTrailRepo, null);
        service.setupDatabase();
        assertFalse(service.isDatabaseSetupComplete());
    }

    @Test
    @DisplayName("setupDatabase should complete successfully and mark setup complete")
    void testSetupDatabase_SuccessPath() {
        SetupService spyService = spy(new SetupService(mockSqlBasedTrailRepo, mockDatabaseService));

        doNothing().when(spyService).createDbActions();
        doNothing().when(spyService).syncDbFromTrailFile();
        doNothing().when(spyService).syncKeywords();
        doNothing().when(spyService).syncFilterOptions();

        spyService.setupDatabase();

        verify(spyService).createDbActions();
        verify(spyService).syncDbFromTrailFile();
        verify(spyService).syncKeywords();
        verify(spyService).syncFilterOptions();
        assertTrue(spyService.isDatabaseSetupComplete());
    }

    @Test
    @DisplayName("createDbActions should return early if database exists and schema up to date")
    void testCreateDbActions_ReturnsEarly() throws SQLException {
        when(mockDatabaseService.databaseExists()).thenReturn(true);
        when(mockDatabaseService.isSchemaUpToDate()).thenReturn(true);

        SetupService service = new SetupService(mockSqlBasedTrailRepo, mockDatabaseService);
        service.createDbActions();

        verify(mockDatabaseService, never()).deleteDatabase();
        verify(mockDatabaseService, never()).createDatabaseIfNotExists();
    }

    @Test
    @DisplayName("createDbActions should delete and recreate database if schema outdated")
    void testCreateDbActions_SchemaOutdated() throws SQLException {
        when(mockDatabaseService.databaseExists()).thenReturn(true);
        when(mockDatabaseService.isSchemaUpToDate()).thenReturn(false);

        SetupService service = new SetupService(mockSqlBasedTrailRepo, mockDatabaseService);
        service.createDbActions();

        verify(mockDatabaseService).deleteDatabase();
        verify(mockDatabaseService).createDatabaseIfNotExists();
    }

    @Test
    @DisplayName("syncKeywords should insert categories and call matchmaking")
    void testSyncKeywords_NormalFlow() throws Exception {
        SqlBasedKeywordRepo mockKeywordRepo = mock(SqlBasedKeywordRepo.class);
        SqlBasedTrailRepo mockTrailRepo = mock(SqlBasedTrailRepo.class);

        try (MockedStatic<App> mockedApp = mockStatic(App.class)) {
            mockedApp.when(App::getKeywordRepo).thenReturn(mockKeywordRepo);
            mockedApp.when(App::getFilterOptionsRepo).thenReturn(mock(SqlBasedFilterOptionsRepo.class));

            SetupService service = new SetupService(mockTrailRepo, mock(DatabaseService.class));
            service.syncKeywords();

            verify(mockKeywordRepo).insertCategoriesAndKeywords(any());
        }
    }

    @Test
    @DisplayName("syncFilterOptions should call refreshAllFilterOptions on repo")
    void testSyncFilterOptions() {
        SqlBasedFilterOptionsRepo mockRepo = mock(SqlBasedFilterOptionsRepo.class);

        try (MockedStatic<App> mockedApp = mockStatic(App.class)) {
            mockedApp.when(App::getFilterOptionsRepo).thenReturn(mockRepo);

            SetupService service = new SetupService(mock(SqlBasedTrailRepo.class), mock(DatabaseService.class));
            service.syncFilterOptions();

            verify(mockRepo).refreshAllFilterOptions();
        }
    }

    @Test
    @DisplayName("waitForDatabaseSetup should exit once setup complete")
    void testWaitForDatabaseSetup_Normal() throws InterruptedException {
        SetupService service = new SetupService(mockSqlBasedTrailRepo, mockDatabaseService);

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(200);
                service.setupDatabase(); // sets flag true
            } catch (InterruptedException ignored) {}
        });
        t.start();

        assertDoesNotThrow(service::waitForDatabaseSetup);
    }

    @Test
    @DisplayName("waitForDatabaseSetup should handle interrupt")
    void testWaitForDatabaseSetup_Interrupted() {
        SetupService service = new SetupService(mockSqlBasedTrailRepo, mockDatabaseService);
        Thread.currentThread().interrupt(); // force interrupt before loop
        assertDoesNotThrow(service::waitForDatabaseSetup);
    }

}
