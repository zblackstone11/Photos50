package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.control.ButtonBar;
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

/**
 * Controller class for the album view.
 * @author ZB SL
 */
public class AlbumViewController {

    /**
     * The selected album for the view.
     */
    private Album selectedAlbum;

    /**
     * The current user for the view.
     */
    private User currentUser;

    /**
     * The ListView for displaying photos in the album.
     */
    @FXML
    private ListView<Photo> photoListView;

    /**
     * Method to set the selected album and current user for the view.
     * This method is called from the UserViewController when the user selects an album.
     * @param album The selected album.
     * @param user The current user.
     */
    public void setAlbumAndUser(Album album, User user) {
        this.selectedAlbum = album;
        this.currentUser = user;
        
        // Initialize the view with album details
        initializeAlbumView();
    }

    /**
     * Method to initialize the album view with the selected album's photos.
     * This method is called when the album view is loaded.
     */
    private void initializeAlbumView() {
        // Set the custom cell factory for the photo list view
        // Works by setting the graphic of the ListCell to a custom layout (HBox with ImageView and Text)
        // The custom layout displays the photo thumbnail and caption
        // Lambda expression to create a new PhotoListCell for each item in the ListView
        photoListView.setCellFactory(listView -> new PhotoListCell());
        
        // Populate the photo list view with photos from the selected album
        photoListView.getItems().setAll(selectedAlbum.getPhotos());
    }

    /**
     * Custom ListCell class for displaying photos in the ListView.
     * This class is used to customize the appearance of each photo in the ListView.
     * Works by setting the graphic of the ListCell to a custom layout (HBox with ImageView and Text).
     */
    static class PhotoListCell extends ListCell<Photo> {

        /**
         * The layout for the custom ListCell.
         * This layout contains an ImageView for the photo thumbnail and a Text component for the caption.
         * The layout is an HBox with the ImageView and Text components.
         */
        private HBox content;

        /**
         * The text component for the photo caption.
         */
        private Text caption;

        /**
         * The image component for the photo thumbnail.
         */
        private ImageView image;

        /**
         * Constructor for the custom ListCell.
         */
        public PhotoListCell() {
            super();
            caption = new Text();
            image = new ImageView();
            content = new HBox(image, caption);
            content.setSpacing(10); // Set spacing between image and caption
        }

        /**
         * Method to update the item in the ListCell.
         * This method is called whenever the item in the cell needs to be updated.
         * @param item The Photo object to display.
         * @param empty A boolean indicating if the cell is empty.
         * If empty is true, the cell is empty and should not display any content.
         * If empty is false, the cell should display the Photo object.
         */
        @Override
        protected void updateItem(Photo item, boolean empty) {
            // Call the super method to update the item in the cell first
            super.updateItem(item, empty);

            if (item != null && !empty) {
                caption.setText(item.getCaption()); // Set the caption text
                // Set the thumbnail image (scaled to 100x100) for the photo in the cell
                image.setImage(item.getThumbnail());
                // Set the content of the cell to the custom layout (HBox with ImageView and Text)
                // This layout displays the photo thumbnail and caption
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * Event handler for the "Add Photo" button.
     * This method is called when the user clicks the "Add Photo" button.
     * It opens a FileChooser dialog to let the user select a photo to add to the album.
     * Only types of image files are allowed (e.g., BMP, GIF, JPG, PNG).
     * The selected photo is added to the album and the photo list view is updated.
     * If the user cancels the dialog or an error occurs, an error dialog is shown.
     */
    @FXML
    private void handleAddPhoto() {
        // Open a FileChooser dialog to select a photo file
        FileChooser fileChooser = new FileChooser();
        // Set the title
        fileChooser.setTitle("Select Photo");
        // Set the initial directory to the user's home directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        // Set the extension filters to allow only image files (BMP, GIF, JPG, JPEG, PNG)
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png"));
        // Show the dialog and wait for the user to select a file
        File file = fileChooser.showOpenDialog(null);
    
        // Check if a file was selected
        if (file != null) {
            // Check if the photo is already in the current album
            for (Photo photo : selectedAlbum.getPhotos()) {
                if (photo.getFilePath().equals(file.getAbsolutePath())) {
                    showErrorDialog("The photo is already in this album.");
                    return;
                }
            }
            // Check if the photo is already in another album for the current user
            boolean photoExistsInOtherAlbum = false;
            for (Album album : currentUser.getAlbums()) {
                for (Photo photo : album.getPhotos()) {
                    if (photo.getFilePath().equals(file.getAbsolutePath()) && !album.equals(selectedAlbum)) {
                        Alert warningAlert = new Alert(Alert.AlertType.WARNING, "This photo is already in another of your album(s). Adding it here will overwrite the old tags and caption. Continue?", ButtonType.OK, ButtonType.CANCEL);
                        warningAlert.setHeaderText("Photo Already Exists");
                        Optional<ButtonType> warningResult = warningAlert.showAndWait();
                        if (warningResult.isPresent() && warningResult.get() == ButtonType.CANCEL) {
                            return;
                        }
                        photoExistsInOtherAlbum = true;
                        break;
                    }
                }
                if (photoExistsInOtherAlbum) break;
            }
    
            // If the photo does not exist in any other album or the user chose to proceed
            Photo newPhoto = new Photo(file.getAbsolutePath());
            selectedAlbum.addPhoto(newPhoto);
            photoListView.getItems().setAll(selectedAlbum.getPhotos());
            DataManager.saveUserData(currentUser);
        }
    }

    /**
     * Event handler for the "Remove Photo" button.
     * This method is called when the user clicks the "Remove Photo" button.
     * It removes the selected photo from the album and updates the photo list view.
     * If no photo is selected or the user cancels the action, an error dialog is shown.
     * If the user confirms the action, the photo is removed from the album.
     * If an error occurs during the removal, an error dialog is shown.
     * If the removal is successful, the updated user data is saved.
     * If the user cancels the action, no changes are made.
     * If the user confirms the action, the photo is removed from the album.
     */
    @FXML
    private void handleRemovePhoto() {
        // Get the selected photo using the ListView selection model
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();

        // Check if a photo is selected
        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo to remove.");
            return;
        }

        // Confirm the action with the user
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove the selected photo?", ButtonType.YES, ButtonType.NO);
        confirmAlert.setHeaderText("Confirm Photo Removal");
        // Show and wait for the user's response (Yes/No) to the confirmation dialog box. Store the result in an Optional<ButtonType> object.
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

    /**
     * Event handler for the "Caption Photo" button.
     * This method is called when the user clicks the "Caption Photo" button.
     * It allows the user to enter a new caption for the selected photo.
     * If no photo is selected, an error dialog is shown.
     * If the user cancels the action, no changes are made.
     * If the user enters a new caption, the photo's caption is updated and the photo list view is refreshed.
     * If an error occurs during the update, an error dialog is shown.
     * If the update is successful, the updated user data is saved.
     */
    @FXML
    private void handleCaptionPhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();

        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo to caption.");
            return;
        }

        // Create a TextInputDialog to let the user enter a new caption for the photo
        TextInputDialog dialog = new TextInputDialog(selectedPhoto.getCaption());
        dialog.setTitle("Caption Photo");
        dialog.setHeaderText("Enter a new caption for the selected photo:");
        dialog.setContentText("Caption:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(caption -> {
            // Set the new caption and update the ListView using a Consumer block lambda, we don't need to return anything
            selectedPhoto.setCaption(caption);
            photoListView.refresh();

            // Save the updated user data
            DataManager.saveUserData(currentUser);
        });
    }

    /**
     * Event handler for the "Display Photo" button.
     * This method is called when the user clicks the "Display Photo" button.
     * It displays the selected photo in a new window with additional details like caption, date, and tags.
     * If no photo is selected, an error dialog is shown.
     * If an error occurs while loading the photo, an error dialog is shown.
     */
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
        VBox layout = new VBox(10); // Spacing between nodes in the layout (10 pixels)
        layout.setAlignment(Pos.CENTER);

        // Create an ImageView for the photo
        Image image;
        try {
            // Load the image from the file path using FileInputStream and create an ImageView
            image = new Image(new FileInputStream(selectedPhoto.getFilePath()));
        } catch (FileNotFoundException e) {
            showErrorDialog("Error loading photo: " + e.getMessage());
            return;
        }
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true); // Preserve the aspect ratio of the image
        imageView.setFitHeight(600); // Might be better to use a ScrollPane for large images but this will do for now

        // Create labels for the caption and date-time
        Label captionLabel = new Label("Caption: " + selectedPhoto.getCaption());
        Label dateLabel = new Label("Date: " + selectedPhoto.getDate().toString());

        // Create a label or a list for tags
        // Map each tag to a string representation of the tag (e.g., "TagType: TagValue") using a lambda expression and collect them into a single string
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

    /**
     * Event handler for the "Add Tag" button.
     * This method is called when the user clicks the "Add Tag" button.
     * It allows the user to add a new tag to the selected photo.
     * If no photo is selected, an error dialog is shown.
     * If the user cancels the action or enters an invalid tag type or value, no changes are made.
     * If the tag type already exists for the photo or the tag type only supports a single value,
     * an error dialog is shown.
     * If the tag is successfully added, the photo list view is updated and the user data is saved.
     */
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
        // Either the tag type does not exist for the photo or it supports multiple values
        if (multiplicity > 1 || !selectedPhoto.hasTagOfType(tagType)) {
            // Get tag value from user input using a dialog method
            String tagValue = showTagValueDialog(tagType);
            if (tagValue == null || tagValue.isBlank()) {
                return; // User canceled or entered an invalid tag value
            }
    
            // Add tag to the photo
            Tag newTag = new Tag(tagType, tagValue);
            if (selectedPhoto.addTag(newTag)) { // Method returns true if tag was added successfully (no duplicates)
                photoListView.refresh(); // Update the ListView to show the new tag
                DataManager.saveUserData(currentUser); // Save changes
                // Sync photo updates across all albums defensively
                synchronizePhotoUpdates();
            } else {
                showErrorDialog("This tag already exists for the selected photo.");
            }
        } else { // From higher up in the method
            showErrorDialog("This tag type only supports a single value, which already exists for this photo.");
        }
    }    
    
    /**
     * Method to show a dialog for selecting a tag type.
     * The dialog allows the user to choose from existing tag types or add a new one.
     * If the user chooses to add a new tag type, they are prompted to enter the new type and multiplicity.
     * If the user cancels or enters invalid input, null is returned.
     * If the user selects an existing tag type, the tag type and multiplicity are returned.
     * @return A Pair containing the tag type and its multiplicity, or null if the user cancels or enters invalid input.
     * @param String tagType The tag type entered by the user.
     * @param Integer multiplicity The multiplicity entered by the user.
     */
    private Pair<String, Integer> showTagTypeDialog() {
        Map<String, Integer> tagTypes = currentUser.getTagTypes(); // Get the user's tag types and multiplicity as a map
        List<String> choices = new ArrayList<>(tagTypes.keySet()); // Get the tag types as a list of choices

        // First, ask if the user wants to add a new tag type
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Add New Tag Type");
        alert.setHeaderText("Do you want to add a new tag type? 'Yes' to create a new tag type, 'No' to select from existing ones.");

        // Set custom buttons for the alert dialog so that we can distinguish between Yes and No
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent() && response.get() == yesButton) {
            // User chose to add a new tag type
            TextInputDialog newTypeDialog = new TextInputDialog();
            newTypeDialog.setTitle("New Tag Type");
            newTypeDialog.setHeaderText("Enter the new tag type:");
            newTypeDialog.setContentText("Tag type:");

            Optional<String> newTypeResult = newTypeDialog.showAndWait();
            if (newTypeResult.isPresent()) {
                String newType = newTypeResult.get().trim();
                // Check if the new tag type already exists in a case-insensitive manner
                boolean typeExists = tagTypes.keySet().stream().anyMatch(existingType -> existingType.equalsIgnoreCase(newType));
                if (typeExists) {
                    showErrorDialog("A tag type with this name already exists. Please enter a different name.");
                    return null; // Prevent adding the new tag type
                }
                Integer multiplicity = askForMultiplicity(newType); // Another method below, asks for multiplicity
                if (multiplicity != null) {
                    // Add the new type and its multiplicity to the user's tag types
                    tagTypes.put(newType, multiplicity);
                    // Update the user's tag types and save the data
                    currentUser.setTagTypes(tagTypes);
                    DataManager.saveUserData(currentUser);
                    return new Pair<>(newType, multiplicity);
                }
            }
        } else if (response.isPresent() && response.get() == noButton) {
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
        return null; // User cancelled or did not enter valid input as in tag was already in the list
    }
    
    /**
     * Method to ask the user for the multiplicity of a new tag type.
     * The user is prompted to enter the multiplicity as an integer.
     * If the user cancels or enters invalid input, null is returned.
     * @param tagType The tag type for which the user is entering the multiplicity.
     * @return The multiplicity entered by the user, or null if the user cancels or enters invalid input.
     */
    private Integer askForMultiplicity(String tagType) {
        // Create a dialog for entering the multiplicity
        TextInputDialog multiplicityDialog = new TextInputDialog("1");
        multiplicityDialog.setTitle("Tag Multiplicity");
        // SL: I was having trouble finding out what you ment by multiplicity of a tag type, could use a short explation on this line 
        multiplicityDialog.setHeaderText("Enter multiplicity for '" + tagType + "':");
        multiplicityDialog.setContentText("Multiplicity:");
    
        Optional<String> result = multiplicityDialog.showAndWait();
        if (result.isPresent()) {
            try {
                return Integer.parseInt(result.get());
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid multiplicity. Please enter an integer.");
                return null;
            }
        }
        return null; // User canceled
    }    

    /**
     * Method to show a dialog for entering a tag value.
     * The dialog prompts the user to enter a value for the given tag type.
     * If the user cancels or enters an empty value, null is returned.
     * If the user enters a valid value, the value is returned.
     * @param tagType The tag type for which the user is entering the value.
     * @return The tag value entered by the user, or null if the user cancels or enters an empty value.
     */
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

    /**
     * Event handler for the "Delete Tag" button.
     * This method is called when the user clicks the "Delete Tag" button.
     * It allows the user to delete a tag from the selected photo.
     * If no photo is selected or the photo has no tags, an error dialog is shown.
     * If the user cancels the action, no changes are made.
     * If the user selects a tag to delete, the tag is removed from the photo and the photo list view is updated.
     * If an error occurs during the deletion, an error dialog is shown.
     * If the deletion is successful, the updated user data is saved.
     */
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
        // Convert tag list to a list of string descriptions
        List<String> tagDescriptions = new ArrayList<>();
        for (Tag tag : tags) {
            tagDescriptions.add(tag.getTagName() + ": " + tag.getTagValue());
        }

        // Create and show choice dialog for tag deletion
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, tagDescriptions);
        dialog.setTitle("Delete Tag");
        dialog.setHeaderText("Select a tag to delete:");
        dialog.setContentText("Tags:");
        Optional<String> result = dialog.showAndWait();

        // Process the result
        if (result.isPresent()) {
            String selectedTagDescription = result.get();
            for (Tag tag : tags) {
                if ((tag.getTagName() + ": " + tag.getTagValue()).equals(selectedTagDescription)) {
                    selectedPhoto.deleteTag(tag);
                    photoListView.refresh(); // Update ListView
                    DataManager.saveUserData(currentUser); // Save changes
                    // Sync photo updates across all albums
                    synchronizePhotoUpdates();
                    break;
                }
            }
        }
    }

    /**
     * Event handler for the "Copy Photo" button.
     * This method is called when the user clicks the "Copy Photo" button.
     * It allows the user to copy the selected photo to another album.
     * If no photo is selected, an error dialog is shown.
     * If the user cancels the action or there are no other albums to copy to, an error dialog is shown.
     * If the user selects an album to copy the photo to, the photo is copied to the destination album.
     * If the destination album already contains the photo, an error dialog is shown.
     * If the photo is successfully copied, a confirmation dialog is shown and the user data is saved.
     */
    @FXML
    private void handleCopyPhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
    
        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo to copy.");
            return;
        }
    
        List<String> choices = new ArrayList<>();
        for (Album album : currentUser.getAlbums()) {
            if (!album.equals(selectedAlbum)) { // Exclude the current album from the choices to copy to
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
    
            Photo photoCopy = new Photo(selectedPhoto.getFilePath()); // Create a new Photo object with the same file path
            photoCopy.setCaption(selectedPhoto.getCaption()); // Copy the caption using the getCaption method
            photoCopy.setTags(new ArrayList<>(selectedPhoto.getTags())); // Copy the tags using a new ArrayList and the getTags method
            destinationAlbum.addPhoto(photoCopy);
    
            DataManager.saveUserData(currentUser);
            showConfirmationDialog("Photo copied successfully to " + selectedAlbumName + ".");
        }
    }
    
    /**
     * Method to show a confirmation dialog with the given message.
     * @param message The message to display in the dialog.
     */
    private void showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Event handler for the "Move Photo" button.
     * This method is called when the user clicks the "Move Photo" button.
     * It allows the user to move the selected photo to another album.
     * If no photo is selected, an error dialog is shown.
     * If there are no other albums to move to, an error dialog is shown.
     * If the user cancels the action, no changes are made.
     * If the user selects an album to move the photo to, the photo is moved to the destination album.
     * If the destination album already contains the photo, an error dialog is shown.
     * If the photo is successfully moved, a confirmation dialog is shown and the user data is saved.
     */
    @FXML
    private void handleMovePhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();

        if (selectedPhoto == null) {
            showErrorDialog("Please select a photo to move.");
            return;
        }

        // Get the list of album names available to move the photo to
        List<String> choices = new ArrayList<>();
        for (Album album : currentUser.getAlbums()) {
            if (!album.equals(selectedAlbum)) { // Exclude the current album from the choices
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

            // Else move the photo to the destination album
            destinationAlbum.addPhoto(selectedPhoto);
            // Remove the photo from the current album
            selectedAlbum.removePhoto(selectedPhoto);

            DataManager.saveUserData(currentUser); // Only move from/to same user
            photoListView.getItems().remove(selectedPhoto); // Update the photo list view
            showConfirmationDialog("Photo moved successfully to " + selectedAlbumName + ".");
        }
    }

    /**
     * Event handler for the "Slideshow" button.
     * This method is called when the user clicks the "Slideshow" button.
     * It opens a new window to display a slideshow of the photos in the album.
     * If there are no photos in the album, an error dialog is shown.
     */
    @FXML
    private void handleSlideshow() {
        if (selectedAlbum.getPhotos().isEmpty()) {
            showErrorDialog("There are no photos in this album.");
            return;
        }

        // Create a new stage for the slideshow
        Stage slideshowStage = new Stage();
        slideshowStage.setTitle("Slideshow");

        // Create a BorderPane layout for the slideshow
        BorderPane borderPane = new BorderPane();
        // ImageView for displaying the photos
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(600); // Adjust size as needed, might be better to use a ScrollPane for large images
        borderPane.setCenter(imageView);

        // HBox for navigation buttons
        HBox navigationBox = new HBox();
        navigationBox.setAlignment(Pos.CENTER);
        navigationBox.setSpacing(10);
        Button prevButton = new Button("Previous");
        Button nextButton = new Button("Next");
        // Add the navigation buttons to the bottom of the BorderPane
        navigationBox.getChildren().addAll(prevButton, nextButton);
        // Set the HBox with the navigation buttons at the bottom of the BorderPane
        borderPane.setBottom(navigationBox);

        // Initial photo display
        // The  = {0} is a way to use a mutable integer in a lambda expression
        int[] photoIndex = {0};
        try {
            imageView.setImage(new Image(new FileInputStream(selectedAlbum.getPhotos().get(photoIndex[0]).getFilePath())));
        } catch (FileNotFoundException e) {
            showErrorDialog(e.getMessage());  
        }
        
        // Event handlers for the previous button
        // For the event e, if the photoIndex is greater than 0, decrement the index and load the previous photo
        prevButton.setOnAction(e -> {
            if (photoIndex[0] > 0) {
                // There is a photo before the current one
                photoIndex[0]--;
                try { // Try to load the image from the file path
                    imageView.setImage(new Image(new FileInputStream(selectedAlbum.getPhotos().get(photoIndex[0]).getFilePath())));
                } catch (FileNotFoundException ex) {
                    showErrorDialog(ex.getMessage());
                }
            }
        });

        // Event handler for the next button
        // For the event e, if the photoIndex is less than the number of photos - 1, increment the index and load the next photo
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

    /**
     * Event handler for the "Quit" button.
     * This method is called when the user clicks the "Quit" button.
     * It saves the current user data and closes the album view window.
     */
    @FXML
    private void handleQuit() {
        // Synchronize photo updates across all albums before quitting
        synchronizePhotoUpdates();

        // Save the current state before quitting
        DataManager.saveUserData(currentUser);

        // Close the current album view window
        Stage currentStage = (Stage) photoListView.getScene().getWindow();
        currentStage.close();
    }

    /**
     * Event handler for the "Back to Albums" button.
     * This method is called when the user clicks the "Back to Albums" button.
     * It saves the current user data and opens the UserView window.
     */
    // not too important but the UserView is a diffrent sized window from this button then when logging in, once you notice cant un-notice. 
    @FXML
    private void handleBackToAlbums() {

        // Synchronize photo updates across all albums before going back to the albums view
        synchronizePhotoUpdates();

        // Retrieve the map of users
        Map<String, User> usersMap = DataManager.getUsersMap();

        // Iterate over the map and save each user's data
        for (User user : usersMap.values()) {
            DataManager.saveUserData(user);
        }

        try {
            // Load the FXML file for the UserView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserView.fxml"));
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

    /**
     * Event handler for the "Logout" button.
     * This method is called when the user clicks the "Logout" button.
     * It saves the current user data, closes the album view window, and opens the login window.
     */
    @FXML
    private void handleLogout() {
        // Synchronize photo updates across all albums before logging out
        synchronizePhotoUpdates();

        // Save the current state before logging out
        DataManager.saveUserData(currentUser);

        // Close the current album view window
        Stage currentStage = (Stage) photoListView.getScene().getWindow();
        currentStage.close();

        // Open the login window
        openLoginWindow();
    }

    /**
     * Method to open the login window.
     * This method is called when the user logs out.
     */
    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
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

    /**
     * Method to show an error dialog with the given message.
     * @param message The error message to display in the dialog.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    } 
    
    /**
     * Method to synchronize photo updates across all albums.
     * This method is called when the user logs out, quits, or goes back to the user view from the album view.
     * It updates the captions and tags of photos in other albums when they are changed in the current album.
     */
    private void synchronizePhotoUpdates() {
        for (Photo photo : selectedAlbum.getPhotos()) {
            String currentPhotoPath = photo.getFilePath();
            for (Album album : currentUser.getAlbums()) {
                if (!album.equals(selectedAlbum)) { // Skip the current album
                    for (Photo otherPhoto : album.getPhotos()) {
                        if (otherPhoto.getFilePath().equals(currentPhotoPath)) {
                            // Found a matching photo in another album, update its caption and tags
                            otherPhoto.setCaption(photo.getCaption());
                            otherPhoto.setTags(new ArrayList<>(photo.getTags())); // Copy the tags
                        }
                    }
                }
            }
        }
        DataManager.saveUserData(currentUser); // Save changes to user data
    }
}
