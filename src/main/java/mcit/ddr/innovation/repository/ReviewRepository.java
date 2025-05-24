package mcit.ddr.innovation.repository;

import mcit.ddr.innovation.dto.ReviewAssigneesLogsResponseDTO;
import mcit.ddr.innovation.dto.ReviewLogsResponseDTO;
import mcit.ddr.innovation.entity.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // the innovation assigned to board members logs
    @Query("SELECT new mcit.ddr.innovation.dto.ReviewAssigneesLogsResponseDTO(" +
            "r.id, r.stateChangedTo, r.createdBy, r.createdDate, r.consideration, r.assignedTo) " +
            "FROM Review r WHERE r.innovation.id = :innovationId AND r.assignedTo IS NOT NULL")
    List<ReviewAssigneesLogsResponseDTO> findReviewAssignedLogs(@Param("innovationId") Long innovationId);

    // the innovation all logs
    // @Query("SELECT new mcit.ddr.innovation.dto.ReviewLogsResponseDTO(" +
    //     "r.id, r.stateChangedTo, r.createdBy, r.createdDate, r.comment,r.innovationHistory) " +
    //     "FROM Review r WHERE r.innovation.id = :innovationId")
    // List<ReviewLogsResponseDTO> findReviewLogs(@Param("innovationId") Long innovationId);

    @Query("SELECT r FROM Review r WHERE r.innovation.id = :innovationId")
    List<Review> findReviewLogs(@Param("innovationId") Long innovationId);
}
