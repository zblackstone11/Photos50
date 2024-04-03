package model;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Class to manage data for the Photos application.
 * This class is responsible for saving and loading user data, albums, and photos.
 * It uses serialization to save and load data from disk.
 */
public class DataManager {

    /** 
     * The directory where data files are stored as per the instructions.
     * This directory will be created if it does not exist.
     * The user map file is stored in this directory. Contains the map of usernames to user objects.
     */
    private static final String DATA_DIR = "data";

    /** 
     * The file name for the user map file.
     * This file stores the map of usernames to user objects.
     * .dat extension is used to indicate a serialized file.
     */
    private static final String USER_MAP_FILE = "users.dat";

    /** 
     * The in-memory map of users.
     * This map stores the usernames as keys and the corresponding user objects as values.
     */
    private static Map<String, User> usersMap = new HashMap<>(); // In-memory storage of users from string to user object

    /**
     * Initializes the user data by loading the user map from disk.
     */
    public static void initializeData() {
        loadUsersMap(); // Load the users map from disk
    }

    /**
     * Saves user data to disk.
     * @param user The user to save.
     */
    public static void saveUserData(User user) {
        checkAndCreateDataDir(); // Ensure the data directory exists already or create it
        usersMap.put(user.getUsername(), user); // Update the in-memory map
        saveUsersMap(); // Save the user map to disk
    }

    /**
     * Loads user data from disk.
     * @param username The username of the user to load.
     * @return The user object loaded from disk.
     */
    public static User loadUserData(String username) {
        return usersMap.get(username); // Retrieve the user from the in-memory map using the username as the key
    }
    
    /**
     * Returns the in-memory map of users.
     * @return The map of users.
     */
    public static Map<String, User> getUsersMap() {
        return usersMap;
    }

    /**
     * Saves the user map to disk.
     */
    public static void saveUsersMap() {
        // Try-with-resources to automatically close the stream
        // ObjectOutputStream is used to write objects to a file
        // FileOutputStream is used to write bytes to a file
        // The file path is obtained using the helper method getUsersMapFilePath()
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getUsersMapFilePath()))) {
            oos.writeObject(usersMap);
        } catch (IOException e) {
            // Handle the exception, e.g., log it or show an error message
            System.err.println("Failed to save user data: " + e.getMessage());
        }
    }

    /**
     * Loads the user map from disk.
     */
    private static void loadUsersMap() {
        File file = new File(getUsersMapFilePath()); // Get the file path for the user map file
        if (file.exists()) {
            // Try-with-resources to automatically close the stream
            // ObjectInputStream is used to read objects from a file
            // FileInputStream is used to read bytes from a file
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                // Read the object from the file, this is the map of usernames to user objects
                Object readObject = ois.readObject();
                if (readObject instanceof Map<?, ?>) { // Check if the object is a map of usernames to user objects
                    // Unsafe cast, but we're simplifying under the assumption that the file's data is correct.
                    usersMap = (Map<String, User>) readObject;
                } else {
                    System.err.println("Data format mismatch: Expected a Map.");
                }
            } catch (IOException e) {
                System.err.println("Error reading users map: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found while reading users map: " + e.getMessage());
            }
        }
    }    

    /**
     * Returns the file path for the user map file.
     * @return The file path for the user map file.
     */
    private static String getUsersMapFilePath() {
        // The file path is the data directory path + the user map file name
        return DATA_DIR + File.separator + USER_MAP_FILE;
    }

    /**
     * Checks if the data directory exists and creates it if it does not.
     */
    private static void checkAndCreateDataDir() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }
}
