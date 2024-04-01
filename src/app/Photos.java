package app;

// Just a test class to see if JavaFX is working, will host main function later too though
// The main class that launches the JavaFX application. It sets up the primary stage and loads the initial scene.

//Tested and works 

/*import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Photos extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello, JavaFX'");
        btn.setOnAction(e -> System.out.println("Hello, JavaFX"));

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("JavaFX Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/

import model.*;
import java.io.IOException;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewController/LoginView.fxml")); // Ensure the path is correct
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
        initializeStockUser();
        launch(args); // Launch the JavaFX application

        // Continue with application startup (showing UI, loading other users, etc.)
    }

    private static void initializeStockUser() {
        User stockUser = DataManager.loadUserData("stock");
        if (stockUser == null) {
            // "stock" user doesn't exist, so initialize it
            stockUser = new User("stock");
            Album stockAlbum = new Album("stock");
            stockUser.addAlbum(stockAlbum);

            // Load stock photos into the album
            loadStockPhotos(stockAlbum);

            // Save the newly initialized "stock" user
            try {
                DataManager.saveUserData(stockUser);
            } catch (IOException e) {
                e.printStackTrace(); // replace with showErrorDialog("An error occurred: " + e.getMessage());
            }
        }
    }
    
    

    private static void loadStockPhotos(Album stockAlbum) {
        // Implement loading of stock photos from the data directory into the stockAlbum
        // The stock photos are stored in the data directory
        // Make photo objects and add them to the stockAlbum using stockAlbum.addPhoto(photo)

        Photo photo1 = new Photo("data/Avatar.jpeg");
        photo1.setCaption("Avatar");
        photo1.addTag(new Tag("movie"));

        Photo photo2 = new Photo("data/CSNY.jpeg");
        photo2.setCaption("CSNY");
        photo2.addTag(new Tag("music"));

        Photo photo3 = new Photo("data/Joyce.jpeg");
        photo3.setCaption("Joyce");
        photo3.addTag(new Tag("literature"));

        Photo photo4 = new Photo("data/Oppenheimer.jpeg");
        photo4.setCaption("Oppenheimer");
        photo4.addTag(new Tag("movie"));

        Photo photo5 = new Photo("data/Zelda.jpeg");
        photo5.setCaption("Zelda");
        photo5.addTag(new Tag("video game"));

        stockAlbum.addPhoto(photo1);
        stockAlbum.addPhoto(photo2);
        stockAlbum.addPhoto(photo3);
        stockAlbum.addPhoto(photo4);
        stockAlbum.addPhoto(photo5);
    }
}

