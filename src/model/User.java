package model;

import java.util.ArrayList;
import java.util.List;

// Represents a user with properties like username, list of albums, and possibly a password. 
// Methods might involve album management (add, delete, rename).

public class User {

    // First some fields including the username and a list of albums.
    private String username;
    private java.util.ArrayList<Album> albums;

    // Constructor that takes just the username.
    public User(String username) {
        this.username = username;
        this.albums = new java.util.ArrayList<Album>();
    }

    // Constructor that takes username and albums.
    public User(String username, java.util.ArrayList<Album> albums) {
        this.username = username;
        this.albums = new java.util.ArrayList<>(albums); // make a copy of the list
    }

    // Getters and setters for all fields.

    // Getter for username.
    public String getUsername() {
        return username;
    }

    // Setter for username.
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for albums.
    public java.util.ArrayList<Album> getAlbums() {
        return albums;
    }

    // Setter for albums. Might drop this and just use addAlbum/removeAlbum methods.
    public void setAlbums(java.util.ArrayList<Album> albums) {
        this.albums = albums;
    }

    // Methods like create album, delete album, rename album

    // Method to add an album to the list of albums.
    // Change to check for duplicates since we are not allowing duplicate album names.
    public void addAlbum(Album album) {
        if (!albums.contains(album)) {
            albums.add(album);
        }
    }

    // Method to remove an album from the list of albums.
    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    // Method to rename an album.
    public void renameAlbum(Album album, String newName) {
        album.setName(newName);
    }

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
    public List<Album> getAlbumsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
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
        return java.util.Objects.hash(username, albums);
    }

    // toString method.
    @Override
    public String toString() {
        return "Username: " + username + "\nAlbums: " + albums;
    }
    
}
