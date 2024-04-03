package viewController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import model.Photo;
import model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SearchViewController {

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField tag1TypeField;
    @FXML
    private TextField tag1ValueField;
    @FXML
    private TextField tag2TypeField;
    @FXML
    private TextField tag2ValueField;
    @FXML
    private ComboBox<String> tagSearchType;
    @FXML
    private ListView<Photo> searchResultsView;

    private User currentUser;

    public void initialize(User currentUser) {
        this.currentUser = currentUser;
        tagSearchType.setItems(FXCollections.observableArrayList("Single", "Conjunctive", "Disjunctive"));
    }

    // Method to handle searching by date range
    @FXML
    private void handleSearchByDate() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        // Implement the search logic and update searchResultsView
    }

    // Method to handle searching by tag
    @FXML
    private void handleSearchByTag() {
        String tag1Type = tag1TypeField.getText();
        String tag1Value = tag1ValueField.getText();
        String tag2Type = tag2TypeField.getText();
        String tag2Value = tag2ValueField.getText();
        String searchType = tagSearchType.getValue();
        // Implement the search logic based on searchType and update searchResultsView
    }

    // Method to create an album from the search results
    @FXML
    private void handleCreateAlbumFromResults() {
        // Implement the logic to create a new album from the items in searchResultsView
    }

    @FXML
    private void handleLogout() {
        // Logic for logging out and returning to the login view
    }

    @FXML
    private void handleQuit() {
        // Logic for quitting the application
    }

    @FXML
    private void handleBackToUserView() {
        // Logic for returning to the UserView
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewController/UserView.fxml"));
            Parent root = loader.load();

            // Get the controller for UserView and set the current user
            UserViewController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            // Create and display the new stage
            Stage stage = new Stage();
            stage.setTitle("Your Albums");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current SearchView window
            Stage currentStage = (Stage) searchResultsView.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException, such as showing an error dialog
            showErrorDialog("Error returning to user view: " + e.getMessage());
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
