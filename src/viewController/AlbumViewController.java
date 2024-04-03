package viewController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();

        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo to display.");
            return;
        }

        // Create a new stage for the photo display
        Stage photoStage = new Stage();
        photoStage.setTitle("Photo Display");

        // Use VBox for the layout
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        // Create an ImageView for the photo
        Image image;
        try {
            image = new Image(new FileInputStream(selectedPhoto.getFilePath()));
        } catch (FileNotFoundException e) {
            showErrorDialog("Error loading photo: " + e.getMessage());
            return;
        }
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(600); // Might be better to use a ScrollPane for large images

        // Create labels for the caption and date-time
        Label captionLabel = new Label("Caption: " + selectedPhoto.getCaption());
        Label dateLabel = new Label("Date: " + selectedPhoto.getDate().toString());

        // Create a label or a list for tags
        String tagString = selectedPhoto.getTags().stream()
                                        .map(tag -> tag.getTagName() + ": " + tag.getTagValue())
                                        .collect(Collectors.joining(", "));
        Label tagsLabel = new Label("Tags: " + tagString);

        // Add everything to the layout
        layout.getChildren().addAll(imageView, captionLabel, dateLabel, tagsLabel);

        // Set the scene and show the stage
        Scene scene = new Scene(layout);
        photoStage.setScene(scene);
        photoStage.show();
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
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();

        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo first.");
            return;
        }

        List<Tag> tags = selectedPhoto.getTags();
        if (tags.isEmpty()) {
            showErrorDialog("This photo has no tags to delete.");
            return;
        }

        List<String> tagDescriptions = tags.stream()
                                            .map(tag -> tag.getTagName() + ": " + tag.getTagValue())
                                            .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, tagDescriptions);
        dialog.setTitle("Delete Tag");
        dialog.setHeaderText("Select a tag to delete:");
        dialog.setContentText("Tags:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedTagDescription -> {
            Tag tagToDelete = tags.stream()
                                .filter(tag -> (tag.getTagName() + ": " + tag.getTagValue()).equals(selectedTagDescription))
                                .findFirst()
                                .orElse(null);

            if (tagToDelete != null) {
                selectedPhoto.deleteTag(tagToDelete);
                photoListView.refresh(); // Update the ListView to reflect the tag deletion
                DataManager.saveUserData(currentUser); // Save the changes
            }
        });
    }

    @FXML
    private void handleCopyPhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
    
        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo to copy.");
            return;
        }
    
        List<String> choices = new ArrayList<>();
        for (Album album : currentUser.getAlbums()) {
            if (!album.equals(selectedAlbum)) {
                choices.add(album.getName());
            }
        }
    
        if (choices.isEmpty()) {
            showErrorDialog("No other albums to copy to.");
            return;
        }
    
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Copy Photo");
        dialog.setHeaderText("Select an album to copy the photo to:");
        dialog.setContentText("Album:");
    
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String selectedAlbumName = result.get();
            Album destinationAlbum = currentUser.getAlbumByName(selectedAlbumName);
            
            if (destinationAlbum == null) {
                showErrorDialog("Invalid album selected.");
                return;
            }
    
            if (destinationAlbum.getPhotos().contains(selectedPhoto)) {
                showErrorDialog("The selected album already contains this photo.");
                return;
            }
    
            Photo photoCopy = new Photo(selectedPhoto.getFilePath());
            photoCopy.setCaption(selectedPhoto.getCaption());
            photoCopy.setTags(new ArrayList<>(selectedPhoto.getTags())); // Assuming you have a suitable constructor or method
            destinationAlbum.addPhoto(photoCopy);
    
            DataManager.saveUserData(currentUser);
            showConfirmationDialog("Photo copied successfully to " + selectedAlbumName + ".");
        }
    }
    
    private void showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleMovePhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();

        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo to move.");
            return;
        }

        List<String> choices = new ArrayList<>();
        for (Album album : currentUser.getAlbums()) {
            if (!album.equals(selectedAlbum)) {
                choices.add(album.getName());
            }
        }

        if (choices.isEmpty()) {
            showErrorDialog("No other albums to move to.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Move Photo");
        dialog.setHeaderText("Select an album to move the photo to:");
        dialog.setContentText("Album:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String selectedAlbumName = result.get();
            Album destinationAlbum = currentUser.getAlbumByName(selectedAlbumName);
            
            if (destinationAlbum == null) {
                showErrorDialog("Invalid album selected.");
                return;
            }

            if (destinationAlbum.getPhotos().contains(selectedPhoto)) {
                showErrorDialog("The selected album already contains this photo.");
                return;
            }

            destinationAlbum.addPhoto(selectedPhoto);
            selectedAlbum.removePhoto(selectedPhoto);

            DataManager.saveUserData(currentUser);
            photoListView.getItems().remove(selectedPhoto);
            showConfirmationDialog("Photo moved successfully to " + selectedAlbumName + ".");
        }
    }

    @FXML
    private void handleSlideshow() {
        if (selectedAlbum.getPhotos().isEmpty()) {
            showErrorDialog("There are no photos in this album.");
            return;
        }

        Stage slideshowStage = new Stage();
        slideshowStage.setTitle("Slideshow");

        BorderPane borderPane = new BorderPane();
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(500); // Adjust size as needed
        borderPane.setCenter(imageView);

        HBox navigationBox = new HBox();
        navigationBox.setAlignment(Pos.CENTER);
        navigationBox.setSpacing(10);
        Button prevButton = new Button("Previous");
        Button nextButton = new Button("Next");
        navigationBox.getChildren().addAll(prevButton, nextButton);
        borderPane.setBottom(navigationBox);

        // Initial photo display
        int[] photoIndex = {0};
        try {
            imageView.setImage(new Image(new FileInputStream(selectedAlbum.getPhotos().get(photoIndex[0]).getFilePath())));
        } catch (FileNotFoundException e) {
            showErrorDialog(e.getMessage());
            
        }

        prevButton.setOnAction(e -> {
            if (photoIndex[0] > 0) {
                photoIndex[0]--;
                try {
                    imageView.setImage(new Image(new FileInputStream(selectedAlbum.getPhotos().get(photoIndex[0]).getFilePath())));
                } catch (FileNotFoundException ex) {
                    showErrorDialog(ex.getMessage());
                }
            }
        });

        nextButton.setOnAction(e -> {
            if (photoIndex[0] < selectedAlbum.getPhotos().size() - 1) {
                photoIndex[0]++;
                try {
                    imageView.setImage(new Image(new FileInputStream(selectedAlbum.getPhotos().get(photoIndex[0]).getFilePath())));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Scene scene = new Scene(borderPane);
        slideshowStage.setScene(scene);
        slideshowStage.show();
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
