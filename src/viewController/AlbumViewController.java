package viewController;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Album;
import model.DataManager;
import model.Photo;
import model.User;

public class AlbumViewController {

    private Album selectedAlbum;
    private User currentUser;

    @FXML
    private ListView<Photo> photoListView;

    public void setAlbumAndUser(Album album, User user) {
        this.selectedAlbum = album;
        this.currentUser = user;
        
        // Initialize the view with album details
        initializeAlbumView();
    }

    private void initializeAlbumView() {
        // Set the custom cell factory for the photo list view
        photoListView.setCellFactory(listView -> new PhotoListCell());
        
        // Populate the photo list view with photos from the selected album
        photoListView.getItems().setAll(selectedAlbum.getPhotos());
        
        // Other initializations as needed...
    }

    // Custom ListCell class for displaying photos with thumbnails and captions
    static class PhotoListCell extends ListCell<Photo> {
        private HBox content;
        private Text caption;
        private ImageView image;

        public PhotoListCell() {
            super();
            caption = new Text();
            image = new ImageView();
            content = new HBox(image, caption);
            content.setSpacing(10); // Set spacing between image and caption
        }

        @Override
        protected void updateItem(Photo item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null && !empty) {
                caption.setText(item.getCaption()); // Set the caption text
                // Set the thumbnail image (you need to implement a method to get the thumbnail in your Photo class)
                image.setImage(item.getThumbnail());
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }

    @FXML
    private void handleAddPhoto() {
        // Create a FileChooser to let the user select a photo
        FileChooser fileChooser = new FileChooser();

        // Set the title for the FileChooser
        fileChooser.setTitle("Select Photo");

        // Set the initial directory, e.g., user's home or any specific path
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Filter to only show image files
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png")
        );

        // Show open file dialog and get the selected file
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            // Create a new Photo object from the selected file
            Photo newPhoto = new Photo(file.getAbsolutePath());

            // Add the photo to the current album
            selectedAlbum.addPhoto(newPhoto);

            // Update the photo list view to include the new photo
            photoListView.getItems().setAll(selectedAlbum.getPhotos());
        }
    }

    @FXML
    private void handleRemovePhoto() {
        // Logic for removing a selected photo from the current album
    }

    @FXML
    private void handleCaptionPhoto() {
        // Logic for captioning or recaptioning a selected photo
    }

    @FXML
    private void handleDisplayPhoto() {
        // Logic for displaying a selected photo in a larger view or separate window
    }

    @FXML
    private void handleAddTag() {
        // Logic for adding a tag to a selected photo
    }

    @FXML
    private void handleDeleteTag() {
        // Logic for deleting a tag from a selected photo
    }

    @FXML
    private void handleCopyPhoto() {
        // Logic for copying a selected photo to another album
    }

    @FXML
    private void handleMovePhoto() {
        // Logic for moving a selected photo to another album
    }

    @FXML
    private void handleSlideshow() {
        // Logic for starting a slideshow of the photos in the current album
    }

    @FXML
    private void handleQuit() {
        // Save the current state before quitting
        DataManager.saveUserData(currentUser);

        // Close the current album view window
        Stage currentStage = (Stage) photoListView.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void handleBackToAlbums() {
        // Save the current user's data first
        DataManager.saveUserData(currentUser);

        try {
            // Load the FXML file for the UserView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewController/UserView.fxml"));
            Parent root = loader.load();

            // Get the controller for the UserView and set the current user
            UserViewController userViewController = loader.getController();
            userViewController.setCurrentUser(currentUser);

            // Set up the scene and stage
            Stage stage = (Stage) photoListView.getScene().getWindow();
            stage.setTitle("Your Albums");
            stage.setScene(new Scene(root));

            // Show the UserView window
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException, such as showing an error dialog
            showErrorDialog("Error opening user album view: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        // Save the current state before logging out
        DataManager.saveUserData(currentUser);

        // Close the current album view window
        Stage currentStage = (Stage) photoListView.getScene().getWindow();
        currentStage.close();

        // Open the login window
        openLoginWindow();
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
