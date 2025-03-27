package mcit.ddr.innovation.controller;

import mcit.ddr.innovation.dto.UserProfileResponse;
import mcit.ddr.innovation.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // add user-profile
    @PostMapping("/user-profile")
    public ResponseEntity<UserProfileResponse> createProfile(
            @RequestParam("userId") Long userId,
            @RequestParam("image") MultipartFile image) {
        try {
            // Save image
            String imagePath = saveImage(image);
            userProfileService.saveProfile(userId, imagePath);

            // Fetch the full profile
            UserProfileResponse profile = userProfileService.getProfileByUserId(userId);
            return ResponseEntity.ok(profile);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // get specific user profile
    @GetMapping("user-profile/{userId}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long userId) {
        UserProfileResponse profile = userProfileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    // save user image
    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty() || image.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Image is required and must be less than 2MB.");
        }

        String folder = "D:\\DDR\\innovation\\user-profile\\";
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches("(?i).+\\.(jpg|jpeg|png)$")) {
            throw new IllegalArgumentException("Only JPG, JPEG, and PNG are allowed.");
        }

        Path path = Paths.get(folder + originalFilename);
        Files.write(path, image.getBytes());
        return path.toString();
    }



}
