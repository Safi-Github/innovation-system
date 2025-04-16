package mcit.ddr.innovation.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import mcit.ddr.innovation.dto.UserProfileDTO;
import mcit.ddr.innovation.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import mcit.ddr.innovation.jwt.JwtUtilityClass;
import mcit.ddr.innovation.jwt.LoginForm;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.service.MyUserDetailService;
import mcit.ddr.innovation.dto.AdminUserDTO; 
import mcit.ddr.innovation.entity.MyUser;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtilityClass jwtUtilityClass;
    @Autowired
    private MyUserDetailService myUserDetailService;
    @Autowired
    private FileStorageService fileStorageService;

    public AccountController(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    // @PostMapping("/register")
    // public MyUser createUser(@RequestBody MyUser user) {
    //     user.setPassword(passwordEncoder.encode(user.getPassword()));
    //     return myUserRepository.save(user);
    // }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestPart("user") MyUser user,
                                        @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        // Check for existing username
        if (myUserRepository.existsByUsername(user.getUsername().toLowerCase())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username already taken", "message", "Please choose another username."));
        }

        // Check for existing email
        if (myUserRepository.existsByEmail(user.getEmail().toLowerCase())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email already taken", "message", "Please choose another email."));
        }

        // Save profile image or assign default avatar
        String profileImagePath;
        if (imageFile != null && !imageFile.isEmpty()) {
            profileImagePath = fileStorageService.saveProfileImage(imageFile, user.getUsername());
        } else {
            profileImagePath = "D:\\DDR\\innovation\\user-profile\\default_avtar"; // Static path or full URL
        }

        // Set values
        user.setUsername(user.getUsername().toLowerCase());
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProfileImage(profileImagePath); // <--- new!

        // Save user
        MyUser savedUser = myUserRepository.save(user);

        // Optionally hide password in response
        savedUser.setPassword(null);

        return ResponseEntity.ok(savedUser);
    }


    // Endpoint to get the profile details of the logged-in user
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);
        Optional<MyUser> userOpt = myUserRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(new UserProfileDTO(userOpt.get()));
    }



    // Endpoint to update the user's profile (including image)
    @PutMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("phone") String phone,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        String username = extractUsernameFromToken(token);
        Optional<MyUser> userOpt = myUserRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        MyUser user = userOpt.get();

        // Update allowed fields only
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPhone(phone);

        if (imageFile != null && !imageFile.isEmpty()) {
            String profileImagePath = fileStorageService.saveProfileImage(imageFile, username);
            user.setProfileImage(profileImagePath);
        }

        MyUser updatedUser = myUserRepository.save(user);

        return ResponseEntity.ok(new UserProfileDTO(updatedUser));
    }


    // Endpoint to retrieve user's profile image
    @GetMapping("/profile/image/{username}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String username) {
        Optional<MyUser> userOpt = myUserRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String imagePath = userOpt.get().getProfileImage();
        Path path = Paths.get(imagePath);

        if (!Files.exists(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // Adjust based on your image format
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




    private String extractUsernameFromToken(String token) {
        String jwt = token.replace("Bearer ", ""); // Remove "Bearer " prefix
        return jwtUtilityClass.extractUsername(jwt); // Use your actual JWT utility method
    }




    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        try {
            //authentication start
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginForm.username(), loginForm.password()
                )
            );

            // check user isActive or not
            MyUser user = myUserRepository.findByUsername(loginForm.username())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!user.getIsActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("inactive User. Please contact admin.");
            }

            //generate token
            if (authentication.isAuthenticated()) {
                UserDetails userDetails = myUserDetailService.loadUserByUsername(loginForm.username());
                String token = jwtUtilityClass.generateToken(userDetails);

                // List<String> roles = jwtUtilityClass.extractRoles(token);

                // // Map<String, Object> response = new HashMap<>();
                // // response.put("token", token);
                // // response.put("roles", roles);
                return ResponseEntity.ok(token);
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    
    @GetMapping("/account")
    public AdminUserDTO getAccount(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtilityClass.extractUsername(jwt);

        Optional<MyUser> userOpt = myUserRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        MyUser user = userOpt.get();
        return new AdminUserDTO(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getFathername(),
            user.getNid(),
            user.getPhone(),
            user.getLiteracyLevel(),
            user.getEmail(),
            user.getUsername(),
            user.getRole()
        );
    }

    //activate or de-activate user
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<MyUser> toggleUserActive(@PathVariable Long id) {
        Optional<MyUser> existingUser = myUserRepository.findById(id);
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        MyUser user = existingUser.get();
        user.setIsActive(!user.getIsActive()); // Toggle isActive status
        MyUser updatedUser = myUserRepository.save(user);
    
        return ResponseEntity.ok(updatedUser);
   }

}