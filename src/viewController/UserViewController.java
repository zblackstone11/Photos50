package viewController;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import model.Album;
import model.DataManager;
import model.User;

public class UserViewController {

    @FXML
    private TableView<Album> albumsTableView;

    @FXML
    private TableColumn<Album, String> albumNameColumn;

    @FXML
    private TableColumn<Album, Integer> numPhotosColumn;

    @FXML
    private TableColumn<Album, String> dateRangeColumn;

    private User currentUser;

    @FXML
    public void initialize() {
        // Initialization logic here
        // Example: Load current user and populate albums table
        currentUser = DataManager.loadUserData("stock"); // Replace with actual logic to determine the current user
        populateAlbumsTable();
    }

    private void populateAlbumsTable() {
        if (currentUser != null) {
            albumsTableView.getItems().setAll(currentUser.getAlbums());
            // Further setup if necessary, e.g., formatting the date range
            // Album class has methods to get the date range and number of photos
        }
    }

    @FXML
    private void handleLogout() {
        // Logic to handle logout action
        // Example: Save any changes, close the current window, and return to the login view
    }

    @FXML
    private void handleQuit() {
        // Logic to handle quit action
        // Example: Save any changes and close the application
        Stage stage = (Stage) albumsTableView.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCreateAlbum() {
        // Logic to handle create album action
        // Example: Open a dialog to enter album details, then add a new album
    }

    @FXML
    private void handleDeleteAlbum() {
        // Logic to handle delete album action
        // Example: Confirm deletion, then remove the selected album
    }

    @FXML
    private void handleRenameAlbum() {
        // Logic to handle rename album action
        // Example: Open a dialog to enter a new name, then rename the selected album
    }

    @FXML
    private void handleOpenAlbum() {
        // Logic to handle open album action
        // Example: Navigate to the album view, displaying photos and details of the selected album
    }

    @FXML
    private void handleSearchPhotos() {
        // Logic to handle search photos action
        // Example: Navigate to the search view, allowing the user to specify search criteria
    }

    // Additional methods as needed for the controller
}
