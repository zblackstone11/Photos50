package viewController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Album;
import model.DataManager;
import model.Photo;
import model.User;
import model.Tag;

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

            // Save the updated user data
            DataManager.saveUserData(currentUser);
        }
    }

    @FXML
    private void handleRemovePhoto() {
        // Get the selected photo
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();

        // Check if a photo is selected
        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo to remove.");
            return;
        }

        // Confirm the action with the user
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove the selected photo?", ButtonType.YES, ButtonType.NO);
        confirmAlert.setHeaderText("Confirm Photo Removal");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            // Remove the photo from the album
            selectedAlbum.removePhoto(selectedPhoto);

            // Update the photo list view
            photoListView.getItems().remove(selectedPhoto);

            // Save the updated user data
            DataManager.saveUserData(currentUser);
        }
    }

    @FXML
    private void handleCaptionPhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();

        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo to caption.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedPhoto.getCaption());
        dialog.setTitle("Caption Photo");
        dialog.setHeaderText("Enter a new caption for the selected photo:");
        dialog.setContentText("Caption:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(caption -> {
            // Set the new caption and update the ListView
            selectedPhoto.setCaption(caption);
            photoListView.refresh();

            // Save the updated user data
            DataManager.saveUserData(currentUser);
        });
    }

    @FXML
    private void handleDisplayPhoto() {
        // Logic for displaying a selected photo in a larger view or separate window
    }

    @FXML
    private void handleAddTag() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
    
        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo to add a tag.");
            return;
        }
    
        // Get tag type and multiplicity
        Pair<String, Integer> tagTypeAndMultiplicity = showTagTypeDialog();
        if (tagTypeAndMultiplicity == null) {
            return; // User canceled or entered an invalid tag type
        }
    
        String tagType = tagTypeAndMultiplicity.getKey();
        Integer multiplicity = tagTypeAndMultiplicity.getValue();
    
        // Now check if the tag type supports multiple values
        if (multiplicity > 1 || !selectedPhoto.hasTagOfType(tagType)) {
            // Get tag value
            String tagValue = showTagValueDialog(tagType);
            if (tagValue == null || tagValue.isBlank()) {
                return; // User canceled or entered an invalid tag value
            }
    
            // Add tag to the photo
            Tag newTag = new Tag(tagType, tagValue);
            if (selectedPhoto.addTag(newTag)) {
                photoListView.refresh(); // Update the ListView to show the new tag
                DataManager.saveUserData(currentUser); // Save changes
            } else {
                showErrorDialog("This tag already exists for the selected photo.");
            }
        } else {
            showErrorDialog("This tag type only supports a single value, which already exists for this photo.");
        }
    }    
    
    private Pair<String, Integer> showTagTypeDialog() {
        Map<String, Integer> tagTypes = currentUser.getTagTypes();
        List<String> choices = new ArrayList<>(tagTypes.keySet());
    
        // First, ask if the user wants to add a new tag type
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Add New Tag Type");
        alert.setHeaderText("Do you want to add a new tag type?");
        alert.setContentText("Choose 'OK' to add a new tag type or 'Cancel' to select from existing ones.");
    
        Optional<ButtonType> response = alert.showAndWait();
    
        if (response.isPresent() && response.get() == ButtonType.OK) {
            // User chose to add a new tag type
            TextInputDialog newTypeDialog = new TextInputDialog();
            newTypeDialog.setTitle("New Tag Type");
            newTypeDialog.setHeaderText("Enter the new tag type:");
            newTypeDialog.setContentText("Tag type:");
    
            Optional<String> newTypeResult = newTypeDialog.showAndWait();
            if (newTypeResult.isPresent()) {
                String newType = newTypeResult.get();
                Integer multiplicity = askForMultiplicity(newType);
                if (multiplicity != null) {
                    // Add the new type and its multiplicity to the user's tag types
                    tagTypes.put(newType, multiplicity);
                    currentUser.setTagTypes(tagTypes);
                    DataManager.saveUserData(currentUser);
                    return new Pair<>(newType, multiplicity);
                }
            }
        } else {
            // User chose to select from existing tag types
            ChoiceDialog<String> dialog = new ChoiceDialog<>(null, choices);
            dialog.setTitle("Select Tag Type");
            dialog.setHeaderText("Select a tag type:");
            dialog.setContentText("Tag type:");
    
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String selectedType = result.get();
                return new Pair<>(selectedType, tagTypes.get(selectedType));
            }
        }
    
        return null; // User cancelled or did not enter valid input
    }
    
    private Integer askForMultiplicity(String tagType) {
        TextInputDialog multiplicityDialog = new TextInputDialog("1");
        multiplicityDialog.setTitle("Tag Multiplicity");
        multiplicityDialog.setHeaderText("Enter multiplicity for '" + tagType + "':");
        multiplicityDialog.setContentText("Multiplicity:");
    
        Optional<String> result = multiplicityDialog.showAndWait();
        if (result.isPresent()) {
            try {
                return Integer.parseInt(result.get());
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid multiplicity. Please enter a number.");
                return null;
            }
        }
        return null; // User canceled
    }    

    private String showTagValueDialog(String tagType) {
        // Create the dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tag Value");
        dialog.setHeaderText("Enter the value for the tag type: " + tagType);
        dialog.setContentText("Tag value:");
    
        // Show the dialog and capture the result
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null); // Return the entered tag value or null if canceled
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
