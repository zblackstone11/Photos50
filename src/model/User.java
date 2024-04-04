package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.io.Serializable;

/**
 * Class to represent a user in the Photos application.
 * @author ZB SL
 */
public class User implements Serializable {

    /**
     * Serial version UID for serialization.
     * This is used to ensure that the deserialization process is compatible with the serialization process.
     */
    private static final long serialVersionUID = 1L; // Again 1L is generic

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The list of albums of the user.
     */
    private ArrayList<Album> albums;

    /**
     * The map of tag types and their multiplicities.
     */
    private Map<String, Integer> tagTypes; // Map to store tag types and their multiplicities


    /**
     * Method to check if the user is an admin.
     * @return True if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return "admin".equals(this.username.toLowerCase());
    }

    /**
     * Constructor that takes the username.
     * @param username The username of the user.
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<Album>();
        this.tagTypes = new HashMap<>(); // Initialize the map
        // Prepopulate the map with default tag types and multiplicities
        this.tagTypes.put("location", 1); // 'location' can have only one value
        this.tagTypes.put("person", Integer.MAX_VALUE); // 'person' can have multiple values
    }

    /**
     * Getter for username.
     * @return The username of the user.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Setter for username.
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for albums.
     * @return The list of albums of the user.
     */
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /**
     * Getter for tag types.
     * The key is the tag type, and the value is its multiplicity.
     * @return The map of tag types and their multiplicities.
     */
    public Map<String, Integer> getTagTypes() {
        return tagTypes;
    }

    /**
     * Setter for tag types.
     * @param tagTypes The map of tag types and their multiplicities.
     */
    public void setTagTypes(Map<String, Integer> tagTypes) {
        this.tagTypes = tagTypes;
    }

    /**
     * Method to add a tag type.
     * @param tagType The tag type to add.
     * @param multiplicity The multiplicity of the tag type.
     */
    public void addTagType(String tagType, Integer multiplicity) {
        this.tagTypes.put(tagType, multiplicity);
    }

    /**
     * Method to create an album.
     * Checks if the album already exists before adding it.
     * @param album The album to create.
     */
    public void createAlbum(Album album) {
        if (!albums.contains(album)) { // deal with case sensitivity elsewhere
            albums.add(album);
        }
    }

    /**
     * Method to check if an album exists.
     * @param name The name of the album to check.
     * @return True if the album exists, false otherwise.
     */
    public boolean albumExists(String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to delete an album.
     * @param album The album to delete.
     */
    public void deleteAlbum(Album album) {
        albums.remove(album);
    }

    /**
     * Method to rename an album.
     * @param album The album to rename.
     * @param newName The new name of the album.
     * @return True if the album was renamed successfully, false otherwise.
     * If an album with the new name already exists, return false, case-insensitive comparison.
     */
    public boolean renameAlbum(Album album, String newName) {
        String trimmedNewName = newName.trim();
    
        // Check for existing album with case-insensitive comparison
        for (Album existingAlbum : this.albums) {
            // If the existing album is NOT the same as the album being renamed AND has the same name as the new name
            if (!existingAlbum.equals(album) && existingAlbum.getName().equalsIgnoreCase(trimmedNewName)) {
                // An album with the new name already exists, return false to indicate failure
                return false;
            }
        }
        // No conflicting album name found, proceed with renaming
        album.setName(trimmedNewName);
        return true; // Indicate success
    }

    /**
     * Method to get an album by name.
     * @param name The name of the album to get.
     * @return The album with the specified name, or null if it doesn't exist.
     */
    public Album getAlbumByName(String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return album;
            }
        }
        return null;
    }

    // Data persistence methods. Save and load user data. Use serialization and deserialization. Linked to DataManager.

    /**
     * Method to save user data.
     * Uses DataManager to save the user data.
     * This method is called when the user logs out.
     */
    public void saveUserData() {
        // Save the user data using DataManager on this user object
        DataManager.saveUserData(this);
    }

    /**
     * Method to load user data.
     * Uses DataManager to load the user data.
     * This method is called when the user logs in.
     * @param username The username of the user to load.
     * @return The user with the specified username, or null if it doesn't exist.
     */
    public static User loadUserData(String username) {
        return DataManager.loadUserData(username);
    }

    /**
     * Equals method.
     * @param o The object to compare to.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User u = (User) o;
        return username.equals(u.getUsername()) && albums.equals(u.getAlbums());
    }
    
    /**
     * Hash code method.
     * @return The hash code of the user.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, albums);
    }

    /**
     * To string method.
     * @return The string representation of the user.
     */
    @Override
    public String toString() {
        return "Username: " + username + "\nAlbums: " + albums;
    }
}
