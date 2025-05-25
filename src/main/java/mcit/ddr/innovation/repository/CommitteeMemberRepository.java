package mcit.ddr.innovation.repository;

import mcit.ddr.innovation.dto.UserInCommitteesDTO;
import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.entity.CommitteeMember;
import mcit.ddr.innovation.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommitteeMemberRepository extends JpaRepository<CommitteeMember, Long> {
    boolean existsByUserAndCommittee(MyUser user, Committee committee);

    // Method to count head members in a committee
    long countByCommitteeAndIsHeadTrue(Committee committee);

    List<CommitteeMember> findByUserId(Long userId);

    @Query("SELECT new mcit.ddr.innovation.dto.UserInCommitteesDTO(c.id,c.name, c.isClosed, c.createdDate) " +
            "FROM CommitteeMember cm JOIN cm.committee c " +
            "WHERE cm.user.id = :userId")

    List<UserInCommitteesDTO> findCommitteeSummariesByUserId(Long userId);

    // 🔽 Add this method
    List<CommitteeMember> findByCommittee(Committee committee);


    void deleteByUserIdAndCommitteeId(Long userId, Long committeeId);

    boolean existsByUserIdAndCommitteeId(Long userId, Long committeeId);

    // Find all members of a specific committee
    List<CommitteeMember> findByCommitteeId(Long committeeId);

    // Find a specific member in a committee by userId
    Optional<CommitteeMember> findByCommitteeIdAndUserId(Long committeeId, Long userId);

    //count the committees a user is part of:
    @Query("SELECT COUNT(cm) FROM CommitteeMember cm WHERE cm.user.id = :userId")
    Long countCommitteesByUserId(@Param("userId") Long userId);

}