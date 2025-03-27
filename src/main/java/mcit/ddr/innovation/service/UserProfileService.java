package mcit.ddr.innovation.service;

import mcit.ddr.innovation.dto.UserProfileResponse;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.UserProfile;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.repository.UserProfileRepository;
import org.springframework.stereotype.Service;


@Service
public class UserProfileService {

        private final UserProfileRepository userProfileRepository;
        private final MyUserRepository myUserRepository;

        public UserProfileService(UserProfileRepository userProfileRepository, MyUserRepository myUserRepository) {
            this.userProfileRepository = userProfileRepository;
            this.myUserRepository = myUserRepository;
        }

        public void saveProfile(Long userId, String imagePath) {
            // Ensure that the userId exists in the database
            MyUser user = myUserRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            // Create the UserProfile entity and associate it with the user
            UserProfile profile = new UserProfile();
            profile.setUser(user);  // Link the user to the profile (user_id will be set)
            profile.setImageUrl(imagePath);

            // Save the profile with the correct user_id
            userProfileRepository.save(profile);
        }

        public UserProfileResponse getProfileByUserId(Long userId) {
            UserProfile profile;
            profile = userProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Profile not found for user: " + userId));
            return new UserProfileResponse(profile);
        }
}
