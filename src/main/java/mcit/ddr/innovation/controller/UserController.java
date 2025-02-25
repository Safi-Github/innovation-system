package mcit.ddr.innovation.controller;

import org.apache.tomcat.util.http.HeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;

import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.enums.LiteracyLevel;
import mcit.ddr.innovation.enums.Role;
import mcit.ddr.innovation.jwt.JwtUtilityClass;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.service.MyUserDetailService;

@RestController
@RequestMapping("/api")
public class UserController {

  private final MyUserRepository myUserRepository;

  public UserController(MyUserRepository myUserRepository) {
      this.myUserRepository = myUserRepository;
  }

  @GetMapping("/users")
//   @Secured("ROLE_ADMIN")
//   @PreAuthorize("hasAuthority('ADMIN')")
  public List<MyUser> getAllUsers() {
      return myUserRepository.findAll();
  }

  //get specific user
  @GetMapping("/user/{id}")
  public Optional<MyUser> getUserById(@PathVariable Long id) {
      return myUserRepository.findById(id);
  }

  @PutMapping("/user/{id}")
  public ResponseEntity<MyUser> updateUser(@PathVariable Long id, @RequestBody MyUser userDetails) {
     Optional<MyUser> existingUser = myUserRepository.findById(id);
     if (existingUser.isEmpty()) {
        return ResponseEntity.notFound().build();
     }

     MyUser user = existingUser.get();
     user.setUsername(userDetails.getUsername());
     user.setRole(userDetails.getRole()); // Adjust according to your entity fields

     MyUser updatedUser = myUserRepository.save(user);

     return ResponseEntity.ok()
            .body(updatedUser);
    }

    //activate or de-activate user
    @PutMapping("/user/{id}/toggle-active")
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
  //
  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    if (!myUserRepository.existsById(id)) {
        return ResponseEntity.notFound().build();
    }
    myUserRepository.deleteById(id);

    return ResponseEntity
            .noContent()
            .build();
  }

    @GetMapping("/enums/literacy-levels")
    public ResponseEntity<List<Map<String, String>>> getLiteracyLevels() {
        List<Map<String, String>> literacyLevels = Arrays.stream(LiteracyLevel.values())
            .map(level -> Map.of("name", level.getDisplayName(), "value", level.name()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(literacyLevels);
    }

    @GetMapping("/enums/roles")
    public ResponseEntity<List<Map<String, String>>> getRoles() {
        List<Map<String, String>> roles = Arrays.stream(Role.values())
            .map(role -> Map.of("name", role.getDisplayName(), "value", role.name()))
            .collect(Collectors.toList());
    
        return ResponseEntity.ok(roles);
    }

}