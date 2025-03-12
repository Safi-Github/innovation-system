package mcit.ddr.innovation.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import mcit.ddr.innovation.entity.InvolvedPerson;

public interface InvolvedPersonRepository extends JpaRepository<InvolvedPerson, Long>{
    List<InvolvedPerson> findByInnovationId(Long innovationId);

}
