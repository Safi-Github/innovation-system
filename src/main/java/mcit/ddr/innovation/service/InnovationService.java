package mcit.ddr.innovation.service;

import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.dto.InnovationAssignmentResponseDTO;
// import mcit.ddr.innovation.dto.InnovationDTO;
import mcit.ddr.innovation.dto.InnovationSearchCriteriaDTO;
import mcit.ddr.innovation.dto.InnovationStatusChangeResponseDTO;
import mcit.ddr.innovation.dto.PartialInnovationUpdateDTO;
import mcit.ddr.innovation.entity.Innovation;
// import mcit.ddr.innovation.entity.InnovationHistory;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.Review;
// import mcit.ddr.innovation.enums.Category;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.enums.Role;
import mcit.ddr.innovation.exception.ResourceNotFoundException;
import mcit.ddr.innovation.enums.InnovStatus;
// import mcit.ddr.innovation.repository.InnovationHistoryRepository;
import mcit.ddr.innovation.repository.InnovationRepository;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.repository.ReviewRepository;
import mcit.ddr.innovation.service.FileStorageService;
import mcit.ddr.innovation.specification.InnovationSpecification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private final FileStorageService fileStorageService;
    // private final InnovationHistoryRepository innovationHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Innovation createInnovation(Innovation innovation, MultipartFile attachmentFile) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        MyUser currentUser = myUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        innovation.setCreateDate(new Date());
        // innovation.setLastModifiedDate(new Date());
        innovation.setIsAssigned(false);
        innovation.setStatus(InnovStatus.DRAFT);
        innovation.setCreatedBy(currentUser);

        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            String filePath = fileStorageService.saveFile(attachmentFile);
            innovation.setAttachment(filePath); // Set file path to bill object
        }
        return innovationRepository.save(innovation);
    }

    // partial update the innovation
    public Innovation partialUpdateInnovation(Long innovationId, PartialInnovationUpdateDTO partialUpdateDTO, MultipartFile attachmentFile) {
        Innovation innovation = innovationRepository.findById(innovationId)
                .orElseThrow(() -> new ResourceNotFoundException("Innovation not found"));

        // Update only the fields present in the DTO, excluding status
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

        // If a new attachment file is provided, handle the file upload
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            String filePath = fileStorageService.saveFile(attachmentFile); // Use your existing file storage service
            innovation.setAttachment(filePath); // Update the file path in the Innovation entity
        }

        // Save the updated innovation object
        return innovationRepository.save(innovation);
    }


    //innovation status change service
    @Transactional
    public InnovationStatusChangeResponseDTO updateInnovationStatus(Long id, Map<String, String> payload) {
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
        Review savedLog = reviewRepository.save(log);

        if (savedLog.getStateChangedTo() == InnovStatus.REJECTED) {
            System.out.println(savedLog.getStateChangedTo());

        }

        // Return the InnovationDTO with the required fields
        return new InnovationStatusChangeResponseDTO(
            innovation.getId(),
            innovation.getTitle(),
            innovation.getStatus(),
            stateChangedByUser,  // state changed by the user
            log.getCreatedDate(),  // created on date
            log.getComment()
        );
    }


    //innovation assignment service
    @Transactional
    public InnovationAssignmentResponseDTO assignInnovation(Long innovationId,Long assignerId, Long boardMemberId, String status,String comment) {
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
        log.setCreatedBy(assigner);
        log.setCreatedDate(LocalDate.now());
        log.setComment(comment);
        log.setAssignedTo(boardMember);
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

        // Return assignment response DTO
        return new InnovationAssignmentResponseDTO(
            innovation.getId(),
            innovation.getTitle(),
            innovation.getStatus(),
            innovation.getBoardMember(),
            innovation.getAssigner(),
            log.getCreatedDate(),
            log.getComment()
        );
    }

    // public List<Innovation> getAllInnovations() {
    //     return innovationRepository.findAll();
    // }

    // public List<Innovation> searchInnovations(
    //         Category category, InnovStatus status, Date createDate, Date lastModifiedDate,
    //         Date assignedDate, MyUser assigner, MyUser boardMember, MyUser createdBy) {

    //     Specification<Innovation> spec = InnovationSpecification.filterByCriteria(
    //             category, status, createDate, lastModifiedDate, assignedDate, assigner, boardMember, createdBy);

    //     return innovationRepository.findAll(spec);
    // }

    public Page<Innovation> searchInnovations(
            InnovationSearchCriteriaDTO criteria,int page, int size, String[] sort)
    {
        Specification<Innovation> spec = InnovationSpecification.filterByCriteria(criteria);
        Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrders(sort)));
        
        return innovationRepository.findAll(spec, pageable);
    }

    //Sorting Helper Method
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
            // Check if boardMemberId is assigned
            if (innovation.get().getBoardMember() != null) {
                throw new IllegalStateException("This innovation is assigned to a board member and cannot be deleted.");
            }
        
            // If no board member is assigned, proceed with deletion
            innovationRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Innovation with ID " + id + " not found.");
        }
    }
}