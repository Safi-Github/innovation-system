package mcit.ddr.innovation.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import mcit.ddr.innovation.entity.InvolvedPerson;

public interface InvolvedPersonRepository extends JpaRepository<InvolvedPerson, Long>{
    Optional<InvolvedPerson> findByNid(String Nid);
    Optional<InvolvedPerson> findById(Long id);

}
