package app;

import model.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for the Photos application.
 * This class initializes and starts the JavaFX application, sets up the primary stage, and initializes
 * default data for the application, including stock and admin users.
 * @author ZB
 */
public class Photos extends Application {

        /**
     * Starts the JavaFX application and sets up the primary stage with the login view.
     * 
     * @param primaryStage The primary stage for this application, onto which
     *                     the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the login view using the FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            // Load the root node from the FXML file (in this case, a BorderPane)
            Parent root = loader.load();
            // Set up the scene with the root node and add it to the stage
            Scene scene = new Scene(root);

            // Configure the primary stage and show it
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        /**
     * The main entry point for the application.
     * Initializes the application data, including stock and admin users, and launches the JavaFX application.
     * 
     * @param args The command line arguments passed to the application. Not used in this application.
     */
    public static void main(String[] args) {
        // Initialize the application data (users, albums, photos, etc.) and stock user and admin user
        DataManager.initializeData();
        // Initialize the stock user if it doesn't already exist
        initializeStockUser();
        // Initialize the admin user if it doesn't already exist
        initializeAdminUser();
        launch(args); // Launch the JavaFX application
    }

    /**
     * Initializes the stock user with a default album and some stock photos if it doesn't already exist.
     */
    private static void initializeStockUser() {
        User stockUser = DataManager.loadUserData("stock");
        if (stockUser == null) {
            // "stock" user doesn't exist, so initialize it
            stockUser = new User("stock");
            Album stockAlbum = new Album("stock");
            stockUser.createAlbum(stockAlbum);
            // Load stock photos into the album
            loadStockPhotos(stockAlbum);
            // Save the newly initialized "stock" user
            DataManager.saveUserData(stockUser);
        }
    }
    
    /**
     * Initializes the admin user if it doesn't already exist.
     */
    private static void initializeAdminUser() {
        User adminUser = DataManager.loadUserData("admin");
        if (adminUser == null) {
            // "admin" user doesn't exist, so initialize it
            adminUser = new User("admin");
            // Save the newly initialized "admin" user
            DataManager.saveUserData(adminUser);
        }
    }

    /**
     * Loads stock photos into the specified album.
     * 
     * @param stockAlbum The album to load stock photos into.
     */
    private static void loadStockPhotos(Album stockAlbum) {

        Photo photo1 = new Photo("data/Avatar.jpeg");
        photo1.setCaption("Avatar");
        photo1.addTag(new Tag("movie", "Avatar"));

        Photo photo2 = new Photo("data/CSNY.jpeg");
        photo2.setCaption("CSNY");
        photo2.addTag(new Tag("music", "CSNY"));

        Photo photo3 = new Photo("data/Joyce.jpeg");
        photo3.setCaption("Joyce");
        photo3.addTag(new Tag("literature", "Joyce"));

        Photo photo4 = new Photo("data/Oppenheimer.jpeg");
        photo4.setCaption("Oppenheimer");
        photo4.addTag(new Tag("movie", "Oppenheimer"));

        Photo photo5 = new Photo("data/Zelda.jpeg");
        photo5.setCaption("Zelda");
        photo5.addTag(new Tag("video game", "Zelda"));

        stockAlbum.addPhoto(photo1);
        stockAlbum.addPhoto(photo2);
        stockAlbum.addPhoto(photo3);
        stockAlbum.addPhoto(photo4);
        stockAlbum.addPhoto(photo5);
    }
}
