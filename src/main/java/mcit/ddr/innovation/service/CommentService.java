package mcit.ddr.innovation.service;


import mcit.ddr.innovation.dto.CommentDTO;
import mcit.ddr.innovation.entity.*;
import mcit.ddr.innovation.enums.VoteDecision;
import mcit.ddr.innovation.repository.*;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    private final InnovationRepository innovationRepository;
    private final MyUserRepository myUserRepository;
    private final CommitteeMemberRepository committeeMemberRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;

    public CommentService(InnovationRepository innovationRepository, MyUserRepository myUserRepository, CommitteeMemberRepository committeeMemberRepository, VoteRepository voteRepository, CommentRepository commentRepository) {
        this.innovationRepository = innovationRepository;
        this.myUserRepository = myUserRepository;
        this.committeeMemberRepository = committeeMemberRepository;
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
    }

    public ResponseEntity<?> addComment(Long innovationId, String comment) {
        Innovation innovation = innovationRepository.findById(innovationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Innovation not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        MyUser loggedInUser = myUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Logged In User not found: " + username));

        CommitteeMember member = committeeMemberRepository
                .findByCommitteeIdAndUserId(innovation.getCommittee().getId(), loggedInUser.getId())
                .orElseThrow(() -> new AccessDeniedException("User is not a committee member"));

        Vote vote = voteRepository.findByUserIdAndInnovationId(loggedInUser.getId(),innovation.getId())
                .orElseThrow(() -> new AccessDeniedException("You have not voted the Innovation yet"));

        if (vote.getDecision() == VoteDecision.APPROVED) {
            return ResponseEntity.badRequest().body("Approved vote cannot comment.");
        }

        Comment comObj = new Comment();

        comObj.setInnovation(innovation);
        comObj.setUser(loggedInUser);
        comObj.setComment(comment);
        comObj.setCommentedAt(LocalDate.now());

        commentRepository.save(comObj);

        return ResponseEntity.ok("Comment added.");
    }

    public ResponseEntity<?> getUserCommentsForInnovation(Long innovationId, Long userId) {
        Innovation innovation = innovationRepository.findById(innovationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Innovation not found"));

        MyUser user = myUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<CommentDTO> userComments = commentRepository.findByUserIdAndInnovationId(userId, innovationId);

        return ResponseEntity.ok(userComments);
    }

    //get baord member comments for an innovation
    public ResponseEntity<?> getAllMemberCommentsForInnovation(Long innovationId) {
        Innovation innovation = innovationRepository.findById(innovationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Innovation not found"));

        List<CommentDTO> comments = commentRepository.findByInnovationId(innovationId);

        if (comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(comments);
    }

}