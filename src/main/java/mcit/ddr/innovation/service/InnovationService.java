package mcit.ddr.innovation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.dto.*;
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

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
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
    private final CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Innovation createInnovation(Innovation innovation, MultipartFile attachmentFile) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String identifier = auth.getName(); // could be email or username

        // Try to find user by username, then email
        Optional<MyUser> userOpt = myUserRepository.findByUsername(identifier);
        if (userOpt.isEmpty()) {
            userOpt = myUserRepository.findByEmail(identifier);
        }
        MyUser currentUser = userOpt.orElseThrow(() -> new RuntimeException("User not found: " + identifier));

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
        if (partialUpdateDTO.getInnovationIntroduciton() != null) {
            innovation.setInnovationIntroduciton(partialUpdateDTO.getInnovationIntroduciton());
        }
        if (partialUpdateDTO.getDigitalRequirements() != null) {
            innovation.setDigitalRequirements(partialUpdateDTO.getDigitalRequirements());
        }
        if (partialUpdateDTO.getInnovationCategory() != null) {
            innovation.setInnovationCategory(partialUpdateDTO.getInnovationCategory());
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
            String email = auth.getName();
//            MyUser loggedInUser = myUserRepository.findByEmail(email)
//                            .orElseThrow(() -> new RuntimeException("User not found: " + email));

                // Lookup by username instead of email
                MyUser loggedInUser = myUserRepository.findByUsernameOrEmail(email, email)
                        .orElseThrow(() -> new RuntimeException("User not found: " + email));



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
        log.setCreatedDate(LocalDateTime.now());
        log.setInnovation(innovation);
        Review savedLog = reviewRepository.save(log);

        //innovation history versioning part commented
        if (savedLog.getStateChangedTo() == InnovStatus.REJECTED) {
            System.out.println(savedLog.getStateChangedTo());
            try {
                // Convert the rejected innovation to JSON
                Map<String, Object> snapshot = new HashMap<>();
                snapshot.put("id", innovation.getId());
                snapshot.put("title", innovation.getTitle());
                snapshot.put("purpose", innovation.getPurpose());
                snapshot.put("category", innovation.getCategory());
                snapshot.put("description", innovation.getDescription());
                snapshot.put("additionalInfo", innovation.getAdditionalInfo());
                snapshot.put("reasonsProvingYouCanInvent", innovation.getReasonsProvingYouCanInvent());
                snapshot.put("impact", innovation.getImpact());
                snapshot.put("resourcesNeeded", innovation.getResourcesNeeded());
                snapshot.put("attachment", innovation.getAttachment());

                String innovationJson = objectMapper.writeValueAsString(snapshot);
                System.out.println(innovationJson);

                InnovationHistory innovHistory = new InnovationHistory();
                innovHistory.setArchivedInnovationData(innovationJson);
                innovHistory.setDateCreated(LocalDateTime.now());
                innovHistory.setReview(savedLog);
                innovHistory.setInnovation(innovation);
                innovationHistoryRepository.save(innovHistory);

            } catch (JsonProcessingException e) {
                logger.error("Failed to convert innovation to JSON for history log", e);
                // Optionally, throw or handle
            }

        }

//      notification part commented
        String contentUrlInnovator = "http://localhost:3000/en/dashboard/innovator/review" + innovation.getId();
        String contentUrlBoardMember = "http://localhost:3000/en/dashboard/board/review" + innovation.getId();
        if (savedLog.getStateChangedTo() == InnovStatus.APPROVED || savedLog.getStateChangedTo() == InnovStatus.REJECTED) {
            notificationService.sendNotification(
                    innovation.getCreatedBy().getId(),
                    "Your Innovation Tittled \"" + innovation.getTitle() + "\" has been " + savedLog.getStateChangedTo().name().toLowerCase() + "",
                    contentUrlInnovator
            );
        }

        if (savedLog.getStateChangedTo() == InnovStatus.RESUBMITTED && innovation.getCommittee() != null) {
            for (CommitteeMember member : innovation.getCommittee().getMembers()) {
                notificationService.sendNotification(
                        member.getUser().getId(),
                        "Innovation titled \"" + innovation.getTitle() + "\" has been " +
                                 "Resubmitted to the committee you are part of.",
                        contentUrlBoardMember
                );
            }
        }


        return new InnovationStatusChangeResponseDTO(
                innovation.getId(),
                innovation.getTitle(),
                innovation.getStatus(),
                stateChangedByUser,
                log.getCreatedDate().toLocalDate(),
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
        log.setCreatedDate(LocalDateTime.now());
        log.setConsideration(consideration);
        log.setAssignedTo(committee);
        log.setInnovation(innovation);
        Review savedLog = reviewRepository.save(log);

//      notification part commented
        String contentUrlInnovator = "http://localhost:3000/en/dashboard/innovator/review/" + innovation.getId();
        String contentUrlBoardMember = "http://localhost:3000/en/dashboard/board/review/" + innovation.getId();
        if (savedLog.getStateChangedTo() == InnovStatus.ASSIGNED) {
            notificationService.sendNotification(
                    innovation.getCreatedBy().getId(),
                    "Your Innovation Tittled \"" + innovation.getTitle() + "\" has been Assigned",
                    contentUrlInnovator
            );
            if(innovation.getCommittee() !=null){
                for (CommitteeMember member : innovation.getCommittee().getMembers()) {
                    notificationService.sendNotification(
                                member.getUser().getId(),
                                "Innovation titled \"" + innovation.getTitle() + "\" has been " +
                                        "Assigned to the committee you are part of.",
                                contentUrlBoardMember
                        );
                    }
                }
        }

        return new InnovationAssignmentResponseDTO(
                innovation.getId(),
                innovation.getTitle(),
                innovation.getStatus().name(),
                committee.getName(),
                assigner.getUsername(),
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

    public Map<String, Long> countInnovationsByStatus() {
        Map<String, Long> result = new HashMap<>();
        for (InnovStatus status : InnovStatus.values()) {
            if (status != InnovStatus.DRAFT) {
                long count = innovationRepository.countByStatus(status);
                result.put(status.name(), count);
            }
        }
        return result;
    }

    public List<InnovationRepository.CategoryInnovationCount> getInnovationCountPerCategory() {
        return innovationRepository.countInnovationsPerCategory();
    }

    //count innovation assigned per committee
    public List<InnovationRepository.CommitteeInnovationCount> getInnovationCountPerCommittee() {
        return innovationRepository.countInnovationsPerCommittee();
    }

    //count innovation assigned for specific committee
    public long countInnovationByCommittee(Long committeeId) {
        return innovationRepository.countByCommitteeId(committeeId);
    }

    public Map<String, Long> countInnovationsByStatusForCommitteeMember() {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<MyUser> userOpt = myUserRepository.findByUsername(identifier);
        if (userOpt.isEmpty()) {
            userOpt = myUserRepository.findByEmail(identifier);
        }

        MyUser user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

        List<Long> committeeIds = committeeMemberRepository.findCommitteeIdsByUserId(user.getId());

        Map<String, Long> counts = new LinkedHashMap<>();

        // Initialize only allowed statuses
        for (InnovStatus status : InnovStatus.values()) {
            if (status != InnovStatus.DRAFT && status != InnovStatus.SUBMITTED) {
                counts.put(status.name(), 0L);
            }
        }

        long totalAssigned = 0;

        if (!committeeIds.isEmpty()) {
            List<Object[]> result = innovationRepository.countByStatusInCommittees(committeeIds);

            for (Object[] row : result) {
                InnovStatus status = (InnovStatus) row[0];
                Long count = (Long) row[1];

                if (status != InnovStatus.DRAFT && status != InnovStatus.SUBMITTED) {
                    counts.put(status.name(), count);
                    totalAssigned += count;
                }
            }
        }

        counts.put("TotalAssigned", totalAssigned);

        return counts;
    }


    // Monthly and yearly report for a specific month and year
    public MonthlyReportDTO getReportByMonthAndYear(int month, int year) {
        MonthlyReportDTO report = new MonthlyReportDTO();
        report.setMonth(Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        report.setYear(year);

        // Initialize statuses (without DRAFT)
        List<String> reportStatuses = List.of("SUBMITTED", "REJECTED", "RESUBMITTED", "ASSIGNED", "APPROVED");
        for (String status : reportStatuses) {
            report.getStatusCounts().put(status, 0L);
        }

        // Fetch actual status counts for that month and year
        List<Object[]> statusData = innovationRepository.countByStatus(month, year);
        for (Object[] row : statusData) {
            String status = row[0].toString();
            Long count = (Long) row[1];

            if ("DRAFT".equals(status)) {
                continue; // skip DRAFT
            } else if ("ASSIGNED".equals(status)) {
                report.getStatusCounts().put("PENDING", count);
            } else {
                report.getStatusCounts().put(status, count);
            }
        }

        // Initialize categories with 0 count
        List<String> allCategories = categoryRepository.findAllCategoryNames();
        for (String category : allCategories) {
            report.getCategoryCounts().put(category, 0L);
        }

        // Fill actual category counts
        List<Object[]> categoryData = innovationRepository.countByCategory(month, year);
        for (Object[] row : categoryData) {
            String category = row[0].toString();
            Long count = (Long) row[1];
            report.getCategoryCounts().put(category, count);
        }

        return report;
    }

    // Report for a whole year (all months combined)
    public MonthlyReportDTO getReportByYear(int year) {
        MonthlyReportDTO report = new MonthlyReportDTO();
        report.setYear(year);
        report.setMonth("ALL");

        // Initialize statuses (without DRAFT)
        List<String> reportStatuses = List.of("SUBMITTED", "REJECTED", "RESUBMITTED", "ASSIGNED", "APPROVED");
        for (String status : reportStatuses) {
            report.getStatusCounts().put(status, 0L);
        }

        // Fetch status counts for whole year
        List<Object[]> statusData = innovationRepository.countByStatusYear(year);
        for (Object[] row : statusData) {
            String status = row[0].toString();
            Long count = (Long) row[1];

            if ("DRAFT".equals(status)) {
                continue; // skip DRAFT
            } else if ("ASSIGNED".equals(status)) {
                report.getStatusCounts().put("PENDING", count);
            } else {
                report.getStatusCounts().put(status, count);
            }
        }

        // Initialize categories with 0 count
        List<String> allCategories = categoryRepository.findAllCategoryNames();
        for (String category : allCategories) {
            report.getCategoryCounts().put(category, 0L);
        }

        // Fill actual category counts for the year
        List<Object[]> categoryData = innovationRepository.countByCategoryYear(year);
        for (Object[] row : categoryData) {
            String category = row[0].toString();
            Long count = (Long) row[1];
            report.getCategoryCounts().put(category, count);
        }

        return report;
    }

}
