package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import seng202.team5.data.DatabaseService;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @Mock
    private DatabaseService mockDatabaseService;

    @Mock
    private SqlBasedTrailRepo mockSqlBasedTrailRepo;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        // Set up database mocking
        when(mockDatabaseService.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        userService = new UserService(mockSqlBasedTrailRepo, mockDatabaseService);
        testUser = new User(
                1,
                "Test User",
                Arrays.asList("Canterbury", "Otago"),
                true,
                false,
                3,
                2,
                4,
                1,
                3,
                2,
                4,
                3,
                2,
                1,
                true, // isProfileComplete = true
                "");
    }

    @Test
    @DisplayName("Should create UserService instance with default DatabaseService")
    void testConstructor() {
        UserService newService = new UserService(mockSqlBasedTrailRepo, new DatabaseService());
        assertNotNull(newService);
    }

    @Test
    @DisplayName("Should create UserService instance with injected DatabaseService")
    void testConstructorWithDatabaseService() {
        UserService newService = new UserService(mockSqlBasedTrailRepo, mockDatabaseService);
        assertNotNull(newService);
    }

    @Test
    @DisplayName("Should return null when user is guest")
    void testGetUserReturnsNullWhenGuest() {
        userService.setGuest(true);

        User retrievedUser = userService.getUser();
        assertNull(retrievedUser);
        assertTrue(userService.isGuest());
    }

    @Test
    @DisplayName("Should not save guest user to database")
    void testSaveUserToDatabaseDoesNotSaveGuest() {
        userService.setGuest(true);
        assertTrue(userService.isGuest());

        userService.saveUserToDatabase(testUser);
        assertTrue(userService.isGuest());
    }
}
