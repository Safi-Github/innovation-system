package mcit.ddr.innovation.repository;

import mcit.ddr.innovation.dto.CommentDTO;
import mcit.ddr.innovation.entity.Comment;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<CommentDTO> findByUserIdAndInnovationId(Long userId, Long innovationId);
}