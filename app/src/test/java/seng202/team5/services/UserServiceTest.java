package seng202.team5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import seng202.team5.data.DatabaseService;
import seng202.team5.data.QueryHelper;
import seng202.team5.models.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private DatabaseService mockDatabaseService;

    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(mockDatabaseService);
        testUser = new User(
                1,
                "profiled",
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
                1);
    }

    @Test
    @DisplayName("Should create UserService instance with default DatabaseService")
    void testConstructor() {
        UserService newService = new UserService();
        assertNotNull(newService);
    }

    @Test
    @DisplayName("Should create UserService instance with injected DatabaseService")
    void testConstructorWithDatabaseService() {
        UserService newService = new UserService(mockDatabaseService);
        assertNotNull(newService);
    }

    @Test
    @DisplayName("Should return existing user without DB call")
    void testGetUserWhenUserAlreadySet() {
        try (MockedConstruction<QueryHelper> queryMock = mockConstruction(QueryHelper.class)) {
            userService.setUser(testUser);

            User retrievedUser = userService.getUser();

            assertNotNull(retrievedUser);
            assertEquals(testUser.getId(), retrievedUser.getId());
            assertEquals(testUser.getName(), retrievedUser.getName());
            assertEquals(testUser.getType(), retrievedUser.getType());
        }
    }

    @Test
    @DisplayName("Should load user from DB when none in memory")
    void testGetUserLoadsFromDatabaseWhenNull() {
        try (MockedConstruction<QueryHelper> queryMock = mockConstruction(QueryHelper.class, (mock, context) -> {
            when(mock.executeQuery(anyString(), isNull(), any())).thenReturn(List.of(testUser));
        })) {

            User retrievedUser = userService.getUser();

            assertNotNull(retrievedUser);
            assertEquals(testUser.getId(), retrievedUser.getId());
            assertEquals(testUser.getName(), retrievedUser.getName());

            assertEquals(1, queryMock.constructed().size());
            verify(queryMock.constructed().get(0)).executeQuery(eq("SELECT * FROM user LIMIT 1"), isNull(), any());
        }
    }

    @Test
    @DisplayName("Should return null when no user in DB")
    void testGetUserReturnsNullWhenNoDatabaseUser() {
        try (MockedConstruction<QueryHelper> queryMock = mockConstruction(QueryHelper.class, (mock, context) -> {
            when(mock.executeQuery(anyString(), isNull(), any())).thenReturn(Collections.emptyList());
        })) {

            User retrievedUser = userService.getUser();
            assertNull(retrievedUser);
            assertEquals(1, queryMock.constructed().size());
        }
    }

    @Test
    @DisplayName("Should set user and update DB")
    void testSetUser() {
        try (MockedConstruction<QueryHelper> queryMock = mockConstruction(QueryHelper.class)) {
            userService.setUser(testUser);
            assertEquals(testUser, userService.getUser());

            assertEquals(1, queryMock.constructed().size());
            verify(queryMock.constructed().get(0)).executeUpdate(anyString(), any());
        }
    }
}
