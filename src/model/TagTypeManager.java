package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Represents a manager for tag types. This class keeps track of predefined tag types and custom tag types.

public class TagTypeManager {
    private Set<String> customTagTypes = new HashSet<>();
    private static final Set<String> predefinedTagTypes = new HashSet<>(Arrays.asList("location", "person"));

    public boolean isValidTagType(String tagType) {
        return predefinedTagTypes.contains(tagType) || customTagTypes.contains(tagType);
    }

    public void addCustomTagType(String tagType) {
        customTagTypes.add(tagType);
    }

    public Set<String> getAllTagTypes() {
        Set<String> allTagTypes = new HashSet<>(predefinedTagTypes);
        allTagTypes.addAll(customTagTypes);
        return allTagTypes;
    }

    public static Set<String> getPredefinedTagTypes() {
        return predefinedTagTypes;
    }
}
