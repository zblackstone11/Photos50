package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class to represent a tag in the Photos application.
 * @author ZB SL
 */
public class Tag implements Serializable {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L; // 1L is generic

    /**
     * The name of the tag.
     */
    private String tagName; // location, person, etc.

    /**
     * The value of the tag.
     */
    private String tagValue; // New York, John Doe etc.

    /**
     * Constructor that takes the tag name and value.
     * @param tagName The name of the tag.
     * @param tagValue The value of the tag.
     */
    public Tag(String tagName, String tagValue) {
        this.tagName = tagName;
        this.tagValue = tagValue;
    }

    /**
     * Getter for tag name.
     * @return The name of the tag.
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Setter for tag name.
     * @param tagName The name of the tag.
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Getter for tag value.
     * @return The value of the tag.
     */
    public String getTagValue() {
        return tagValue;
    }

    /**
     * Setter for tag value.
     * @param tagValue The value of the tag.
     */
    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    /**
     * Equals method.
     * @param obj The object to compare to.
     * @return True if the objects are equal, false otherwise.
     */
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

    /**
     * Hash code method.
     * @return The hash code of the tag.
     */
    public int hashCode() {
        return Objects.hash(tagName, tagValue);
    }

    /**
     * To string method.
     * @return The string representation of the tag.
     */
    public String toString() {
        return tagName + ": " + tagValue;
    }
}
