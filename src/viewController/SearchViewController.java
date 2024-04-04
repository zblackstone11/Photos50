package viewController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * SearchViewController class for the search view.
 */
public class SearchViewController {

    /**
     * The DatePicker for the start date of the search.
     */
    @FXML
    private DatePicker startDatePicker;

    /**
     * The DatePicker for the end date of the search.
     */
    @FXML
    private DatePicker endDatePicker;

    /**
     * The TextField for the first tag type.
     */
    @FXML
    private TextField tag1TypeField;

    /**
     * The TextField for the first tag value.
     */
    @FXML
    private TextField tag1ValueField;

    /**
     * The TextField for the second tag type.
     */
    @FXML
    private TextField tag2TypeField;

    /**
     * The TextField for the second tag value.
     */
    @FXML
    private TextField tag2ValueField;

    /**
     * The ComboBox for the tag search type.
     */
    @FXML
    private ComboBox<String> tagSearchType;

    /**
     * The ListView to display the search results.
     */
    @FXML
    private ListView<Photo> searchResultsView;

    /**
     * The current user logged in.
     */
    private User currentUser;

    /**
     * Method to initialize the SearchViewController with the current user.
     * This method is called from the UserViewController to pass the current user.
     * @param currentUser The current user logged in.
     */
    public void initialize(User currentUser) {
        this.currentUser = currentUser;
        tagSearchType.setItems(FXCollections.observableArrayList("Single", "Conjunctive", "Disjunctive"));
    }

    /**
     * Method to handle searching by date.
     * This method is called when the user clicks the "Search by Date" button.
     * It reads the start and end dates from the DatePickers and searches for photos in that range.
     * It then displays the search results in the ListView.
     * If no photos are found, it shows an error dialog.
     */
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

        Set<Photo> matchingPhotosSet = new HashSet<>(); // set to avoid duplicates

        for (Album album : currentUser.getAlbums()) {
            for (Photo photo : album.getPhotos()) {
                LocalDateTime photoDate = photo.getDate();
                if ((photoDate.isEqual(startDateTime) || photoDate.isAfter(startDateTime)) &&
                    (photoDate.isEqual(endDateTime) || photoDate.isBefore(endDateTime))) {
                    matchingPhotosSet.add(photo);
                }
            }
        }

        List<Photo> matchingPhotos = new ArrayList<>(matchingPhotosSet);

        if (matchingPhotos.isEmpty()) {
            showErrorDialog("No photos found in the specified date range.");
        } else {
            searchResultsView.getItems().setAll(matchingPhotos);
            // Make sure your ListView is set up to display photos appropriately,
            // possibly by setting a custom cell factory.
        }
    }

    /**
     * Method to handle searching by tag.
     * This method is called when the user clicks the "Search by Tag" button.
     * It reads the tag type, value, and search type from the TextFields and ComboBox.
     * It then searches for photos based on the tag criteria and displays the search results in the ListView.
     * If no photos are found, it shows an error dialog.
     */
    @FXML
    private void handleSearchByTag() {
        String tag1Type = tag1TypeField.getText().trim().toLowerCase();
        String tag1Value = tag1ValueField.getText().trim().toLowerCase();
        String tag2Type = tag2TypeField.getText().trim().toLowerCase();
        String tag2Value = tag2ValueField.getText().trim().toLowerCase();
        String searchType = tagSearchType.getValue();

        // Input validation
        if (searchType == null) {
            showErrorDialog("Please select a search type.");
            return;
        }
        // Input validation for "Single" search type, seems to cover all cases
        if (searchType.equals("Single")) {
            if ((tag1Type.isEmpty() || tag1Value.isEmpty()) && (tag2Type.isEmpty() || tag2Value.isEmpty())) {
                showErrorDialog("Please fill in at least one tag type-value pair for single tag search.");
                return;
            }
            if ((!tag1Type.isEmpty() && tag1Value.isEmpty()) || (!tag2Type.isEmpty() && tag2Value.isEmpty())) {
                showErrorDialog("Both tag type and value must be filled in.");
                return;
            }
            if ((!tag1Type.isEmpty() && !tag1Value.isEmpty()) && (!tag2Type.isEmpty() && !tag2Value.isEmpty())) {
                showErrorDialog("Only one tag type-value pair should be filled in for single tag search.");
                return;
            }
        }

        // Input validation for "Conjunctive" and "Disjunctive" search types
        if (searchType.equals("Conjunctive") || searchType.equals("Disjunctive")) {
            if (tag1Type.isEmpty() || tag1Value.isEmpty() || tag2Type.isEmpty() || tag2Value.isEmpty()) {
                showErrorDialog("All fields must be filled in for conjunctive/disjunctive tag search.");
                return;
            }
        }

        Set<Photo> matchingPhotosSet = new HashSet<>(); // Use a Set to avoid duplicates

        for (Album album : currentUser.getAlbums()) {
            for (Photo photo : album.getPhotos()) {
                boolean matches = false;

                switch (searchType) {
                    case "Single":
                        // Either tag1 or tag2 must match but not both
                        boolean matchesTag1 = matchesTagCriteria(photo, tag1Type, tag1Value);
                        boolean matchesTag2 = matchesTagCriteria(photo, tag2Type, tag2Value);
                        matches = (matchesTag1 && !matchesTag2) || (!matchesTag1 && matchesTag2); // Use logical XOR for single search
                        break;
                    case "Conjunctive":
                        // Both tags must match
                        boolean conMatchesTag1 = matchesTagCriteria(photo, tag1Type, tag1Value);
                        boolean conMatchesTag2 = matchesTagCriteria(photo, tag2Type, tag2Value);
                        matches = conMatchesTag1 && conMatchesTag2; // Use logical AND for conjunctive search
                        break;
                    case "Disjunctive":
                        // Disjunctive search logic...
                        boolean disMatchesTag1 = matchesTagCriteria(photo, tag1Type, tag1Value);
                        boolean disMatchesTag2 = matchesTagCriteria(photo, tag2Type, tag2Value);
                        matches = disMatchesTag1 || disMatchesTag2; // Use logical OR for disjunctive search
                        break;
                }

                if (matches) {
                    matchingPhotosSet.add(photo); // Add photo to the set if it matches the criteria
                }
            }
        }

        List<Photo> matchingPhotos = new ArrayList<>(matchingPhotosSet); // Convert the Set to a List

        // Update searchResultsView with matchingPhotos...
        if (matchingPhotos.isEmpty()) {
            showErrorDialog("No photos found matching the specified tag criteria.");
        } else {
            searchResultsView.getItems().setAll(matchingPhotos);
        }
    }

    /**
     * Method to check if a photo matches the tag criteria.
     * This method is used in the searchByTag method to determine if a photo matches the tag criteria.
     * @param photo The photo to check.
     * @param tagType The tag type to match.
     * @param tagValue The tag value to match.
     * @return True if the photo matches the tag criteria, false otherwise.
     */
    private boolean matchesTagCriteria(Photo photo, String tagType, String tagValue) {
        return photo.getTags().stream()
                    .anyMatch(tag -> tag.getTagName().equalsIgnoreCase(tagType) && tag.getTagValue().equalsIgnoreCase(tagValue));
    }

    /**
     * Method to handle creating an album from the search results.
     * This method is called when the user clicks the "Create Album from Results" button.
     * It prompts the user to enter a name for the new album and creates the album with the search results.
     * If the album name is empty or already exists, it shows an error dialog.
     */
    @FXML
    private void handleCreateAlbumFromResults() {
        // Check if the search results view is empty
        if (searchResultsView.getItems().isEmpty()) {
            showErrorDialog("Cannot create an empty album. Please perform a search first or go back to User View.");
            return;
        }

        // Show dialog to get the new album name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album from Results");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the name for the new album:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String albumName = result.get().trim();
            // Check for empty name
            if (albumName.isEmpty()) {
                showErrorDialog("Album name cannot be empty.");
                return;
            }

            // Check for duplicate name
            if (currentUser.getAlbums().stream().anyMatch(album -> album.getName().equalsIgnoreCase(albumName))) {
                showErrorDialog("An album with this name already exists.");
                return;
            }

            // Create the new album and add the photos from the search results
            Album newAlbum = new Album(albumName);
            for (Photo photo : searchResultsView.getItems()) {
                newAlbum.addPhoto(photo);
            }

            // Add the new album to the user's list
            currentUser.createAlbum(newAlbum);

            // Save changes
            DataManager.saveUserData(currentUser);

            // Show success dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Album Created");
            alert.setHeaderText(null);
            alert.setContentText("Album created successfully. Go back to User View to see the new album.");
            alert.showAndWait();

        } else {
            // User cancelled the dialog
            // Do nothing
        }
    }

    /**
     * Method to handle clearing the search fields and results.
     * This method is called when the user clicks the "Clear" button.
     * It resets the date pickers, tag fields, and search results.
     */
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

    /**
     * Method to handle logging out.
     * This method is called when the user clicks the "Logout" button.
     * It saves the user data and closes the current window, then opens the login window.
     */
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

    /**
     * Method to handle quitting the application.
     * This method is called when the user clicks the "Quit" button.
     * It saves the user data and closes the application.
     */
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

    /**
     * Method to handle returning to the UserView.
     * This method is called when the user clicks the "Back" button.
     * It loads the UserView and sets the current user, then displays the new stage.
     * It closes the current SearchView window.
     */
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

    /**
     * Method to show an error dialog with the specified message.
     * @param message The error message to display.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    } 

    /**
     * Method to open the login window.
     * This method is called when the user logs out.
     * It loads the LoginView FXML file and displays the login window.
     */
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
