package model;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.io.Serializable;
import java.util.Objects;

/**
 * Class to represent an album in the Photos application.
 * An album has a name, a list of photos, and metadata including the date created and date last modified.
 */
public class Album implements Serializable {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the album.
     */
    private String name;

    /**
     * The list of photos in the album.
     */
    private ArrayList<Photo> photos;

    /**
     * The date the album was created.
     */
    private final LocalDateTime dateCreated;

    /**
     * The date the album was last modified.
     */
    private LocalDateTime dateModified;

   /**
    * Constructor that takes only the name of the album.
    * @param name The name of the album.
    */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<Photo>();
        this.dateCreated = LocalDateTime.now();
        this.dateModified = LocalDateTime.now();
    }

    /**
     * Constructor that takes the name of the album and a list of photos.
     * @param name The name of the album.
     * @param photos The list of photos to add to the album.
     */
    public Album(String name, java.util.ArrayList<Photo> photos) {
        this.name = name;
        this.photos = new ArrayList<>(photos); // make a copy of the list
        this.dateCreated = LocalDateTime.now();
        this.dateModified = LocalDateTime.now();
    }

    /**
     * Getter for name.
     * @return The name of the album.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name.
     * @param name The new name of the album.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for photos.
     * @return The list of photos in the album.
     */
    public ArrayList<Photo> getPhotos() {
        return photos;
    }
  
    /** 
     * Getter for dateCreated.
     * @return The date the album was created.
     */
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    /**
     * Getter for dateModified.
     * @return The date the album was last modified.
     */
    public LocalDateTime getDateModified() {
        return dateModified;
    }

    /**
     * Setter for dateModified.
     * @param dateModified The new date the album was last modified.
     */
    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    /**
     * Method to add a photo to the album.
     * @param photo The photo to add to the album.
     * @return True if the photo was added successfully, false if the photo is already in the album.
     */
    public boolean addPhoto(Photo photo) {
        // Check if the photo is already in the album.
        if (photos.contains(photo)) {
            return false;
        }
        photos.add(photo);
        dateModified = LocalDateTime.now();
        return true;
    }

    /**
     * Method to remove a photo from the album.
     * @param photo The photo to remove from the album.
     */
    public void removePhoto(Photo photo) {
        // Check if the photo is in the album.
        if (!photos.contains(photo)) {
            return;
        }
        photos.remove(photo);
        dateModified = LocalDateTime.now();
    }

    /**
     * Method to get the list of photos in the album.
     * @return The list of photos in the album.
     */
    public ArrayList<Photo> getPhotoList() {
        return photos;
    }

    /**
     * Method to get the metadata of the album.
     * @return A string containing the metadata of the album.
     */
    public String getAlbumMetadata() {
        return "Album name: " + name + "\nDate created: " + dateCreated + "\nDate last modified: " + dateModified;
    }

    /**
     * Method to get the number of photos in the album.
     * @return The number of photos in the album.
     */
    public int getNumPhotos() {
        return photos.size();
    }

    /**
     * Method to get the date range of the photos in the album.
     * @return A string containing the earliest and latest dates of the photos in the album.
     */
    public String getDateRange() {
        if (photos.isEmpty()) {
            return "N/A";
        }
        LocalDateTime earliest = photos.get(0).getDate();
        LocalDateTime latest = photos.get(0).getDate();
        for (Photo p : photos) {
            if (p.getDate().isBefore(earliest)) {
                earliest = p.getDate();
            }
            if (p.getDate().isAfter(latest)) {
                latest = p.getDate();
            }
        }
        return "Earliest: " + earliest + "\nLatest: " + latest;
    }

    /**
     * Method to check for equality between two albums
     * @return True if the albums are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Album)) {
            return false;
        }
        Album a = (Album) o;
        return name.equals(a.getName()) && photos.equals(a.getPhotos()) && dateCreated.equals(a.getDateCreated()) && dateModified.equals(a.getDateModified());
    }

    /**
     * Method to generate a hash code for the album.
     * @return The hash code for the album.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, photos, dateCreated, dateModified);
    }

    /**
     * Method to generate a string representation of the album.
     * @return A string representation of the album.
     */
    @Override
    public String toString() {
        return "Album name: " + name + "\nDate created: " + dateCreated + "\nDate last modified: " + dateModified + "\nNumber of photos: " + photos.size();
    }

    /** 
     * Method to sort the photos in the album by date.
     * This method uses a custom comparator to sort the photos by date.
     */
    public void sortPhotosByDate() {
        photos.sort(new Comparator<Photo>() {
            @Override
            public int compare(Photo p1, Photo p2) {
                return p1.getDate().compareTo(p2.getDate());
            }
        });
    }

    /**
     * Method to sort the photos in the album by tags.
     * This method uses a custom comparator to sort the photos by tags.
     */
    public void sortPhotosByTags() {
        photos.sort(new Comparator<Photo>() {
            @Override
            public int compare(Photo p1, Photo p2) {
                return p1.getTags().toString().compareTo(p2.getTags().toString());
            }
        });
    }

    /**
     * Method to get all tags in the album.
     * @return A list of all tags in the album.
     */
    public ArrayList<Tag> getAllTags() {
        ArrayList<Tag> allTags = new ArrayList<>();
        for (Photo p : photos) {
            for (Tag t : p.getTags()) {
                if (!allTags.contains(t)) {
                    allTags.add(t);
                }
            }
        }
        return allTags;
    }

    /**
     * Method to get all photos with a certain tag.
     * @param tag The tag to search for.
     * @return A list of all photos with the specified tag.
     */
    public ArrayList<Photo> getPhotosWithTag(Tag tag) {
        ArrayList<Photo> photosWithTag = new ArrayList<>();
        for (Photo p : photos) {
            if (p.getTags().contains(tag)) {
                photosWithTag.add(p);
            }
        }
        return photosWithTag;
    }

    /**
     * Method to get all photos in a date range.
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return A list of all photos in the specified date range.
     */
    public ArrayList<Photo> getPhotosInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        ArrayList<Photo> photosInDateRange = new ArrayList<>();
        for (Photo p : photos) {
            if (p.getDate().isAfter(startDate) && p.getDate().isBefore(endDate)) {
                photosInDateRange.add(p);
            }
        }
        return photosInDateRange;
    }
}
