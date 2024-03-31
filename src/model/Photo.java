package model;

// Represents a photo with properties such as file path, date of the photo (last modification date), caption, and a list of 
// tags. Methods could include adding/removing tags and updating the caption.

public class Photo {

    // First some fields including the file path, date of the photo using java.time package, caption, and a list of tags.
    private String filePath;
    private java.time.LocalDate date;
    private String caption;
    private java.util.ArrayList<Tag> tags;

    // Constructors: one that takes just the file path, and one that takes all fields.

    // Constructor that takes just the file path.
    public Photo(String filePath) {
        this.filePath = filePath;
        this.date = java.time.LocalDate.now(); // might need to change this to get the photos last modified date
        this.caption = "";
        this.tags = new java.util.ArrayList<Tag>();
    }

    // Constructor that takes all fields.
    public Photo(String filePath, java.time.LocalDate date, String caption, java.util.ArrayList<Tag> tags) {
        this.filePath = filePath;
        this.date = date;
        this.caption = caption;
        this.tags = new java.util.ArrayList<>(tags); // make a copy of the list
    }

    // Space to add more constructors if needed. Maybe one that takes just the file path and date/caption/tags

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
    public java.time.LocalDate getDate() {
        return date;
    }

    // Setter for date.
    public void setDate(java.time.LocalDate date) {
        this.date = date;
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
    public java.util.ArrayList<Tag> getTags() {
        return tags;
    }

    // Setter for tags.
    public void setTags(java.util.ArrayList<Tag> tags) {
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
        return filePath.equals(p.getFilePath()) && date.equals(p.getDate());
    }

    // Hashcode method.
    public int hashCode() {
        return java.util.Objects.hash(filePath, date);
    }

    // toString method. This will be useful for debugging.

    // toString method.
    public String toString() {
        return "Photo: " + filePath + ", Date: " + date + ", Caption: " + caption + ", Tags: " + tags;
    }

    // Space for additional methods such as a method to get all tags as a string, a method to get all tags as a list of strings,

}
