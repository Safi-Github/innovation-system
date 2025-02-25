package mcit.ddr.innovation.enums;

public enum Role {
    ROLE_ADMIN("Admin"),
    ROLE_INNOVATOR("Innovator"),
    ROLE_BOARD_MEMBER("Board Member");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
