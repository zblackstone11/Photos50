package viewController;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import model.Photo;
import model.User;

import java.time.LocalDate;

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
        // Additional initialization tasks
    }

    // Method to handle searching by date range
    @FXML
    private void handleSearchByDate() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        // Implement the search logic and update searchResultsView
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
    }

    // Additional methods for the controller as needed
}
