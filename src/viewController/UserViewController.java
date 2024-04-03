package viewController;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import model.Album;
import model.DataManager;
import model.Photo;
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
        // Set the cell value factories for the table columns
        albumNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        numPhotosColumn.setCellValueFactory(new PropertyValueFactory<>("numPhotos"));
        dateRangeColumn.setCellValueFactory(new PropertyValueFactory<>("dateRange"));

        // No need to populate the table here, as setCurrentUser will be called after the controller is initialized
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        populateAlbumsTable(); // This will populate the table once the current user is set
    }

    private void populateAlbumsTable() {
        if (currentUser != null) {
            // Add albums to the table view
            albumsTableView.getItems().setAll(currentUser.getAlbums());
        }
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
        Stage currentStage = (Stage) albumsTableView.getScene().getWindow();
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
        Stage stage = (Stage) albumsTableView.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCreateAlbum() {
        // Create a TextInputDialog to ask the user for the album name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Album");
        dialog.setHeaderText(null); // No header text
        dialog.setContentText("Please enter the album name:");

        // Show the dialog and wait for the user to enter a name
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(albumName -> {
            // Check for empty or duplicate album names
            if (albumName.trim().isEmpty()) {
                showErrorDialog("Album name cannot be empty.");
                return;
            }
            for (Album album : currentUser.getAlbums()) {
                if (album.getName().equalsIgnoreCase(albumName.trim())) {
                    showErrorDialog("An album with this name already exists.");
                    return;
                }
            }

            // Create the new album and add it to the user's list of albums
            Album newAlbum = new Album(albumName.trim());
            currentUser.createAlbum(newAlbum); // Assuming createAlbum method adds the album to the user's list

            // Update the TableView to reflect the new album
            populateAlbumsTable();
        });
    }

    @FXML
    private void handleDeleteAlbum() {
        // Get the selected album from the TableView
        Album selectedAlbum = albumsTableView.getSelectionModel().getSelectedItem();

        // Check if an album is selected
        if (selectedAlbum == null) {
            showErrorDialog("Please select an album to delete.");
            return;
        }

        // Confirm the deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the album '" + selectedAlbum.getName() + "'?", ButtonType.YES, ButtonType.NO);
        confirmAlert.setHeaderText("Confirm Deletion");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            // Remove the selected album from the user's list of albums
            currentUser.deleteAlbum(selectedAlbum); // Assuming deleteAlbum method removes the album from the user's list

            // Update the TableView to reflect the album deletion
            populateAlbumsTable();
        }
    }

    @FXML
    private void handleRenameAlbum() {
        // Get the selected album from the TableView
        Album selectedAlbum = albumsTableView.getSelectionModel().getSelectedItem();
    
        // Check if an album is selected
        if (selectedAlbum == null) {
            showErrorDialog("Please select an album to rename.");
            return;
        }
    
        // Show dialog to enter the new album name
        TextInputDialog dialog = new TextInputDialog(selectedAlbum.getName());
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Rename album '" + selectedAlbum.getName() + "'");
        dialog.setContentText("Enter new album name:");
    
        // Handle the user response
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            // Validate the new name
            if (newName.trim().isEmpty()) {
                showErrorDialog("Album name cannot be empty.");
            } else if (currentUser.albumExists(newName.trim())) {
                showErrorDialog("An album with this name already exists.");
            } else {
                // Rename the selected album
                selectedAlbum.setName(newName.trim());
    
                // Update the TableView to reflect the name change
                populateAlbumsTable();
            }
        });
    }

    @FXML
    private void handleOpenAlbum() {
        // Get the selected album from the TableView
        Album selectedAlbum = albumsTableView.getSelectionModel().getSelectedItem();

        // Check if an album is selected
        if (selectedAlbum == null) {
            showErrorDialog("Please select an album to open.");
            return;
        }

        // Navigate to the AlbumView, passing the selected album and the current user
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewController/AlbumView.fxml"));
            Parent root = loader.load();

            // Get the controller for AlbumView and set the album and user
            AlbumViewController controller = loader.getController();
            controller.setAlbumAndUser(selectedAlbum, currentUser);

            // Create and display the new stage
            Stage stage = new Stage();
            stage.setTitle("Album: " + selectedAlbum.getName());
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current UserView window
            Stage currentStage = (Stage) albumsTableView.getScene().getWindow();
            currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog("Error opening album view: " + e.getMessage());
            }
    }

    @FXML
    private void handleSearchPhotos() {
        try {
            // Load the FXML file for the SearchView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewController/SearchView.fxml"));
            Parent root = loader.load();

            // Get the controller for the SearchView and pass the current user
            SearchViewController searchViewController = loader.getController();
            searchViewController.initialize(currentUser);

            // Set up the scene and stage for the search view
            Stage searchStage = new Stage();
            searchStage.setTitle("Search Photos");
            searchStage.setScene(new Scene(root));
            searchStage.show();

            // Close the current UserView window
            Stage currentStage = (Stage) albumsTableView.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error opening search view: " + e.getMessage());
        }
    }

    // Additional methods as needed for the controller

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
