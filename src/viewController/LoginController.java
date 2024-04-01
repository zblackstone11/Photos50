package viewController;

// Represents the controller for the login view

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
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
            }
        }
    }

    // Method to open the admin view
    private void openAdminView() {
        // Code to switch to the admin view
    }

    // Method to open the general user view
    private void openUserView(User user) {
        // Code to switch to the user view, passing in the user object
    }
}
