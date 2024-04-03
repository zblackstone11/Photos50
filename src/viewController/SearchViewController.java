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
import model.Album;
import model.DataManager;
import model.Photo;
import model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @FXML
    private void handleSearchByDate() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            showErrorDialog("Please select a valid start and end date.");
            return;
        }

        // Convert LocalDate to LocalDateTime at the start of the start day and the end of the end day
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Photo> matchingPhotos = new ArrayList<>();

        for (Album album : currentUser.getAlbums()) {
            for (Photo photo : album.getPhotos()) {
                LocalDateTime photoDate = photo.getDate();
                if ((photoDate.isEqual(startDateTime) || photoDate.isAfter(startDateTime)) &&
                    (photoDate.isEqual(endDateTime) || photoDate.isBefore(endDateTime))) {
                    matchingPhotos.add(photo);
                }
            }
        }

        if (matchingPhotos.isEmpty()) {
            showErrorDialog("No photos found in the specified date range.");
        } else {
            searchResultsView.getItems().setAll(matchingPhotos);
            // Make sure your ListView is set up to display photos appropriately,
            // possibly by setting a custom cell factory.
        }
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
        // Show a dialog to get the name of the new album
        // Add the new album to the current user's list of albums
        // Close the SearchView window
    }

    @FXML
    private void handleClear() {
        // Reset date pickers
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        // Reset tag fields
        tag1TypeField.setText("");
        tag1ValueField.setText("");
        tag2TypeField.setText("");
        tag2ValueField.setText("");
        tagSearchType.setValue(null); // Reset the ComboBox selection

        // Clear the search results
        searchResultsView.getItems().clear();
    }
    
    @FXML
    private void handleLogout() {
    // Retrieve the map of users
        Map<String, User> usersMap = DataManager.getUsersMap();

        // Iterate over the map and save each user's data
        for (User user : usersMap.values()) {
            DataManager.saveUserData(user);
        }
        // Close the current user window
        Stage currentStage = (Stage) searchResultsView.getScene().getWindow();
        currentStage.close();
    
        // Open the login window
        openLoginWindow();
    }

    @FXML
    private void handleQuit() {
        // Logic to handle quit action
        // Example: Save any changes and close the application
        // Retrieve the map of users
        Map<String, User> usersMap = DataManager.getUsersMap();

        // Iterate over the map and save each user's data
        for (User user : usersMap.values()) {
            DataManager.saveUserData(user);
        }
        // Close the entire application
        Stage stage = (Stage) searchResultsView.getScene().getWindow();
        stage.close();
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

    // Method to go back to the login view
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
}
