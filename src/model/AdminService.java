package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminService {

    // Method to list all users
    public List<User> listUsers(User adminUser) {
        if (!adminUser.isAdmin()) {
            System.out.println("User does not have admin privileges."); // CHANGE TO GUI HANDLING LATER
            return null; // Or an empty list to indicate no action taken
        }
        // Logic to list all users
        Map<String, User> userMap = DataManager.getUsersMap();
        return new ArrayList<>(userMap.values()); // Convert map values to a list and return
    }

    // Method to create a new user
    public static boolean createUser(User adminUser, String newUsername) {
        if (!adminUser.isAdmin()) {
            return false;
        }
        User newUser = new User(newUsername);
        try {
            DataManager.saveUserData(newUser);
            return true;
        } catch (IOException e) {
            e.printStackTrace(); // Change to GUI handling later
            return false;
        }
    }

    // Method to delete an existing user
    public static boolean deleteUser(User adminUser, String usernameToDelete) {
        if (!adminUser.isAdmin()) {
            return false;
        }
        Map<String, User> usersMap = DataManager.getUsersMap();
        if (usersMap.containsKey(usernameToDelete)) {
            usersMap.remove(usernameToDelete);
            try {
                DataManager.saveUsersMap();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // Additional helper methods or services to interact with data storage for user management might be needed.
}
