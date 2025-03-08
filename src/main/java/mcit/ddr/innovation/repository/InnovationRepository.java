package mcit.ddr.innovation.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import mcit.ddr.innovation.entity.Innovation;

public interface InnovationRepository extends JpaRepository<Innovation, Long>{
    
}
