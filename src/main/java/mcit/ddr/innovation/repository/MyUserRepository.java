package mcit.ddr.innovation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mcit.ddr.innovation.entity.MyUser;

import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser, Long> {

    Optional<MyUser> findByUsername(String username);
    
    Optional<MyUser> findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}