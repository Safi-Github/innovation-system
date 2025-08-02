package mcit.ddr.innovation.config;

import jakarta.annotation.PostConstruct;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.enums.Role;
import mcit.ddr.innovation.repository.MyUserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDataInitializer {

    private final MyUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataInitializer(MyUserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        createDefaultUser("innovator", "123", Role.ROLE_INNOVATOR, "Innovator", "Innovator", "innovator@example.com");
        createDefaultUser("admin", "123", Role.ROLE_ADMIN, "Admin", "Admin", "admin@example.com");
        createDefaultUser("boardmember", "123", Role.ROLE_BOARD_MEMBER, "Board", "Member", "boardmember@example.com");
    }

    private void createDefaultUser(String username, String password, Role role, String firstName, String lastName, String email) {
        try {
            if (!userRepository.existsByUsername(username)) {
                MyUser user = new MyUser();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setRole(role);
                user.setFirstname(firstName);
                user.setLastname(lastName);
                user.setEmail(email);  // Set email here!
                userRepository.save(user);
                System.out.println("Created user: " + username);
            } else {
                System.out.println("User already exists: " + username);
            }
        } catch (Exception e) {
            System.err.println("Error creating user " + username);
            e.printStackTrace();
            throw e;
        }
    }



}
