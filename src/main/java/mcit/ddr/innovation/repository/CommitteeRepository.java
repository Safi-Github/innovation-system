package mcit.ddr.innovation.repository;

import mcit.ddr.innovation.entity.Committee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommitteeRepository extends JpaRepository<Committee, Long> {
    Optional<Committee> findById(Long id);

    Optional<Committee> findByName(String newName);
}

