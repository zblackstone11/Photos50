package app;

// The main class for the application. This class is responsible for starting the application and initializing the stock user.

import model.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Photos extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewController/LoginView.fxml"));
            Parent root = loader.load();

            // Set up the scene
            Scene scene = new Scene(root);

            // Configure the primary stage
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        // Initialize the application
        DataManager.initializeData();
        initializeStockUser();
        initializeAdminUser();
        launch(args); // Launch the JavaFX application

        // Continue with application startup (showing UI, loading other users, etc.)
    }

    // method to initialize the stock user
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
    
    // method to initialize the admin user
    private static void initializeAdminUser() {
        User adminUser = DataManager.loadUserData("admin");
        if (adminUser == null) {
            // "admin" user doesn't exist, so initialize it
            adminUser = new User("admin");
            // Save the newly initialized "admin" user
            DataManager.saveUserData(adminUser);
        }
    }

    // method to load stock photos into the stock album
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
