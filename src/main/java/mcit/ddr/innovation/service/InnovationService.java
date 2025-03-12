package mcit.ddr.innovation.service;

import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.dto.InnovationDTO;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.Review;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.enums.Role;
import mcit.ddr.innovation.exception.ResourceNotFoundException;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.repository.InnovationRepository;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.repository.ReviewRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InnovationService {

    private static final Logger logger = LoggerFactory.getLogger(InnovationService.class);
    private final InnovationRepository innovationRepository;
    // private final NotificationService notificationService;
    private final MyUserRepository myUserRepository;
    private final ReviewRepository reviewRepository;

    public Innovation createInnovation(Innovation innovation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        MyUser currentUser = myUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        innovation.setCreateDate(new Date());
        // innovation.setLastModifiedDate(new Date());
        innovation.setIsAssigned(false);
        innovation.setStatus(InnovStatus.DRAFT);
        innovation.setCreatedBy(currentUser);

        return innovationRepository.save(innovation);
    }


    //innovation status change service
    @Transactional
    public InnovationDTO updateInnovationStatus(Long id, Map<String, String> payload) {
        Innovation innovation = innovationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Innovation record not found!"));

        String statusValue = payload.get("status");
        if (statusValue == null || statusValue.isEmpty()) {
            throw new IllegalArgumentException("New Status Param is required");
        }

        // Update Innovation Status
        innovation.setStatus(InnovStatus.valueOf(statusValue.toUpperCase()));
        innovationRepository.save(innovation);

        // Create a log entry
        MyUser stateChangedByUser = myUserRepository.findById(Long.parseLong(payload.get("commentedBy")))
                .orElseThrow(() -> new ResourceNotFoundException("User Not Exist"));

        Review log = new Review();
        log.setStateChangedTo(InnovStatus.valueOf(statusValue.toUpperCase()));
        log.setComment(payload.get("comment"));
        log.setCreatedBy(stateChangedByUser);
        log.setCreatedDate(LocalDate.now());
        log.setInnovation(innovation);
        reviewRepository.save(log);

        if (log.getStateChangedTo() == InnovStatus.REJECTED) {
            logger.debug("Innovation status changed to REJECTED.");
        }

        return new InnovationDTO(innovation.getId(), innovation.getTitle(), innovation.getStatus());
    }


    //innovation assignment service
    @Transactional
    public Innovation assignInnovation(Long innovationId,Long assignerId, Long boardMemberId, String status,String comment) {
        // Retrieve the innovation
        Innovation innovation = innovationRepository.findById(innovationId)
                .orElseThrow(() -> new RuntimeException("Innovation not found"));

         // Get the currently authenticated admin (assigner)
         MyUser assigner = myUserRepository.findById(assignerId)
         .orElseThrow(() -> new RuntimeException("Assigner not found"));

        // Retrieve the board member
        MyUser boardMember = myUserRepository.findById(boardMemberId)
                .orElseThrow(() -> new RuntimeException("Board member not found"));


        // Update innovation details
        innovation.setAssigner(assigner);
        innovation.setStatus(InnovStatus.ASSIGNED);
        innovation.setBoardMember(boardMember);
        innovation.setIsAssigned(true);
        innovation.setAssignedDate(new Date());
        // innovation.setLastModifiedDate(new Date());
        innovationRepository.save(innovation);

        Review log = new Review();
        log.setStateChangedTo(InnovStatus.valueOf(status.toUpperCase()));
        log.setComment(comment);
        log.setCreatedBy(assigner);
        log.setCreatedDate(LocalDate.now());
        log.setInnovation(innovation);
        reviewRepository.save(log);

        if (log.getStateChangedTo() == InnovStatus.REJECTED) {
            logger.debug("Innovation status changed to REJECTED.");
        }

        // // Notify Innovator & Board Member
        // notificationService.sendNotification(
        //         innovation.getCreatedBy().getId(),
        //         "Your innovation has been assigned to a board member." + innovationId
        // );

        // notificationService.sendNotification(
        //         boardMember.getId(),
        //         "You have been assigned an innovation: " + innovation.getTitle() + innovationId
        // );

        return innovation;
    }

    public List<Innovation> getAllInnovations() {
        return innovationRepository.findAll();
    }

    public Optional<Innovation> getInnovationById(Long id) {
        return innovationRepository.findById(id);
    }
}