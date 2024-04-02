package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminService {

    // Method to list all users
    public List<User> listUsers() {
        // Logic to list all users
        Map<String, User> userMap = DataManager.getUsersMap();
        return new ArrayList<>(userMap.values()); // Convert map values to a list and return
    }

    // Method to create a new user
    public static boolean createUser(String newUsername) {
        if (DataManager.getUsersMap().containsKey(newUsername)) {
            return false;
        }
        User newUser = new User(newUsername);
        DataManager.saveUserData(newUser);
        return true;
    }

    // Method to delete an existing user
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
