package mcit.ddr.innovation.dto;

import mcit.ddr.innovation.entity.MyUser;

public class UserProfileDTO {

    private final Long  id;
    private final String fullName;
    private final String username;
    private final String email;
    private final String literacyLevel;
    private final String role;
    private final String phone;
    private final boolean isActive;
    private final String profileImage;

    public UserProfileDTO(MyUser user) {
        this.id = user.getId();
        this.fullName = user.getFirstname() + " " + user.getLastname();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.literacyLevel = String.valueOf(user.getLiteracyLevel());
        this.role = String.valueOf(user.getRole());
        this.isActive = user.getIsActive();
        this.profileImage = user.getProfileImage(); // adjust if stored differently
    }

    // Getters only
    public Long getId() {return id;}
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getLiteracyLevel() { return literacyLevel; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public boolean isActive() { return isActive; }
    public String getImageUrl() { return profileImage; }
}
