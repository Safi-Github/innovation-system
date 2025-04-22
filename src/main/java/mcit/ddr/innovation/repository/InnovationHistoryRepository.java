package mcit.ddr.innovation.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mcit.ddr.innovation.entity.InnovationHistory;

public interface InnovationHistoryRepository extends JpaRepository<InnovationHistory, Long> {

    
}