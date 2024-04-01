package viewController;

import java.io.IOException;

// Represents the controller for the login view

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DataManager;
import model.User;

public class LoginController {

    @FXML
    private TextField usernameField; // Matches the TextField for username in FXML

    // Method to handle login action
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            // Handle empty username, maybe show an error message
            showErrorDialog("Please enter a username.");
            return;
        }

        if ("admin".equals(username)) {
            // Handle admin login
            openAdminView();
        } else {
            User user = DataManager.loadUserData(username);
            if (user != null) {
                // User exists, proceed with login
                openUserView(user);
            } else {
                // Handle case where user doesn't exist, maybe show an error or create a new user
                showErrorDialog("User does not exist.");
            }
        }
    }

    private void openAdminView() {
        try {
            // Load the FXML file for the AdminView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewController/AdminView.fxml"));
            Parent root = loader.load();

            // Get the current stage (from the login button, for example)
            Stage currentStage = (Stage) usernameField.getScene().getWindow();

            // Set the admin view to the current stage
            currentStage.setTitle("Admin Dashboard");
            currentStage.setScene(new Scene(root, 800, 600)); // Set the size of the admin view as needed
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException, such as showing an error dialog
        }
    }

    // Method to open the general user view
    private void openUserView(User user) {
        // Code to switch to the user view, passing in the user object
    }

    // Method to show an error dialog
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
