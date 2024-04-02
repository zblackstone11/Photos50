package model;

import java.io.*;
// A class dedicated to handling data persistence, such as loading and saving user data, albums, and photos. 
// This class could utilize serialization to manage user sessions and their corresponding data.
import java.util.Map;
import java.util.HashMap;

public class DataManager {
    // Core responsibilities: saving and loading user data, albums, and photos.

    // Constants
    private static final String DATA_DIR = "data";
    private static final String USER_MAP_FILE = "users.dat";
    private static Map<String, User> usersMap = new HashMap<>(); // In-memory storage of users

    // public method to initialize the data
    public static void initializeData() {
        loadUsersMap(); // Load the users map from disk
    }

    public static void saveUserData(User user) {
        checkAndCreateDataDir();
        usersMap.put(user.getUsername(), user); // Update the in-memory map
        saveUsersMap(); // Persist the updated map
    }

    public static User loadUserData(String username) {
        return usersMap.get(username); // Retrieve the user from the in-memory map
    }
    
    public static Map<String, User> getUsersMap() {
        return usersMap;
    }

    public static void saveUsersMap() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getUsersMapFilePath()))) {
            oos.writeObject(usersMap);
        } catch (IOException e) {
            // Handle the exception, e.g., log it or show an error message
            System.err.println("Failed to save user data: " + e.getMessage());
            // Consider rethrowing as a runtime exception if the error is unrecoverable
            throw new RuntimeException("Failed to save user data", e);
        }
    }

    private static void loadUsersMap() {
        File file = new File(getUsersMapFilePath());
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object readObject = ois.readObject();
                if (readObject instanceof Map<?, ?>) {
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

    private static String getUsersMapFilePath() {
        return DATA_DIR + File.separator + USER_MAP_FILE;
    }

    private static void checkAndCreateDataDir() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }
}
