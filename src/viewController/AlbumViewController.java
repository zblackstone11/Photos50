package viewController;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import model.Album;
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
        // Logic for adding a photo to the current album
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
    private void handleLogout() {
        // Logic for logging out the current user and returning to the login view
    }

    @FXML
    private void handleQuit() {
        // Logic for quitting the application
    }

    @FXML
    private void handleBackToAlbums() {
        // Logic for returning to the user's album view
    }
    
}
