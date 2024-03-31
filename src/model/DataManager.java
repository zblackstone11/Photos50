package model;

import java.io.*;
// A class dedicated to handling data persistence, such as loading and saving user data, albums, and photos. 
// This class could utilize serialization to manage user sessions and their corresponding data.

public class DataManager {
    // Core responsibilities: saving and loading user data, albums, and photos.

    // Constants
    private static final String DATA_DIR = "data";
    private static final String USER_DATA_FILE_PREFIX = "user_"; // Prefix for user data files

    // Save user data to a file. This method serializes the User object to a file.
    public static void saveUserData(User user) throws IOException {
        checkAndCreateDataDir();
        String userFilePath = getUserDataFilePath(user.getUsername());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFilePath))) {
            oos.writeObject(user);
        }
    }

    // Load user data from a file. This method deserializes the User object from a file.
    public static User loadUserData(String username) throws IOException, ClassNotFoundException {
        String userFilePath = getUserDataFilePath(username);
        File userFile = new File(userFilePath);
        if (!userFile.exists()) {
            return null; // or throw an exception, decide later
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFilePath))) {
            return (User) ois.readObject();
        }
    }

    // Utility methods
    // Check if the data directory exists, and create it if it doesn't.
    private static void checkAndCreateDataDir() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }

    // Get the file path for a user's data file.
    private static String getUserDataFilePath(String username) {
        return DATA_DIR + File.separator + USER_DATA_FILE_PREFIX + username + ".dat";
    }
    
}
