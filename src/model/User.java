package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * Class to represent a user in the Photos application.
 */
public class User implements Serializable {

    // First some fields including the username and a list of albums.
    private static final long serialVersionUID = 1L;
    private String username;
    private ArrayList<Album> albums;
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
     * Setter for albums.
     * @param albums The list of albums of the user.
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
        if (!albums.contains(album)) {
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
     */
    public void renameAlbum(Album album, String newName) {
        album.setName(newName);
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

    /**
     * Method to get albums by tag.
     * @param tag The tag to search for.
     * @return The list of albums that contain the specified tag.
     */
    public List<Album> getAlbumsByTag(Tag tag) {
        List<Album> matchingAlbums = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                if (photo.getTags().contains(tag)) {
                    matchingAlbums.add(album);
                    break; // Stop searching this album if a match is found
                }
            }
        }
        return matchingAlbums;
    }

    /**
     * Method to get albums by date range.
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return The list of albums that contain photos within the specified date range.
     */
    public List<Album> getAlbumsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Album> matchingAlbums = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                if (photo.getDate().isAfter(startDate) && photo.getDate().isBefore(endDate)) {
                    matchingAlbums.add(album);
                    break; // Stop searching this album if a match is found
                }
            }
        }
        return matchingAlbums;
    }

    /**
     * Method to get albums by caption.
     * @param caption The caption to search for.
     * @return The list of albums that contain photos with the specified caption.
     */
    public List<Album> getAlbumsByCaption(String caption) {
        List<Album> matchingAlbums = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                if (photo.getCaption().contains(caption)) {
                    matchingAlbums.add(album);
                    break; // Stop searching this album if a match is found
                }
            }
        }
        return matchingAlbums;
    }

    // Data persistence methods. Save and load user data. Use serialization and deserialization. Linked to DataManager.

    /**
     * Method to save user data.
     * Uses DataManager to save the user data.
     * This method is called when the user logs out.
     */
    public void saveUserData() {
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
