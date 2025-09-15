package seng202.team5.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import seng202.team5.data.DatabaseService;
import seng202.team5.models.User;
import seng202.team5.utils.QueryHelper;

public class UserService {
    User user;
    private final DatabaseService databaseService;

    // SQL Constants
    private static final String UPSERT_SQL = """
            INSERT INTO user (
                id, type, name, regions, isFamilyFriendly, isAccessible,
                experienceLevel, gradientPreference, bushPreference,
                reservePreference, lakeRiverPreference, coastPreference,
                mountainPreference, wildlifePreference, historicPreference,
                waterfallPreference
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET
                type=excluded.type,
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
                waterfallPreference=excluded.waterfallPreference
            """;

    /**
     * Constructor for UserService
     */
    public UserService() {
        this.user = null;
        this.databaseService = new DatabaseService();
    }

    /**
     * Constructor for UserService with custom DatabaseService (for testing)
     *
     * @param databaseService the database service to use
     */
    public UserService(DatabaseService databaseService) {
        this.user = null;
        this.databaseService = databaseService;
    }

    /**
     * Get the current user.
     *
     * @return the current user
     */
    public User getUser() {
        if (user == null) {
            user = loadUserFromDatabase();
        }
        return user;
    }

    /**
     * Load the user from the database.
     *
     * @return the user loaded from the database, or guest user if none found
     */
    private User loadUserFromDatabase() {
        QueryHelper queryHelper = new QueryHelper(databaseService);

        List<User> users = queryHelper.executeQuery("SELECT * FROM user LIMIT 1", null, this::mapRowToUser);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Checks if a user's name choice is valid.
     * @param name the name to validate
     * @return true if the name is not null, not empty, and not "Guest User"
     */
    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && !name.equalsIgnoreCase("Guest User") && !name.equalsIgnoreCase("null");
    }

    /**
     * Checks if the user's chosen name is valid, if so,
     * it sets the current user and updates it in the database if needed.
     * Otherwise, it does nothing and lets the controllers handle the invalid input.
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        if (!isValidName(user.getName())) {
            return;
        }

        this.user = user;
        QueryHelper queryHelper = new QueryHelper(databaseService);

        queryHelper.executeUpdate(UPSERT_SQL, stmt -> setUserParameters(stmt, user));
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
                    row.getString("type"),
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
                    row.getInt("waterfallPreference"));
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
        stmt.setString(2, user.getType());
        stmt.setString(3, user.getName());
        stmt.setString(4, user.getRegion() != null ? String.join(",", user.getRegion()) : "");
        stmt.setBoolean(5, user.isFamilyFriendly());
        stmt.setBoolean(6, user.isAccessible());
        stmt.setInt(7, user.getExperienceLevel());
        stmt.setInt(8, user.getGradientPreference());
        stmt.setInt(9, user.getBushPreference());
        stmt.setInt(10, user.getReservePreference());
        stmt.setInt(11, user.getLakeRiverPreference());
        stmt.setInt(12, user.getCoastPreference());
        stmt.setInt(13, user.getMountainPreference());
        stmt.setInt(14, user.getWildlifePreference());
        stmt.setInt(15, user.getHistoricPreference());
        stmt.setInt(16, user.getWaterfallPreference());
    }
}
