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

    // Other methods for handling user actions in the AlbumView...
}
