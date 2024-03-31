package model;

// Represents an album with properties like name, list of photos, and possibly date range 
// (earliest and latest photo dates). Methods might include adding/removing photos, renaming the album, etc.

public class Album {

    // First some fields including the name of the album and a list of photos. Also date of creation and date of last modification.

    private String name;
    private java.util.ArrayList<Photo> photos;
    private java.time.LocalDate dateCreated;
    private java.time.LocalDate dateModified;

    // Constructors: one that takes just the name, and one that takes name and photos.

    // Constructor that takes just the name.
    public Album(String name) {
        this.name = name;
        this.photos = new java.util.ArrayList<Photo>();
        this.dateCreated = java.time.LocalDate.now();
        this.dateModified = java.time.LocalDate.now();
    }

    // Constructor that takes name and photos.
    public Album(String name, java.util.ArrayList<Photo> photos) {
        this.name = name;
        this.photos = new java.util.ArrayList<>(photos); // make a copy of the list
        this.dateCreated = java.time.LocalDate.now();
        this.dateModified = java.time.LocalDate.now();
    }

    // Getters and setters for all fields.

    // Getter for name.
    public String getName() {
        return name;
    }

    // Setter for name.
    public void setName(String name) {
        this.name = name;
    }

    // Getter for photos.
    public java.util.ArrayList<Photo> getPhotos() {
        return photos;
    }

    // Setter for photos. Might drop this and just use addPhoto/removePhoto methods.
    public void setPhotos(java.util.ArrayList<Photo> photos) {
        this.photos = photos;
    }

    // Getter for dateCreated.
    public java.time.LocalDate getDateCreated() {
        return dateCreated;
    }

    // Getter for dateModified.
    public java.time.LocalDate getDateModified() {
        return dateModified;
    }

    // Setter for dateModified.
    public void setDateModified(java.time.LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    // Methods to add/remove photos, get the photo list, album metadata, equals and hashcode, toString, etc.

    // Method to add a photo to the album.
    public void addPhoto(Photo photo) {
        photos.add(photo);
        dateModified = java.time.LocalDate.now();
    }

    // Method to remove a photo from the album.
    public void removePhoto(Photo photo) {
        // Check if the photo is in the album.
        if (!photos.contains(photo)) {
            return;
        }
        photos.remove(photo);
        dateModified = java.time.LocalDate.now();
    }

    // Method to get the list of photos in the album.
    public java.util.ArrayList<Photo> getPhotoList() {
        return photos;
    }

    // Method to get the metadata of the album.
    public String getAlbumMetadata() {
        return "Album name: " + name + "\nDate created: " + dateCreated + "\nDate last modified: " + dateModified;
    }

    // Equals and hashcode methods.

    // Equals method.
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

    // Hashcode method.
    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, photos, dateCreated, dateModified);
    }

    // toString method.
    @Override
    public String toString() {
        return "Album name: " + name + "\nDate created: " + dateCreated + "\nDate last modified: " + dateModified + "\nNumber of photos: " + photos.size();
    }

    // Sorting method to sort the photos in the album by date.
    public void sortPhotosByDate() {
        photos.sort(new java.util.Comparator<Photo>() {
            @Override
            public int compare(Photo p1, Photo p2) {
                return p1.getDate().compareTo(p2.getDate());
            }
        });
    }

    // Sorting method to sort the photos in the album by tags.
    public void sortPhotosByTags() {
        photos.sort(new java.util.Comparator<Photo>() {
            @Override
            public int compare(Photo p1, Photo p2) {
                return p1.getTags().toString().compareTo(p2.getTags().toString());
            }
        });
    }

    // Tag aggregation method to get all tags in the album.
    public java.util.ArrayList<Tag> getAllTags() {
        java.util.ArrayList<Tag> allTags = new java.util.ArrayList<>();
        for (Photo p : photos) {
            for (Tag t : p.getTags()) {
                if (!allTags.contains(t)) {
                    allTags.add(t);
                }
            }
        }
        return allTags;
    }

    // Photo retrieval method to get all photos with a certain tag.
    public java.util.ArrayList<Photo> getPhotosWithTag(Tag tag) {
        java.util.ArrayList<Photo> photosWithTag = new java.util.ArrayList<>();
        for (Photo p : photos) {
            if (p.getTags().contains(tag)) {
                photosWithTag.add(p);
            }
        }
        return photosWithTag;
    }

    // Photo retrieval method to get all photos taken within a certain date range.
    public java.util.ArrayList<Photo> getPhotosInDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        java.util.ArrayList<Photo> photosInDateRange = new java.util.ArrayList<>();
        for (Photo p : photos) {
            if (p.getDate().compareTo(startDate) >= 0 && p.getDate().compareTo(endDate) <= 0) {
                photosInDateRange.add(p);
            }
        }
        return photosInDateRange;
    }
    
}
