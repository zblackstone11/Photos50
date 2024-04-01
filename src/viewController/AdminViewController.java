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

// Represents the controller for the admin view, which handles admin-specific functionality such as user management.

public class AdminViewController {

    @FXML
    private TableView<User> userTableView;

    @FXML
    private void handleLogout() {
    // Retrieve the map of users
        Map<String, User> usersMap = DataManager.getUsersMap();

        // Iterate over the map and save each user's data
        for (User user : usersMap.values()) {
            try {
                DataManager.saveUserData(user);
            } catch (IOException e) {
                // Handle IOException, possibly with an error dialog
                showErrorDialog("Error saving user data: " + e.getMessage());
                return; // Exit method to avoid logging out if data wasn't saved properly
            }
        }
        // Close the current admin window
        Stage currentStage = (Stage) userTableView.getScene().getWindow();
        currentStage.close();
    
        // Open the login window
        openLoginWindow();
    }

    @FXML
    private void handleQuit() {
        // Retrieve the map of users
        Map<String, User> usersMap = DataManager.getUsersMap();
    
        // Iterate over the map and save each user's data
        for (User user : usersMap.values()) {
            try {
                DataManager.saveUserData(user);
            } catch (IOException e) {
                // Handle IOException, possibly with an error dialog
                showErrorDialog("Error saving user data: " + e.getMessage());
                return; // Exit method to avoid quitting if data wasn't saved properly
            }
        }
    
        // Close the entire application
        Stage primaryStage = (Stage) userTableView.getScene().getWindow();
        primaryStage.close();
    }    

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
                boolean success = AdminService.createUser(DataManager.loadUserData("admin"), username);
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

    // Refresh the TableView or ListView that displays users
    private void refreshUserListView() {
        // Implementation depends on how you've set up your TableView or ListView
        // For example, you might need to clear the items and add them again from DataManager.getUsersMap()
    }

    @FXML
    private void handleDeleteSelectedUser() {
        // Get the selected user from the TableView
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            // No user selected, show an error message or do nothing
            showErrorDialog("No user selected.");
            return;
        }

        // Confirm the deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the user " + selectedUser.getUsername() + "?", ButtonType.YES, ButtonType.NO);
        confirmAlert.setHeaderText("Confirm Deletion");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            // Assuming adminUser is the currently logged-in admin User object
            boolean success = AdminService.deleteUser(DataManager.loadUserData("admin"), selectedUser.getUsername());
            
            if (success) {
                // Deletion successful, remove the user from the TableView
                userTableView.getItems().remove(selectedUser);
                // Optionally, refresh the list or TableView if necessary
                refreshUserListView();
            } else {
                // Deletion failed, show an error message
                showErrorDialog("Failed to delete the user.");
            }
        }
    }


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

    // Method to show an error dialog
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    } 
}
