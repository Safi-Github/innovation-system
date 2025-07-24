package mcit.ddr.innovation.config;

import jakarta.annotation.PostConstruct;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.enums.Role;
import mcit.ddr.innovation.repository.MyUserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        createDefaultUser("innovator", "123", Role.ROLE_INNOVATOR, "Innovator", "Innovator");
        createDefaultUser("admin", "123", Role.ROLE_ADMIN, "Admin", "Admin");
        createDefaultUser("boardmember", "123", Role.ROLE_BOARD_MEMBER, "Board", "Member");
    }


    private void createDefaultUser(String username, String password, Role role, String firstName, String lastName) {
        if (!userRepository.existsByUsername(username)) {
            MyUser user = new MyUser();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setFirstname(firstName);
            user.setLastname(lastName);
            userRepository.save(user);
        }
    }

}
