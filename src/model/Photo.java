package model;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;

// Represents a photo with properties such as file path, date of the photo (last modification date), caption, and a list of 
// tags. Methods could include adding/removing tags and updating the caption.

public class Photo implements Serializable {

    // First some fields including the file path, date of the photo using java.time package, caption, and a list of tags.
    private static final long serialVersionUID = 1L; // 1L is generic
    private String filePath;
    private LocalDateTime dateTaken;
    private String caption;
    private ArrayList<Tag> tags;

    // Constructors: one that takes just the file path, and one that takes all fields.

    // Constructor that takes just the file path.
    public Photo(String filePath) {
        this.filePath = filePath;
        this.dateTaken = getLastModificationDate(filePath);
        this.caption = "";
        this.tags = new ArrayList<Tag>();
    }

    // Helper method to get the last modification date of a file.
    private LocalDateTime getLastModificationDate(String filePath) {
        try {
            Path path = Paths.get(filePath);
            FileTime fileTime = Files.getLastModifiedTime(path);
            return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Getters and setters for all fields.

    // Getter for file path.
    public String getFilePath() {
        return filePath;
    }

    // Setter for file path.
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // Getter for date.
    public LocalDateTime getDate() {
        return dateTaken;
    }

    // Setter for date.
    public void setDate(LocalDateTime date) {
        this.dateTaken = date;
    }

    // Getter for caption.
    public String getCaption() {
        return caption;
    }

    // Setter for caption.
    public void setCaption(String caption) {
        this.caption = caption;
    }

    // Getter for tags.
    public ArrayList<Tag> getTags() {
        return tags;
    }

    // Setter for tags.
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    // Tag management methods. Add a tag, remove a tag, and update a tag.

    // Method to add a tag.
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    // Method to remove a tag.
    // Need error handling in case the tag is not in the list.
    public void removeTag(Tag tag) {
        // If the tag is not in the list, throw an exception.
        if (!tags.contains(tag)) {
            throw new IllegalArgumentException("Tag not found in the list.");
        }
        tags.remove(tag);
    }

    // Method to update a tag.
    public void updateTag(Tag oldTag, Tag newTag) {
        tags.remove(oldTag);
        tags.add(newTag);
    }

    // Equals and hashcode methods. Two photos are equal if their file paths are equal and their dates are equal.

    // Equals method.
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Photo)) {
            return false;
        }
        Photo p = (Photo) o;
        return filePath.equals(p.getFilePath()) && dateTaken.equals(p.getDate());
    }

    // Hashcode method.
    public int hashCode() {
        return Objects.hash(filePath, dateTaken);
    }

    // toString method. This will be useful for debugging.

    // toString method.
    public String toString() {
        return "Photo: " + filePath + ", Date: " + dateTaken + ", Caption: " + caption + ", Tags: " + tags;
    }

    // Space for additional methods such as a method to get all tags as a string, a method to get all tags as a list of strings,

}
