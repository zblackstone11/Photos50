package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import javafx.scene.image.Image;

/**
 * Class to represent a photo in the Photos application.
 * @author ZB SL
 */
public class Photo implements Serializable {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L; // 1L is generic

    /**
     * The file path of the photo.
     */
    private String filePath;

    /**
     * The date the photo was taken.
     */
    private LocalDateTime dateTaken;

    /**
     * The caption of the photo.
     */
    private String caption;

    /**
     * The list of tags of the photo.
     */
    private ArrayList<Tag> tags;

    /**
     * The width for the thumbnail.
     */
    private final int THUMBNAIL_WIDTH = 100; // Width for the thumbnail

    /**
     * The height for the thumbnail.
     */
    private final int THUMBNAIL_HEIGHT = 100; // Height for the thumbnail

    /**
     * Constructor that takes only the file path of the photo.
     * @param filePath The file path of the photo.
     */
    public Photo(String filePath) {
        this.filePath = filePath;
        this.dateTaken = getLastModificationDate(filePath); // As per the instructions, the date taken is the last modification date
        this.caption = "";
        this.tags = new ArrayList<Tag>();
    }

    /**
     * Helper method to get the last modification date of a file.
     * @param filePath The file path of the photo.
     * @return The last modification date of the file.
     */
    private LocalDateTime getLastModificationDate(String filePath) {
        try {
            Path path = Paths.get(filePath);
            FileTime fileTime = Files.getLastModifiedTime(path); // Uses built-in Java NIO library to get the last modified time
            return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault()); // Converts the FileTime to LocalDateTime
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Getter for file path.
     * @return The file path of the photo.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Setter for file path.
     * @param filePath The file path of the photo.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Getter for date.
     * @return The date the photo was taken.
     */
    public LocalDateTime getDate() {
        return dateTaken;
    }

    /**
     * Setter for date.
     * @param date The date the photo was taken.
     */
    public void setDate(LocalDateTime date) {
        this.dateTaken = date;
    }

    /**
     * Getter for caption.
     * @return The caption of the photo.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Setter for caption.
     * @param caption The caption of the photo.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Getter for tags.
     * @return The tags of the photo.
     */
    public ArrayList<Tag> getTags() {
        return tags;
    }

    /**
     * Setter for tags.
     * @param tags The tags of the photo.
     */
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Getter for thumbnail.
     * @return The thumbnail of the photo.
     */
    public Image getThumbnail() {
        try {
            // Uses JavaFX Image class to create a thumbnail of the photo with the specified width and height
            Image image = new Image(new FileInputStream(filePath), THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, true, true);
            return image;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to add a tag.
     * @param tag The tag to add.
     * @return True if the tag was added, false otherwise.
     */
    public boolean addTag(Tag newTag) {
        for (Tag existingTag : tags) {
            if (existingTag.getTagName().equalsIgnoreCase(newTag.getTagName()) && existingTag.getTagValue().equalsIgnoreCase(newTag.getTagValue())) {
                return false; // Tag already exists, considering case-insensitivity
            }
        }
        tags.add(newTag);
        return true;
    }    

    /**
     * Method to delete a tag.
     * @param tag The tag to delete.
     * @return True if the tag was deleted, false otherwise.
     */
    public boolean deleteTag(Tag tag) {
        if (!tags.contains(tag)) {
            return false;
        }
        tags.remove(tag);
        return true;
    }

    /**
     * Method to check if a photo has a tag of a certain type.
     * @param tagType The type of tag to check for.
     * @return True if the photo has a tag of the specified type, false otherwise.
     */
    public boolean hasTagOfType(String tagType) {
        for (Tag tag : tags) {
            // Check if the tag type matches the specified tag type (case-insensitive)
            if (tag.getTagName().equalsIgnoreCase(tagType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Equals method.
     * @param o The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
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

    /**
     * Hash code method.
     * @return The hash code of the photo.
     */
    public int hashCode() {
        return Objects.hash(filePath, dateTaken);
    }

    /**
     * To string method.
     * @return The string representation of the photo.
     */
    public String toString() {
        return "Photo: " + filePath + ", Date: " + dateTaken + ", Caption: " + caption + ", Tags: " + tags;
    }
}
