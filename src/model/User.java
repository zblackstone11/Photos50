package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.io.IOException;

// Represents a user with properties like username, list of albums, and possibly a password. 
// Methods might involve album management (add, delete, rename).

public class User implements Serializable {

    // First some fields including the username and a list of albums.
    private static final long serialVersionUID = 1L;
    private String username;
    private ArrayList<Album> albums;
    // private boolean isAdmin = false;

    // Method to check if user is admin
    public boolean isAdmin() {
        return "admin".equals(this.username);
    }

    // Constructor that takes just the username.
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<Album>();
    }

    // Constructor that takes username and albums.
    public User(String username, ArrayList<Album> albums) {
        this.username = username;
        this.albums = new ArrayList<>(albums); // make a copy of the list
    }

    // Getters and setters for all fields.

    // Getter for username.
    public String getUsername() {
        return this.username;
    }

    // Setter for username.
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for albums.
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    // Methods like create album, delete album, rename album

    // Method to add an album to the list of albums.
    public void createAlbum(Album album) {
        if (!albums.contains(album)) {
            albums.add(album);
        }
    }

    // Method to remove an album from the list of albums.
    public void deleteAlbum(Album album) {
        albums.remove(album);
    }

    // Method to rename an album.
    public void renameAlbum(Album album, String newName) {
        album.setName(newName);
    }

    // Method to open an album, for later with main user screen

    // Album retrieval methods

    // Method to get an album by name.
    public Album getAlbumByName(String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return album;
            }
        }
        return null;
    }

    // Method to get albums by tag.
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

    // Method to get albums by date range.
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

    // Method to get albums by caption.
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

    // Method to save user data.
    public void saveUserData() {
        DataManager.saveUserData(this);
    }

    // Method to load user data.
    public static User loadUserData(String username) {
        return DataManager.loadUserData(username);
    }

    // Equals and hashcode, toString, etc.

    // Equals method.
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
    
    // Hashcode method.
    @Override
    public int hashCode() {
        return Objects.hash(username, albums);
    }

    // toString method.
    @Override
    public String toString() {
        return "Username: " + username + "\nAlbums: " + albums;
    }
    
}
