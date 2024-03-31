package model;

import java.io.Serializable;
import java.util.Objects;

// Represents a tag with properties like tag name and value (e.g., "location", "New York"). 
// Might have methods for editing the tag value.

//SL
//Diffrent tag names we could have: location, person, aspect ratio
//All tags are opitoal but I thinbk we should have a Stock boolean that is necessary for all 

public class Tag implements Serializable {

    // First some fields including the tag name and value.
    private static final long serialVersionUID = 1L;
    private String tagName; // location, person, aspect ratio etc
    private String tagValue; // New York, John Doe, 16:9 etc

    // Constructors: one that takes just the tag name, and one that takes both.

    // Constructor that takes just the tag name.
    public Tag(String tagName) {
        this.tagName = tagName;
        this.tagValue = "";
    }

    // Constructor that takes both.
    public Tag(String tagName, String tagValue) {
        this.tagName = tagName;
        this.tagValue = tagValue;
    }

    // Getters and setters for all fields.

    // Getter for tag name.
    public String getTagName() {
        return tagName;
    }

    // Setter for tag name.
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    // Getter for tag value.
    public String getTagValue() {
        return tagValue;
    }

    // Setter for tag value.
    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    // Equals, hashCode, and toString methods.

    // Equals method that checks if two tags are equal.
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) obj;
        return tagName.equals(other.tagName) && tagValue.equals(other.tagValue);
    }

    // Hashcode method.
    public int hashCode() {
        return Objects.hash(tagName, tagValue);
    }

    // toString method.
    public String toString() {
        return tagName + ": " + tagValue;
    }
    
}
