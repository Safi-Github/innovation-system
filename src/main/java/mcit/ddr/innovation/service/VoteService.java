package mcit.ddr.innovation.service;

import mcit.ddr.innovation.dto.MyVoteResponseDTO;
import mcit.ddr.innovation.dto.VoteResponseDTO;
import mcit.ddr.innovation.entity.CommitteeMember;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.Vote;
import mcit.ddr.innovation.enums.VoteDecision;
import mcit.ddr.innovation.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public class VoteService {
    private final InnovationRepository innovationRepository;
    private final VoteRepository voteRepository;
    private final MyUserRepository myUserRepository;
    private final CommitteeMemberRepository committeeMemberRepository;

    public VoteService(InnovationRepository innovationRepository,MyUserRepository myUserRepository,VoteRepository voteRepository,CommitteeMemberRepository committeeMemberRepository) {
        this.innovationRepository = innovationRepository;
        this.voteRepository = voteRepository;
        this.myUserRepository = myUserRepository;
        this.committeeMemberRepository=committeeMemberRepository;
    }

    public ResponseEntity<?> vote(Long innovationId, VoteDecision decesion) {

        Innovation innovation = innovationRepository.findById(innovationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Innovation not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        MyUser loggedInUser = myUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        CommitteeMember member = committeeMemberRepository
                .findByCommitteeIdAndUserId(innovation.getCommittee().getId(), loggedInUser.getId())
                .orElseThrow(() -> new AccessDeniedException("User is not a committee member"));

        Optional<Vote> existingVoteOpt = voteRepository.findByUserAndInnovation(loggedInUser, innovation);

        if (existingVoteOpt.isPresent()) {
            Vote existingVote = existingVoteOpt.get();

            if (existingVote.getDecision() == VoteDecision.APPROVED) {
                return ResponseEntity.badRequest().body("Vote already approved. Cannot change or comment.");
            }

            if (existingVote.getDecision() == VoteDecision.REJECTED && decesion == VoteDecision.APPROVED) {
                existingVote.setDecision(VoteDecision.APPROVED);
                existingVote.setVotedAt(LocalDate.now());
                voteRepository.save(existingVote);
                return ResponseEntity.ok("Vote updated to APPROVED.");
            }

            return ResponseEntity.badRequest().body("Already rejected. You can only change to approved.");
        }

        // Create new vote
        Vote newVote = new Vote();
        newVote.setInnovation(innovation);
        newVote.setUser(loggedInUser);
        newVote.setDecision(decesion);
        newVote.setVotedAt(LocalDate.now());

        voteRepository.save(newVote);
        return ResponseEntity.ok("Vote submitted.");
    }

    public ResponseEntity<?> getVoteForLoggedInUser(Long innovationId) {
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        MyUser user = myUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Optional<Vote> voteOpt = voteRepository.findByUserIdAndInnovationId(user.getId(), innovationId);

        if (voteOpt.isPresent()) {
            Vote vote = voteOpt.get();
            MyVoteResponseDTO dto = new MyVoteResponseDTO(vote.getDecision(), vote.getVotedAt());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(404).body("Vote not found for this innovation and user.");
        }
    }

    public ResponseEntity<?> getVotesByInnovationId(Long innovationId) {
        List<Vote> votes = voteRepository.findByInnovationId(innovationId);

        List<VoteResponseDTO> voteDTOs = votes.stream()
                .map(vote -> new VoteResponseDTO(
                        vote.getDecision(),
                        vote.getVotedAt(),
                        vote.getUser()
                ))
                .toList();

        return ResponseEntity.ok(voteDTOs);
    }

}
