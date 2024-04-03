package viewController;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DataManager;
import model.User;

/**
 * Controller class for the LoginView FXML file.
 */
public class LoginController {

    /**
     * The TextField for the username input.
     */
    @FXML
    private TextField usernameField; // Matches the TextField for username in FXML

    /**
     * Method to handle the login button click.
     * This method is called when the login button is clicked in the FXML file.
     * It reads the username from the TextField and checks if the user exists.
     * If the user is an admin, it opens the AdminView.
     * If the user is a general user, it opens the UserView.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            // Handle empty username, maybe show an error message
            showErrorDialog("Please enter a username.");
            return;
        }

        if ("admin".equals(username.toLowerCase())) {
            // Handle admin login
            openAdminView();
        } else {
            User user = DataManager.loadUserData(username);
            if (user != null) {
                // User exists, proceed with login
                openUserView(user);
            } else {
                // Handle case where user doesn't exist, maybe show an error or create a new user
                showErrorDialog("User does not exist. Login to admin to create new user.");
            }
        }
    }

    /**
     * Method to open the AdminView.
     * This method is called when the user logs in as an admin.
     * It loads the AdminView FXML file and sets it to the current stage.
     */
    private void openAdminView() {
        try {
            // Load the FXML file for the AdminView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewController/AdminView.fxml"));
            Parent root = loader.load();

            // Get the current stage (from the login button, for example)
            Stage currentStage = (Stage) usernameField.getScene().getWindow();

            // Set the admin view to the current stage
            currentStage.setTitle("Admin Dashboard");
            currentStage.setScene(new Scene(root, 800, 600));
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException, such as showing an error dialog
        }
    }

    /**
     * Method to open the UserView.
     * This method is called when the user logs in as a general user.
     * It loads the UserView FXML file, sets the current user in the UserViewController,
     * and sets the UserView to the current stage.
     * @param user The user object for the current user.
     */
    private void openUserView(User user) {
        try {
            // Load the FXML file for the UserView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewController/UserView.fxml"));
            Parent root = loader.load();

            // Get the UserViewController from the loader
            UserViewController userViewController = loader.getController();
            
            // Set the current user in the UserViewController
            userViewController.setCurrentUser(user);

            // Get the current stage (from the login button, for example)
            Stage currentStage = (Stage) usernameField.getScene().getWindow();

            // Set the user view to the current stage
            currentStage.setTitle("User Dashboard");
            currentStage.setScene(new Scene(root, 800, 600));
            currentStage.show();
        } catch (IOException e) {
            // Show an error dialog if an IOException occurs
           showErrorDialog("An error occurred while opening the UserView.");
        }
    }

    /**
     * Method to show an error dialog.
     * This method is called when an error occurs, such as an empty username.
     * It shows an error dialog with the given message.
     * @param message The message to show in the error dialog.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
