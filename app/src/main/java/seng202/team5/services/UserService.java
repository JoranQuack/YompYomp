package seng202.team5.services;

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

        // TODO: load user from database
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
}
