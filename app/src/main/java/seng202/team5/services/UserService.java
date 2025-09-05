package seng202.team5.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.management.Query;

import seng202.team5.data.DatabaseService;
import seng202.team5.data.QueryHelper;
import seng202.team5.models.User;

public class UserService {
    User user;

    /**
     * Constructor for UserService
     */
    public UserService() {
        this.user = null;
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
        DatabaseService dbService = new DatabaseService();
        QueryHelper queryHelper = new QueryHelper(dbService);

        List<User> users = queryHelper.executeQuery("SELECT * FROM users LIMIT 1", null, this::mapRowToUser);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Set the current user, and updates it in the database if needed.
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;

        DatabaseService dbService = new DatabaseService();

        // TODO: update user in database if needed

    }

    /**
     * Maps a database row to a User object.
     *
     * @param row the database row
     * @return the User object
     */
    private User mapRowToUser(ResultSet row) {
        try {
            return new User(
                    row.getInt("id"),
                    row.getString("type"),
                    row.getString("name"),
                    List.of(row.getString("regions").split(",")),
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
}
