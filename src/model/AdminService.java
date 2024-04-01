package model;

import java.util.List;

public class AdminService {

    // Method to list all users
    public List<User> listUsers(User adminUser) {
        if (!adminUser.isAdmin()) {
            System.out.println("User does not have admin privileges."); // handle differently later
            return null; // Or an empty list to indicate no action taken
        }
        // Logic to list all users
        // For example, this could return a list of usernames or User objects depending on your design
        // Return the list of users
        return null;
    }

    // Method to create a new user
    public boolean createUser(User adminUser, String newUsername) {
        if (!adminUser.isAdmin()) {
            System.out.println("User does not have admin privileges.");
            return false; // Action not completed
        }
        // Logic to create a new user
        // Return true if the user was successfully created
        // Use the constuctor of the User class to create a new user
        
        User newUser = new User(newUsername);
        // Save the user data using the DataManager class

        // Return true if the user was successfully created
        return true;
    }

    // Method to delete an existing user
    public boolean deleteUser(User adminUser, String usernameToDelete) {
        if (!adminUser.isAdmin()) {
            System.out.println("User does not have admin privileges.");
            return false; // Action not completed
        }
        // Logic to delete the user
        // Return true if the user was successfully deleted
        // Use the DataManager class to delete the user data file
        usernameToDelete = null;
        // Return true if the user was successfully deleted
        return true;
    }

    // Additional helper methods or services to interact with data storage for user management might be needed.
}
