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

    static {
        // Load the user map when the class is first accessed
        loadUsersMap();
    }

    public static void saveUserData(User user) throws IOException {
        checkAndCreateDataDir();
        usersMap.put(user.getUsername(), user); // Update the in-memory map
        saveUsersMap(); // Persist the updated map
    }

    public static User loadUserData(String username) {
        return usersMap.get(username); // Retrieve the user from the in-memory map
    }
    public static Map<String, User> getUsersMap() {
        return new HashMap<>(usersMap); // Return a copy to avoid external modifications
    }

    public static void saveUsersMap() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getUsersMapFilePath()))) {
            oos.writeObject(usersMap);
        }
    }

    @SuppressWarnings("unchecked") // Suppress unchecked cast warning for deserialization operation 
    private static void loadUsersMap() {
        File file = new File(getUsersMapFilePath());
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                usersMap = (Map<String, User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace(); // Consider a more robust error handling
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
