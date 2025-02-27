package mcit.ddr.innovation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.jwt.JwtUtilityClass;
import mcit.ddr.innovation.jwt.LoginForm;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.service.MyUserDetailService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Slf4j
@RestController
// @RequestMapping("/api")
public class AuthController {
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private JwtUtilityClass jwtUtilityClass;
  @Autowired
  private MyUserDetailService myUserDetailService;

  private final MyUserRepository myUserRepository;

  public AuthController(MyUserRepository myUserRepository) {
      this.myUserRepository = myUserRepository;
  }

  //using the spring blade thymeleave
  // @GetMapping("/home")
  // public String handleWelcome() {
  //   return "home";
  // }

  // @GetMapping("/admin/home")
  // public String handleAdminHome() {
  //   return "home_admin";
  // }

  // @GetMapping("/user/home")
  // public String handleUserHome() {
  //   return "home_user";
  // }

//   @GetMapping("/protected")
//   public List<MyUser> getAllUsers() {
//       return myUserRepository.findAll();
//   }

@PostMapping("/authenticate")
public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginForm loginForm) {
    try {
        //authentication start
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginForm.username(), loginForm.password()
            )
        );

        //Does user isActive
        MyUser user = myUserRepository.findByUsername(loginForm.username())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("inactive User. Please contact admin.");
        }

        //generate token
        if (authentication.isAuthenticated()) {
          UserDetails userDetails = myUserDetailService.loadUserByUsername(loginForm.username());
            String token = jwtUtilityClass.generateToken(userDetails);

            List<String> roles = jwtUtilityClass.extractRoles(token);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("roles", roles);
            return ResponseEntity.ok(response);
        }
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error");
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
}

}