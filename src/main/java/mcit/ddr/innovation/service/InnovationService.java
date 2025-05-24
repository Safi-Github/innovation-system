package mcit.ddr.innovation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.dto.InnovationAssignmentResponseDTO;
import mcit.ddr.innovation.dto.InnovationSearchCriteriaDTO;
import mcit.ddr.innovation.dto.InnovationStatusChangeResponseDTO;
import mcit.ddr.innovation.dto.PartialInnovationUpdateDTO;
import mcit.ddr.innovation.entity.*;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.enums.Role;
import mcit.ddr.innovation.enums.VoteDecision;
import mcit.ddr.innovation.exception.ResourceNotFoundException;
import mcit.ddr.innovation.repository.*;
import mcit.ddr.innovation.service.FileStorageService;
import mcit.ddr.innovation.service.NotificationService;
import mcit.ddr.innovation.specification.InnovationSpecification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InnovationService {

    private static final Logger logger = LoggerFactory.getLogger(InnovationService.class);
    private final InnovationRepository innovationRepository;
    private final MyUserRepository myUserRepository;
    private final ReviewRepository reviewRepository;
    private final FileStorageService fileStorageService;
    private final InnovationHistoryRepository innovationHistoryRepository;
    private final NotificationService notificationService;
    private final CommitteeRepository committeeRepository;
    private final CommitteeMemberRepository committeeMemberRepository;
    private final VoteRepository voteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Innovation createInnovation(Innovation innovation, MultipartFile attachmentFile) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        MyUser currentUser = myUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        innovation.setCreateDate(new Date());
        innovation.setIsAssigned(false);
        innovation.setStatus(InnovStatus.DRAFT);
        innovation.setCreatedBy(currentUser);

        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            String filePath = fileStorageService.saveFile(attachmentFile);
            innovation.setAttachment(filePath);
        }
        return innovationRepository.save(innovation);
    }

    public Innovation partialUpdateInnovation(Long innovationId, PartialInnovationUpdateDTO partialUpdateDTO, MultipartFile attachmentFile) {
        Innovation innovation = innovationRepository.findById(innovationId)
                .orElseThrow(() -> new ResourceNotFoundException("Innovation not found"));

        if (partialUpdateDTO.getTitle() != null) {
            innovation.setTitle(partialUpdateDTO.getTitle());
        }
        if (partialUpdateDTO.getDescription() != null) {
            innovation.setDescription(partialUpdateDTO.getDescription());
        }
        if (partialUpdateDTO.getPurpose() != null) {
            innovation.setPurpose(partialUpdateDTO.getPurpose());
        }
        if (partialUpdateDTO.getCategory() != null) {
            innovation.setCategory(partialUpdateDTO.getCategory());
        }
        if (partialUpdateDTO.getAdditionalInfo() != null) {
            innovation.setAdditionalInfo(partialUpdateDTO.getAdditionalInfo());
        }
        if (partialUpdateDTO.getReasonsProvingYouCanInvent() != null) {
            innovation.setReasonsProvingYouCanInvent(partialUpdateDTO.getReasonsProvingYouCanInvent());
        }
        if (partialUpdateDTO.getImpact() != null) {
            innovation.setImpact(partialUpdateDTO.getImpact());
        }
        if (partialUpdateDTO.getResourcesNeeded() != null) {
            innovation.setResourcesNeeded(partialUpdateDTO.getResourcesNeeded());
        }

        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            String filePath = fileStorageService.saveFile(attachmentFile);
            innovation.setAttachment(filePath);
        }

        return innovationRepository.save(innovation);
    }
    //Innovation Status Change Service
    @Transactional
    public InnovationStatusChangeResponseDTO updateInnovationStatus(Long id, Map<String, String> payload) {
        Innovation innovation = innovationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Innovation record not found!"));

        String statusValue = payload.get("status");
        if (statusValue == null || statusValue.isEmpty()) {
            throw new IllegalArgumentException("New Status Param is required");
        }

        //Check if status is equal to Approved
        if(innovation.getCommittee() !=null && (InnovStatus.valueOf(statusValue.toUpperCase())==InnovStatus.APPROVED || 
        InnovStatus.valueOf(statusValue.toUpperCase())==InnovStatus.REJECTED))
            
            {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            MyUser loggedInUser = myUserRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("User not found: " + username));

            CommitteeMember member = committeeMemberRepository
            .findByCommitteeIdAndUserId(innovation.getCommittee().getId(), loggedInUser.getId())
                    .orElseThrow(() -> new AccessDeniedException("User is not a committee member"));
    
            //check the user is the committee head
            if (!Boolean.TRUE.equals(member.getIsHead())) {
                throw new AccessDeniedException("Only committee head can change the status");
            }

            // 🔒 Ensure all committee members have voted
            List<CommitteeMember> committeeMembers = committeeMemberRepository.findByCommitteeId(innovation.getCommittee().getId());
            Set<Long> votedUserIds = voteRepository.findByInnovationId(innovation.getId()).stream()
                .map(vote -> vote.getUser().getId())
                .collect(Collectors.toSet());

            boolean allMembersVoted = committeeMembers.stream()
                .allMatch(cm -> votedUserIds.contains(cm.getUser().getId()));

            if (!allMembersVoted) {
                throw new IllegalStateException("All committee members must vote before Final Rejection/Approval.");
            }

            if(InnovStatus.valueOf(statusValue.toUpperCase())==InnovStatus.APPROVED){
                // Block approval if any vote is REJECTED
                boolean hasRejectedVotes = voteRepository.existsByInnovationIdAndDecision(innovation.getId(), VoteDecision.REJECTED);
                if (hasRejectedVotes) {
                    throw new IllegalStateException("Cannot approve innovation with rejected votes.");
                }
            }else if(InnovStatus.valueOf(statusValue.toUpperCase())==InnovStatus.REJECTED){
                // Block rejection if no vote is REJECTED
                boolean hasAtLeastOneRejected = voteRepository.existsByInnovationIdAndDecision(innovation.getId(), VoteDecision.REJECTED);
                if (!hasAtLeastOneRejected) {
                    throw new IllegalStateException("Cannot reject innovation without any rejected votes.");
                }
            }
        }

        InnovStatus newStatus = InnovStatus.valueOf(statusValue.toUpperCase());
        innovation.setStatus(newStatus);
        innovationRepository.save(innovation);

        MyUser stateChangedByUser = myUserRepository.findById(Long.parseLong(payload.get("commentedBy")))
                .orElseThrow(() -> new ResourceNotFoundException("User Not Exist"));

        Review log = new Review();
        log.setStateChangedTo(newStatus);
        log.setConsideration(payload.get("consideration"));
        log.setCreatedBy(stateChangedByUser);
        log.setCreatedDate(LocalDate.now());
        log.setInnovation(innovation);
        Review savedLog = reviewRepository.save(log);

//      innovation history versioning part commented
//        if (savedLog.getStateChangedTo() == InnovStatus.REJECTED) {
//            System.out.println(savedLog.getStateChangedTo());
//            try {
//                // Convert the rejected innovation to JSON
//                Map<String, Object> snapshot = new HashMap<>();
//                snapshot.put("id", innovation.getId());
//                snapshot.put("title", innovation.getTitle());
//                snapshot.put("purpose", innovation.getPurpose());
//                snapshot.put("category", innovation.getCategory());
//                snapshot.put("description", innovation.getDescription());
//                snapshot.put("additionalInfo", innovation.getAdditionalInfo());
//                snapshot.put("reasonsProvingYouCanInvent", innovation.getReasonsProvingYouCanInvent());
//                snapshot.put("impact", innovation.getImpact());
//                snapshot.put("resourcesNeeded", innovation.getResourcesNeeded());
//                snapshot.put("attachment", innovation.getAttachment());
//
//                String innovationJson = objectMapper.writeValueAsString(snapshot);
//                System.out.println(innovationJson);
//
//                InnovationHistory innovHistory = new InnovationHistory();
//                innovHistory.setArchivedInnovationData(innovationJson);
//                innovHistory.setDateCreated(LocalDate.now());
//                innovHistory.setReview(savedLog);
//                innovHistory.setInnovation(innovation);
//                innovationHistoryRepository.save(innovHistory);
//
//            } catch (JsonProcessingException e) {
//                logger.error("Failed to convert innovation to JSON for history log", e);
//                // Optionally, throw or handle
//            }
//
//        }

//        notification part commented
//        String contentUrlInnovator = "http://localhost:3000/dashboard/innovator/review/" + innovation.getId();
//        String contentUrlBoardMember = "http://localhost:3000/dashboard/boardmember/review/" + innovation.getId();
//        if (savedLog.getStateChangedTo() == InnovStatus.APPROVED || savedLog.getStateChangedTo() == InnovStatus.REJECTED) {
//            notificationService.sendNotification(
//                    innovation.getCreatedBy().getId(),
//                    "Your Innovation Tittled \"" + innovation.getTitle() + "\" has been " + savedLog.getStateChangedTo().name().toLowerCase() + "",
//                    contentUrlInnovator
//            );
//        }
//        if (savedLog.getStateChangedTo() == InnovStatus.RESUBMITTED && innovation.getCommittee()!=null) {
//            notificationService.sendNotification(
//                    innovation.getCommittee().getId(),
//                    "Innovation Tittled \"" + innovation.getTitle() + "\" has been " + savedLog.getStateChangedTo().name().toLowerCase() + "to You",
//                    contentUrlBoardMember
//            );
//        }

        return new InnovationStatusChangeResponseDTO(
                innovation.getId(),
                innovation.getTitle(),
                innovation.getStatus(),
                stateChangedByUser,
                log.getCreatedDate(),
                log.getConsideration()
        );
    }

    //Innovation Assignment Service
    @Transactional
    public InnovationAssignmentResponseDTO assignInnovation(Long innovationId, Long assignerId, Long committeeId, String status, String consideration) {
        Innovation innovation = innovationRepository.findById(innovationId)
                .orElseThrow(() -> new RuntimeException("Innovation not found"));

        MyUser assigner = myUserRepository.findById(assignerId)
                .orElseThrow(() -> new RuntimeException("Assigner not found"));

//        Committee committee = myUserRepository.findById(committeeId)
//                .orElseThrow(() -> new RuntimeException("Committee not found"));

        Committee committee = committeeRepository.findById(committeeId)
                .orElseThrow(() -> new RuntimeException("Committee not found"));

        innovation.setAssigner(assigner);
        innovation.setStatus(InnovStatus.ASSIGNED);
        innovation.setCommittee(committee);
        innovation.setIsAssigned(true);
        innovation.setAssignedDate(new Date());
        innovationRepository.save(innovation);

        Review log = new Review();
        log.setStateChangedTo(InnovStatus.valueOf(status.toUpperCase()));
        log.setCreatedBy(assigner);
        log.setCreatedDate(LocalDate.now());
        log.setConsideration(consideration);
        log.setAssignedTo(committee);
        log.setInnovation(innovation);
        Review savedLog = reviewRepository.save(log);

//        notification part commented
//        String contentUrlInnovator = "http://localhost:3000/dashboard/innovator/review/" + innovation.getId();
//        String contentUrlBoardMember = "http://localhost:3000/dashboard/boardmember/review/" + innovation.getId();
//        if (savedLog.getStateChangedTo() == InnovStatus.ASSIGNED) {
//
//            notificationService.sendNotification(
//                    innovation.getCreatedBy().getId(),
//                    "Your Innovation Tittled \"" + innovation.getTitle() + "\" has been " + savedLog.getStateChangedTo().name().toLowerCase() + "",
//                    contentUrlInnovator
//            );
//            if(innovation.getCommittee() !=null){
//                notificationService.sendNotification(
//                        innovation.getCommittee().getId(),
//                        "Innovation Tittled \"" + innovation.getTitle() + "\" has been " + savedLog.getStateChangedTo().name().toLowerCase() + "to You",
//                        contentUrlBoardMember
//                );
//            }
//        }

        return new InnovationAssignmentResponseDTO(
                innovation.getId(),
                innovation.getTitle(),
                innovation.getStatus(),
                innovation.getCommittee(),
                innovation.getAssigner(),
                log.getCreatedDate(),
                log.getConsideration()
        );
    }

    public Page<Innovation> searchInnovations(InnovationSearchCriteriaDTO criteria, int page, int size, String[] sort) {
        Specification<Innovation> spec = InnovationSpecification.filterByCriteria(criteria);
        Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrders(sort)));

        return innovationRepository.findAll(spec, pageable);
    }

    private List<Sort.Order> getSortOrders(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sortOrder : sort) {
            String[] split = sortOrder.split(",");
            if (split.length == 2) {
                orders.add(new Sort.Order(Sort.Direction.fromString(split[1]), split[0]));
            }
        }
        return orders.isEmpty() ? List.of(new Sort.Order(Sort.Direction.ASC, "id")) : orders;
    }

    public Optional<Innovation> getInnovationById(Long id) {
        return innovationRepository.findById(id);
    }

    public void deleteInnovationById(Long id) {
        Optional<Innovation> innovation = innovationRepository.findById(id);
        if (innovation.isPresent()) {
            if (innovation.get().getCommittee() != null) {
                throw new IllegalStateException("This innovation is assigned to a board member and cannot be deleted.");
            }

            innovationRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Innovation with ID " + id + " not found.");
        }
    }
}
