package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import model.DataManager;
import model.User;
import model.AdminService;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Controller class for the admin view.
 * @author ZB SL
 */
public class AdminViewController {

    /**
     * The TableView to display the list of users.
     */
    @FXML
    private TableView<User> userTableView;

    /**
     * Initializes the admin view.
     * This method is called automatically after the fxml file has been loaded.
     * It is used to initialize the view with the current users.
     */
    @FXML
    private void initialize() {
        refreshUserListView();
    }
    
    /**
     * Handles the logout button click event.
     * Saves the user data and closes the current window.
     * Opens the login window.
     */
    @FXML
    private void handleLogout() {
        // Retrieve the map of users
        Map<String, User> usersMap = DataManager.getUsersMap();

        // Iterate over the map and save each user's data
        for (User user : usersMap.values()) {
            DataManager.saveUserData(user);
        }
        // Close the current admin window
        Stage currentStage = (Stage) userTableView.getScene().getWindow();
        currentStage.close();
    
        // Open the login window
        openLoginWindow();
    }

    /**
     * Handles the quit button click event.
     * Saves the user data and closes the entire application.
     */
    @FXML
    private void handleQuit() {
        // Retrieve the map of users
        Map<String, User> usersMap = DataManager.getUsersMap();
    
        // Iterate over the map and save each user's data
        for (User user : usersMap.values()) {
            DataManager.saveUserData(user);
        }
        // Close the entire application
        Stage primaryStage = (Stage) userTableView.getScene().getWindow();
        primaryStage.close();
    }    

    /**
     * Handles the create new user button click event.
     * Prompts the admin to enter a new username and creates a new user with that username.
     * If the username is already in use, shows an error message.
     * If the username is empty, shows an error message.
     * If the user is created successfully, refreshes the user list view.
     */
    @FXML
    private void handleCreateNewUser() {
        // Prompt for the new username using a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New User");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter username for new user:");

        // Show the dialog using showAndWait() to wait for the user's input and capture the result in an Optional
        Optional<String> result = dialog.showAndWait();
        // Use lambda expression to handle the result if present (user clicked OK) and create the user, uses consumer functional interface
        result.ifPresent(username -> {
            // Check if the username is not empty and create the user
            if (!username.trim().isEmpty()) {
                // Call the AdminService to create the user, which checks if the username is already in use
                boolean success = AdminService.createUser(username);
                if (success) {
                    // Success, refresh the user list view
                    refreshUserListView();
                } else {
                    // Failure, show error dialog that the username is already in use
                    showErrorDialog("Failed to create new user. Cannot have repeat usernames.");
                }
            } else {
                // No username entered, show an error dialog
                showErrorDialog("Username cannot be empty.");
            }
        });
    }

    /**
     * Handles the delete selected user button click event.
     * Deletes the selected user from the list of users.
     */
    @FXML
    private void handleDeleteSelectedUser() {
        // Get the selected user from the TableView using getSelectionModel().getSelectedItem() and store it in a variable
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showErrorDialog("No user selected.");
            return;
        }
        if ("admin".equalsIgnoreCase(selectedUser.getUsername())) {
            showErrorDialog("Cannot delete the admin user.");
            return;
        }
        // Show a confirmation dialog using an Alert with AlertType.CONFIRMATION
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the user " + selectedUser.getUsername() + "?", ButtonType.YES, ButtonType.NO);
        confirmAlert.setHeaderText("Confirm Deletion");
        // Use showAndWait() to wait for the user's response and capture the result in an Optional
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean success = AdminService.deleteUser(selectedUser.getUsername());
            if (success) {
                refreshUserListView(); // Refresh the list view to reflect the changes
            } else {
                showErrorDialog("Failed to delete the user."); // Should not happen
            }
        }
    }    
    
    /**
     * Opens the login window.
     */
    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            // Handle IOException, possibly with an error dialog
            showErrorDialog("Error opening login window: " + e.getMessage());
        }
    }

    /**
     * Refreshes the user list view with the current users.
     */
    private void refreshUserListView() {
        userTableView.getItems().clear(); // Clear the existing items in the TableView
        // Add all current users to the TableView using DataManager.getUsersMap().values().forEach() lambda expression consumer functional interface
        DataManager.getUsersMap().values().forEach(user -> userTableView.getItems().add(user)); // Add all current users to the TableView
    }    

    /**
     * Shows an error dialog with the specified message.
     * @param message The message to display in the dialog.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    } 
}
