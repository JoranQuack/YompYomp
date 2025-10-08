package seng202.team5.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import seng202.team5.data.DatabaseService;
import seng202.team5.data.QueryHelper;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.exceptions.MatchmakingFailedException;
import seng202.team5.models.Trail;
import seng202.team5.models.User;

public class UserService {
    private boolean isGuest;
    private User cachedUser;
    private final DatabaseService databaseService;
    private final QueryHelper queryHelper;

    // SQL Constants
    private static final String UPSERT_SQL = """
            INSERT INTO user (
                id, name, regions, isFamilyFriendly, isAccessible,
                experienceLevel, gradientPreference, bushPreference,
                reservePreference, lakeRiverPreference, coastPreference,
                mountainPreference, wildlifePreference, historicPreference,
                waterfallPreference, isProfileComplete, profilePicture
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET
                name=excluded.name,
                regions=excluded.regions,
                isFamilyFriendly=excluded.isFamilyFriendly,
                isAccessible=excluded.isAccessible,
                experienceLevel=excluded.experienceLevel,
                gradientPreference=excluded.gradientPreference,
                bushPreference=excluded.bushPreference,
                reservePreference=excluded.reservePreference,
                lakeRiverPreference=excluded.lakeRiverPreference,
                coastPreference=excluded.coastPreference,
                mountainPreference=excluded.mountainPreference,
                wildlifePreference=excluded.wildlifePreference,
                historicPreference=excluded.historicPreference,
                waterfallPreference=excluded.waterfallPreference,
                isProfileComplete=excluded.isProfileComplete,
                profilePicture=excluded.profilePicture
            """;

    /**
     * Constructor for UserService with custom DatabaseService (for testing)
     *
     * @param databaseService the database service to use
     */
    public UserService(DatabaseService databaseService) {
        this.isGuest = false;
        this.databaseService = databaseService;
        this.queryHelper = new QueryHelper(databaseService);
    }

    /**
     * Get the current user from cache or database.
     *
     * @return the current user loaded from cache or database, or null if user is
     *         guest or no user exists
     */
    public User getUser() {
        if (isGuest) {
            return null;
        }

        if (cachedUser != null) {
            return cachedUser;
        }

        cachedUser = loadUserFromDatabase();
        return cachedUser;
    }

    /**
     * Load the user from the database.
     *
     * @return the user loaded from the database, or guest user if none found
     */
    private User loadUserFromDatabase() {
        List<User> users = queryHelper.executeQuery("SELECT * FROM user LIMIT 1", null, this::mapRowToUser);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Save user to database immediately.
     *
     * @param user the user to save
     */
    public void saveUserToDatabase(User user) {
        if (user != null && !isGuest) {
            queryHelper.executeUpdate(UPSERT_SQL, stmt -> setUserParameters(stmt, user));
        }
    }

    /**
     * Save user to database
     *
     * @param user the user to save
     */
    public void saveUser(User user) {
        if (user != null && !isGuest) {
            user.setProfileComplete(true);
            saveUserToDatabase(user);
            this.cachedUser = user;
        }
    }

    /**
     * Set the current user by saving it directly to the database and updating the
     * cache.
     *
     * @param user the user to set and save
     */
    public void setUser(User user) {
        this.isGuest = false; // Setting a user means no longer a guest
        this.cachedUser = user;
        if (user != null) {
            saveUserToDatabase(user);
        }
    }

    /**
     * Checks if a user's name choice is valid.
     *
     * @param name the name to validate
     * @return true if the name is not null, not empty, and not "Guest User"
     */
    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() <= 30 &&
               !name.equalsIgnoreCase("Guest User") && !name.equalsIgnoreCase("null");
    }

    /**
     * Set the service to guest mode.
     */
    public void setGuest(boolean isGuest) {
        if (isGuest) {
            clearUser();
        } else {
            this.cachedUser = null;
        }
        this.isGuest = isGuest;
    }

    /**
     * Check if current user is a guest.
     *
     * @return true if user is a guest, false otherwise
     */
    public boolean isGuest() {
        return isGuest;
    }

    /**
     * Removes last user if exists and resets to clean state.
     */
    public void clearUser() {
        SqlBasedTrailRepo trailRepo = new SqlBasedTrailRepo(databaseService);
        trailRepo.clearUserWeights();
        queryHelper.executeUpdate("DELETE FROM user", null);
        this.cachedUser = null;
        this.isGuest = false;
    }

    /**
     * Returns user object to be used when skip is clicked
     */
    public User getUserAfterSkip() {
        User prevUser = getUser();
        if (prevUser != null && prevUser.getName() != null) {
            return prevUser;
        } else {
            setGuest(true);
            return null;
        }
    }

    /**
     * Clean up incomplete profiles on app startup.
     * This removes any user profiles that were not fully completed.
     */
    public void cleanupIncompleteProfiles() {
        queryHelper.executeUpdate("DELETE FROM user WHERE isProfileComplete = 0", null);
        this.cachedUser = null;
    }

    /**
     * Mark the current user's profile as complete.
     * This should be called when the user finishes the quiz.
     */
    public void markProfileComplete() {
        User user = getUser();
        if (user != null && !isGuest) {
            user.setProfileComplete(true);
            saveUserToDatabase(user);
            this.cachedUser = user;
        }
    }

    /**
     * Check if there's a user in the database with an incomplete profile.
     *
     * @return true if an incomplete profile exists
     */
    public boolean hasIncompleteProfile() {
        List<User> incompleteUsers = queryHelper.executeQuery(
                "SELECT * FROM user WHERE isProfileComplete = 0 LIMIT 1", null, this::mapRowToUser);
        return !incompleteUsers.isEmpty();
    }

    /**
     * Maps a database row to a User object.
     *
     * @param row the database row
     * @return the User object
     */
    private User mapRowToUser(ResultSet row) {
        try {
            String regionsString = row.getString("regions");
            List<String> regions = (regionsString != null && !regionsString.isEmpty())
                    ? List.of(regionsString.split(","))
                    : List.of();

            return new User(
                    row.getInt("id"),
                    row.getString("name"),
                    regions,
                    row.getBoolean("isFamilyFriendly"),
                    row.getBoolean("isAccessible"),
                    row.getInt("experienceLevel"),
                    row.getInt("gradientPreference"),
                    row.getInt("bushPreference"),
                    row.getInt("reservePreference"),
                    row.getInt("lakeRiverPreference"),
                    row.getInt("coastPreference"),
                    row.getInt("mountainPreference"),
                    row.getInt("wildlifePreference"),
                    row.getInt("historicPreference"),
                    row.getInt("waterfallPreference"),
                    row.getBoolean("isProfileComplete"),
                    row.getString("profilePicture"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Binds user fields to the prepared statement. The order must match.
     *
     * @param stmt prepared statement to bind
     * @param user source of values
     */
    private void setUserParameters(java.sql.PreparedStatement stmt, User user) throws java.sql.SQLException {
        stmt.setInt(1, user.getId());
        stmt.setString(2, user.getName());
        stmt.setString(3, user.getRegion() != null ? String.join(",", user.getRegion()) : "");
        stmt.setBoolean(4, user.isFamilyFriendly());
        stmt.setBoolean(5, user.isAccessible());
        stmt.setInt(6, user.getExperienceLevel());
        stmt.setInt(7, user.getGradientPreference());
        stmt.setInt(8, user.getBushPreference());
        stmt.setInt(9, user.getReservePreference());
        stmt.setInt(10, user.getLakeRiverPreference());
        stmt.setInt(11, user.getCoastPreference());
        stmt.setInt(12, user.getMountainPreference());
        stmt.setInt(13, user.getWildlifePreference());
        stmt.setInt(14, user.getHistoricPreference());
        stmt.setInt(15, user.getWaterfallPreference());
        stmt.setBoolean(16, user.isProfileComplete());
        stmt.setString(17, user.getProfilePicture());
    }

    /**
     * Returns a map of pairs category : count
     * To be used for pie chart
     * Will be updated later after trip logging is merged
     */
    public Map<String, Integer> getTrailStats() {
        SqlBasedTrailRepo trailRepo = new SqlBasedTrailRepo(databaseService);
        MatchmakingService matchmakingService = new MatchmakingService(databaseService);
        List<Trail> recommendedTrails = trailRepo.getRecommendedTrails();
        Map<String, Integer> trailStats = new HashMap<>();
        for (Trail trail : recommendedTrails) {
            try {
                Set<String> categories = matchmakingService.categoriseTrail(trail);
                for (String category : categories) {
                    if (trailStats.containsKey(category)) {
                        trailStats.put(category, trailStats.get(category) + 1);
                    } else {
                        trailStats.put(category, 1);
                    }
                }
            } catch (MatchmakingFailedException e) {
                System.out.println(e);
            }
        }
        return trailStats;
    }
}
