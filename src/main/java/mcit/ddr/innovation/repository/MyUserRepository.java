package mcit.ddr.innovation.repository;

import mcit.ddr.innovation.enums.Role;
import org.antlr.v4.runtime.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import mcit.ddr.innovation.entity.MyUser;

import java.util.List;
import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser, Long> {

    Optional<MyUser> findByUsername(String username);

    Optional<MyUser> findById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    MyUser findByEmail(String email);

    List<MyUser> findByRole(Role role);

//  Optional<MyUser> findByEmailVerificationToken(String token);



}