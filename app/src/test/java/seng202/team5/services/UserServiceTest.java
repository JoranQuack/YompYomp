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
import java.sql.SQLException;
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

    @Test
    @DisplayName("Should return cached user when available")
    void testGetUserUsesCache() {
        userService.saveUser(testUser); // caches the user
        User result = userService.getUser();
        assertSame(testUser, result);
    }

    @Test
    @DisplayName("saveUser should mark user as profile complete and cache it")
    void testSaveUser() {
        testUser.setProfileComplete(false);
        userService.saveUser(testUser);
        assertTrue(testUser.isProfileComplete());
        assertEquals(testUser, userService.getUser());
    }

    @Test
    @DisplayName("setUser should unset guest mode and cache user")
    void testSetUser() {
        userService.setGuest(true);
        userService.setUser(testUser);
        assertFalse(userService.isGuest());
        assertEquals(testUser, userService.getUser());
    }

    @Test
    @DisplayName("isValidName should handle all edge cases")
    void testIsValidName() {
        assertTrue(userService.isValidName("Hayley"));
        assertFalse(userService.isValidName(""));
        assertFalse(userService.isValidName(null));
        assertFalse(userService.isValidName("Guest User"));
        assertFalse(userService.isValidName("null"));
        assertFalse(userService.isValidName("A".repeat(31)));
    }

    @Test
    @DisplayName("setGuest(true) should clear user and set guest mode")
    void testSetGuestTrue() {
        userService.saveUser(testUser);
        userService.setGuest(true);
        assertTrue(userService.isGuest());
        verify(mockSqlBasedTrailRepo).clearUserWeights();
    }

    @Test
    @DisplayName("setGuest(false) should disable guest mode")
    void testSetGuestFalse() {
        userService.setGuest(false);
        assertFalse(userService.isGuest());
    }

    @Test
    @DisplayName("clearUser should reset cached user and guest flag")
    void testClearUser() throws SQLException {
        userService.saveUser(testUser);
        userService.clearUser();
        assertNull(userService.getUser());
        assertFalse(userService.isGuest());
        verify(mockSqlBasedTrailRepo, times(1)).clearUserWeights();
        verify(mockPreparedStatement, atLeastOnce()).executeUpdate();
    }

    @Test
    @DisplayName("getUserAfterSkip should return previous user when valid")
    void testGetUserAfterSkipWithPreviousUser() {
        userService.saveUser(testUser);
        User result = userService.getUserAfterSkip();
        assertNotNull(result);
        assertEquals("Test User", result.getName());
    }

    @Test
    @DisplayName("getUserAfterSkip should return null when previous user missing or name null")
    void testGetUserAfterSkipNoUser() {
        assertNull(userService.getUserAfterSkip());
        User nameless = new User();
        userService.saveUser(nameless);
        assertNull(userService.getUserAfterSkip());
    }

    @Test
    @DisplayName("saveUserToDatabase should handle null user safely")
    void testSaveUserToDatabaseWithNull() {
        assertDoesNotThrow(() -> userService.saveUserToDatabase(null));
    }
}
