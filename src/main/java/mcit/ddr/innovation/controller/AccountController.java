package mcit.ddr.innovation.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import mcit.ddr.innovation.jwt.JwtUtilityClass;
import mcit.ddr.innovation.jwt.LoginForm;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.service.MyUserDetailService;
import mcit.ddr.innovation.dto.AdminUserDTO; 
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.enums.Role;

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

    public AccountController(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    // @PostMapping("/register")
    // public MyUser createUser(@RequestBody MyUser user) {
    //     user.setPassword(passwordEncoder.encode(user.getPassword()));
    //     return myUserRepository.save(user);
    // }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody MyUser user) {
        // Check for existing user and email
        if (myUserRepository.existsByUsername(user.getUsername().toLowerCase())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Username already taken", "message", "Please choose another username."));
        }
        if (myUserRepository.existsByEmail(user.getEmail().toLowerCase())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Email already taken", "message", "Please choose another Email."));
        }
        // Set values and save user
        user.setUsername(user.getUsername().toLowerCase());
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        MyUser savedUser = myUserRepository.save(user);
            return ResponseEntity.ok(savedUser);
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

            //checi user isActive or not
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