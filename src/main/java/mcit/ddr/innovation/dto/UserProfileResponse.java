package mcit.ddr.innovation.dto;

import mcit.ddr.innovation.entity.UserProfile;

public class UserProfileResponse {

    private final Long id;
    private final String fullName;
    private final String username;
    private final String email;
    private final String literacyLevel;
    private final String role;
    private final boolean isActive;
    private final String imageUrl; // From UserProfile

    // Constructor using the UserProfile entity
    public UserProfileResponse(UserProfile profile) {
        this.id = profile.getUser().getId();  // Assuming UserProfile has a 'User' entity
        this.fullName = profile.getUser().getFirstname() + " " + profile.getUser().getLastname();
        this.username = profile.getUser().getUsername();
        this.email = profile.getUser().getEmail();
        this.literacyLevel = String.valueOf(profile.getUser().getLiteracyLevel());
        this.role = String.valueOf(profile.getUser().getRole());
        this.isActive = profile.getUser().getIsActive();
        this.imageUrl = profile.getImageUrl(); // Assuming imagePath is stored in UserProfile
    }

    // Getters
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getLiteracyLevel() { return literacyLevel; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive; }
    public String getImageUrl() { return imageUrl; }
}
