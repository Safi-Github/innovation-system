package mcit.ddr.innovation.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mcit.ddr.innovation.entity.Innovation;

// public interface InnovationRepository extends JpaRepository<Innovation, Long>{}
public interface InnovationRepository extends JpaRepository<Innovation, Long>, JpaSpecificationExecutor<Innovation> { }
