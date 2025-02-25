package mcit.ddr.innovation.enums;

public enum LiteracyLevel {
    HIGH_SCHOOL ("High School"),
    BACHELOR("Bachelor"),
    MASTER("Master"),
    DOCTORATE("Doctor");

    private final String displayName;

    LiteracyLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}