package mcit.ddr.innovation.config;

import jakarta.annotation.PostConstruct;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.enums.Role;
import mcit.ddr.innovation.repository.MyUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDataInitializer {

    private final MyUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataInitializer(MyUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        createDefaultUser("innovator", "123", Role.ROLE_INNOVATOR);
        createDefaultUser("admin", "123", Role.ROLE_ADMIN);
        createDefaultUser("boardmember", "123", Role.ROLE_BOARD_MEMBER);
    }

    private void createDefaultUser(String username, String rawPassword, Role role) {
        Optional<MyUser> existing = userRepository.findByUsername(username);
        if (existing.isEmpty()) {
            MyUser user = new MyUser();
            user.setEmail(username + "@example.com");
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            user.setIsActive(true);
//            user.setIsEmailVerified(true);
            userRepository.save(user);
            System.out.println("✅ Default " + role.name() + " user created: " + username);
        }
    }
}
