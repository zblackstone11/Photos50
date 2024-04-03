package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service class for the admin user. This class provides methods to list all users, create a new user, and delete an existing user.
 */
public class AdminService {

    /**
     * Method to list all users in the application.
     * @return A list of all users in the application.
     */
    public List<User> listUsers() {
        // Get the users map from the data manager
        Map<String, User> userMap = DataManager.getUsersMap();
        return new ArrayList<>(userMap.values()); // Convert map values to a list and return
    }

    /**
     * Method to create a new user.
     * @param newUsername The username of the new user.
     * @return True if the user was created successfully, false if the user already exists.
     */
    public static boolean createUser(String newUsername) {
        // Convert the username to lowercase
        String newUsernameLower = newUsername.toLowerCase();
        // Iterate over the existing usernames to check for a case-insensitive match
        for (String existingUsername : DataManager.getUsersMap().keySet()) {
            if (existingUsername.toLowerCase().equals(newUsernameLower)) {
                return false; // Found a case-insensitive match, user already exists
            }
        }
        User newUser = new User(newUsername);
        DataManager.saveUserData(newUser);
        return true;
    }

    /**
     * Method to delete a user.
     * @param usernameToDelete The username of the user to delete.
     * @return True if the user was deleted successfully, false if the user doesn't exist.
     */
    public static boolean deleteUser(String usernameToDelete) {
        Map<String, User> usersMap = DataManager.getUsersMap();
        if (usersMap.containsKey(usernameToDelete)) {
            usersMap.remove(usernameToDelete);
            DataManager.saveUsersMap();
            return true;
        }
        return false;
    }
}
