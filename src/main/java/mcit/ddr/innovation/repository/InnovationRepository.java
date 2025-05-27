package mcit.ddr.innovation.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import mcit.ddr.innovation.entity.Innovation;

// public interface InnovationRepository extends JpaRepository<Innovation, Long>{}
public interface InnovationRepository extends JpaRepository<Innovation, Long>, JpaSpecificationExecutor<Innovation> { 

    @Query("SELECT i.committee.id AS committeeId, i.committee.name AS committeeName, COUNT(i.id) AS innovationCount " +
           "FROM Innovation i " +
           "GROUP BY i.committee.id, i.committee.name")
    List<CommitteeInnovationCount> countInnovationsPerCommittee();

    interface CommitteeInnovationCount {
        Long getCommitteeId();
        String getCommitteeName();
        Long getInnovationCount();
    }

    //count innovation for specific committee
    long countByCommitteeId(Long committeeId);
}
