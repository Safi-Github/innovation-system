package mcit.ddr.innovation.repository;

import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.Vote;
import mcit.ddr.innovation.enums.VoteDecision;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
  Optional<Vote> findByUserAndInnovation(MyUser user, Innovation innovation);

  Optional<Vote> findByUserIdAndInnovationId(Long userId, Long innovationId);

  List<Vote> findByInnovationId(Long innovationId);

  boolean existsByInnovationIdAndDecision(Long innovationId, VoteDecision decision);

}