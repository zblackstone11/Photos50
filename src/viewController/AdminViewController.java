package viewController;

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
     */
    @FXML
    private void handleCreateNewUser() {
        // Prompt for the new username, for example, using a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New User");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter username for new user:");
        // If username is already in use, show an error message

        // Show the dialog and capture the result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            // Check if the username is not empty and create the user
            if (!username.trim().isEmpty()) {
                // Assuming adminUser is the currently logged-in admin User object
                boolean success = AdminService.createUser(username);
                if (success) {
                    // Success, maybe refresh the user list view or show a confirmation
                    refreshUserListView();
                } else {
                    // Failure, show an error message
                    showErrorDialog("Failed to create new user.");
                }
            } else {
                // No username entered, show an error or do nothing
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
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showErrorDialog("No user selected.");
            return;
        }
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the user " + selectedUser.getUsername() + "?", ButtonType.YES, ButtonType.NO);
        confirmAlert.setHeaderText("Confirm Deletion");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean success = AdminService.deleteUser(selectedUser.getUsername());
            if (success) {
                refreshUserListView(); // Refresh the list view to reflect the changes
            } else {
                showErrorDialog("Failed to delete the user.");
            }
        }
    }    
    
    /**
     * Opens the login window.
     */
    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewController/LoginView.fxml"));
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
