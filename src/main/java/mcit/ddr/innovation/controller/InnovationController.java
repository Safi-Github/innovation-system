package mcit.ddr.innovation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.dto.*;
import mcit.ddr.innovation.entity.*;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.repository.InnovationRepository;
import mcit.ddr.innovation.repository.ReviewRepository;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.exception.FileStorageException;
import mcit.ddr.innovation.exception.FileValidationException;
import mcit.ddr.innovation.service.FileDownloadService;
import mcit.ddr.innovation.service.InnovationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/innovation")
public class InnovationController {
    private static final Logger logger = LoggerFactory.getLogger(InnovationController.class);

    @Autowired
    private InnovationRepository innovationRepository;
    private ReviewRepository reviewRepository;
    private MyUserRepository myUserRepository;
    private final InnovationService innovationService;
    private final FileDownloadService fileDownloadService;
    private final ObjectMapper objectMapper;


    public InnovationController(InnovationRepository innovationRepository,ReviewRepository reviewRepository,MyUserRepository myUserRepository, InnovationService innovationService,FileDownloadService fileDownloadService,ObjectMapper objectMapper) {
        this.innovationRepository = innovationRepository;
        this.reviewRepository = reviewRepository;
        this.myUserRepository = myUserRepository;
        this.innovationService = innovationService;
        this.fileDownloadService = fileDownloadService;
        this.objectMapper = objectMapper;
    }
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> createInnovation(@Valid @RequestPart("innovation") Innovation innovation,
//        @RequestPart(value = "attachmentFile", required = false) MultipartFile attachmentFile) {
//        // Validate file type before saving innovation
//        if (attachmentFile != null && !attachmentFile.isEmpty()) {
//            String originalFileName = attachmentFile.getOriginalFilename();
//            long fileSize = attachmentFile.getSize();
//            long maxSize = 3 * 1024 * 1024; // 3 MB in bytes
//
//            // Ensure the file has a .pdf extension (case insensitive)
//            if (originalFileName != null && !originalFileName.toLowerCase().endsWith(".pdf")) {
//                throw new FileValidationException("Only PDF files are allowed!");
//            }
//            // Ensure the file size is less than 3MB
//            if (fileSize > maxSize) {
//                throw new FileValidationException("File size must be less than 3MB!");
//            }
//        }
//        Innovation createdInnovation = innovationService.createInnovation(innovation,attachmentFile);
//        return ResponseEntity.ok(createdInnovation);
//    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addInnovation(
            @RequestPart("innovation") String innovationJson,
            @RequestPart(value = "attachmentFile", required = false) MultipartFile file
    ) {
        try {
            // Validate file type before saving innovation
            if (file != null && !file.isEmpty()) {
                String originalFileName = file.getOriginalFilename();
                long fileSize = file.getSize();
                long maxSize = 3 * 1024 * 1024; // 3 MB in bytes

                // Ensure the file has a .pdf extension (case insensitive)
                if (originalFileName != null && !originalFileName.toLowerCase().matches(".*\\.(pdf|docx|png|jpeg|jpg)$")) {
                    throw new FileValidationException("Only PDF, DOCX, PNG, JPEG, and JPG files are allowed!");
                }
                // Ensure the file size is less than 3MB
                if (fileSize > maxSize) {
                    throw new FileValidationException("File size must be less than 3MB!");
                }
            }

            // Convert JSON string to Innovation entity
            Innovation innovation = objectMapper.readValue(innovationJson, Innovation.class);

            // Delegate to service
            Innovation createdInnovation = innovationService.createInnovation(innovation, file);
            return ResponseEntity.ok(createdInnovation);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add innovation: " + e.getMessage());
        }
    }

    // partial innovation update endpoint
    @PatchMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> partialUpdateInnovation(
            @PathVariable Long id,
            @RequestPart("innovation") PartialInnovationUpdateDTO partialUpdateDTO,
            @RequestPart(value = "attachmentFile", required = false) MultipartFile attachmentFile) {
        // Validate file type before saving innovation
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            String originalFileName = attachmentFile.getOriginalFilename();
            long fileSize = attachmentFile.getSize();
            long maxSize = 3 * 1024 * 1024; // 3 MB in bytes

            // Ensure the file has a .pdf extension (case insensitive)
            if (originalFileName != null && !originalFileName.toLowerCase().endsWith(".pdf")) {
                throw new FileValidationException("Only PDF files are allowed!");
            }
            // Ensure the file size is less than 3MB
            if (fileSize > maxSize) {
                throw new FileValidationException("File size must be less than 3MB!");
            }
        }

        // Call the service method to perform the partial update, including handling file upload
        Innovation updatedInnovation = innovationService.partialUpdateInnovation(id, partialUpdateDTO, attachmentFile);
        return ResponseEntity.ok(updatedInnovation);
    }

    

    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/assign/{id}")
    public ResponseEntity<InnovationAssignmentResponseDTO> assignInnovation(@PathVariable Long id,@RequestBody Map<String, String> payload ) {
        Long assignerId = Long.parseLong(payload.get("assignerId"));
        Long committeeId = Long.parseLong(payload.get("committeeId"));
        String statusValue = payload.get("status");
        String consideration = payload.get("consideration");

        InnovationAssignmentResponseDTO updatedInnovation = innovationService.assignInnovation(id, assignerId, committeeId, statusValue, consideration);
        return ResponseEntity.ok(updatedInnovation);
    }

    //innovation status changing api
    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateInnovationStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        InnovationStatusChangeResponseDTO responseDTO = innovationService.updateInnovationStatus(id, payload);
            return ResponseEntity.ok(responseDTO);
    }

    // @GetMapping
    // public ResponseEntity<List<Innovation>> getAllInnovations() {
    //     return ResponseEntity.ok(innovationService.getAllInnovations());
    // }

    //Innovation Search Criteria Without Pagination
    // @GetMapping
    // public List<Innovation> searchInnovations(
    //         @RequestParam(required = false) Category category,
    //         @RequestParam(required = false) InnovStatus status,
    //         @RequestParam(required = false) Date createDate,
    //         @RequestParam(required = false) Date lastModifiedDate,
    //         @RequestParam(required = false) Date assignedDate,
    //         @RequestParam(required = false) MyUser assigner,
    //         @RequestParam(required = false) MyUser boardMember,
    //         @RequestParam(required = false) MyUser createdBy) {

    //     return innovationService.searchInnovations(
    //             category, status, createDate, lastModifiedDate, assignedDate, assigner, boardMember, createdBy);
    // }

    @GetMapping
    public ResponseEntity<InnovationPaginatedResponseDTO<InnovationResponseDTO>> searchInnovations(
            InnovationSearchCriteriaDTO criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Page<Innovation> result = innovationService.searchInnovations(criteria, page, size, sort);

        List<InnovationResponseDTO> dtoList = result.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        InnovationPaginatedResponseDTO<InnovationResponseDTO> response = new InnovationPaginatedResponseDTO<>(
                dtoList,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext(),
                result.hasPrevious()
        );

        return ResponseEntity.ok(response);
    }


    private InnovationResponseDTO mapToDTO(Innovation innovation) {
        InnovationResponseDTO dto = new InnovationResponseDTO();
        dto.setId(innovation.getId());
        dto.setTitle(innovation.getTitle());
        dto.setDescription(innovation.getDescription());
        dto.setPurpose(innovation.getPurpose());
        dto.setAdditionalInfo(innovation.getAdditionalInfo());
        dto.setStatus(innovation.getStatus().toString());
        dto.setReasonsProvingYouCanInvent(innovation.getReasonsProvingYouCanInvent());
        dto.setImpact(innovation.getImpact());
        dto.setResourcesNeeded(innovation.getResourcesNeeded());
        dto.setIsAssigned(innovation.getIsAssigned());
        dto.setAssignedDate(innovation.getAssignedDate());
        dto.setAttachment(innovation.getAttachment());
        dto.setCreatedDate(innovation.getCreateDate());
        List<InvolvedPersonDTO> involvedPersonDTOs = innovation.getInvolvedPersons().stream().map(person -> {
            InvolvedPersonDTO dto1 = new InvolvedPersonDTO();
            dto1.setId(person.getId());
            dto1.setFirstname(person.getFirstname());
            dto1.setFathername(person.getFathername());
            dto1.setNid(person.getNid());
            dto1.setLastname(person.getLastname());
            dto1.setPhone(person.getPhone());
            dto1.setEmail(person.getEmail());
            dto1.setPersonType(person.getPersonType());
            dto1.setInvolvedPercentage(person.getInvolvedPercentage());
            return dto1;
        }).collect(Collectors.toList());

        dto.setInvolvedPersons(involvedPersonDTOs);

        if (innovation.getCategory() != null) {
            Category catDto = new Category();
            catDto.setId(innovation.getCategory().getId());
            catDto.setName(innovation.getCategory().getName());
            dto.setCategory(catDto);
        }

        dto.setAssigner(mapUserToAdminDTO(innovation.getAssigner()));
        dto.setCreatedBy(mapUserToAdminDTO(innovation.getCreatedBy()));

        if (innovation.getCommittee() != null) {
            CommitteeDTO committeeDto = new CommitteeDTO();
            committeeDto.setId(innovation.getCommittee().getId());
            committeeDto.setName(innovation.getCommittee().getName());
            committeeDto.setDescription(innovation.getCommittee().getDescription());

            List<AdminUserDTO> memberDTOs = innovation.getCommittee().getMembers().stream()
                    .map(member -> {
                        MyUser user = member.getUser();
                        if (user == null) return null;

                        AdminUserDTO dtoMember = mapUserToAdminDTO(user);
                        dtoMember.setIsHead(member.getIsHead());
                        return dtoMember;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            committeeDto.setMembers(memberDTOs);
            dto.setAssignedTo(committeeDto);
        }
        return dto;
    }

    private AdminUserDTO mapUserToAdminDTO(MyUser user) {
        if (user == null) return null;

        AdminUserDTO dto = new AdminUserDTO(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getFathername(),
                user.getNid(),
                user.getPhone(),
                user.getLiteracyLevel(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );

        // isHead is set externally (from CommitteeMember)
        return dto;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Innovation> getInnovationById(@PathVariable Long id) {
        return innovationService.getInnovationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInnovationById(@PathVariable Long id) {
        innovationService.deleteInnovationById(id);
        return ResponseEntity.noContent().build();
    }

   @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id, HttpServletRequest request) {
        try {
            // Load file as Resource
            Resource resource = fileDownloadService.loadFileById(id);

            // Determine file's content type
            String contentType = "application/octet-stream"; // Default to binary data
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                // Fallback to default content type
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (FileStorageException ex) {
            // Return 404 with custom error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + ex.getMessage() + "\"}");
        } catch (Exception ex) {
            // Return 500 with generic error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An unexpected error occurred.\"}");
        }
    }

    //count innovation assigned per each committee
    @GetMapping("/innovationAssignedPerCommittee")
    public ResponseEntity<List<Map<String, Object>>> getInnovationCountPerCommittee() {
        List<InnovationRepository.CommitteeInnovationCount> data = innovationService.getInnovationCountPerCommittee();

        List<Map<String, Object>> response = data.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("committeeId", d.getCommitteeId());
            map.put("committeeName", d.getCommitteeName());
            map.put("totalAssignedInnovation", d.getInnovationCount());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    //count innovation assigned for specific committee
    @GetMapping("/committees/{committeeId}/innovation-count")
    public ResponseEntity<Long> getInnovationCountByCommittee(@PathVariable Long committeeId) {
        long count = innovationService.countInnovationByCommittee(committeeId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-by-status")
    public ResponseEntity<Map<String, Long>> countInnovationsByStatus() {
        Map<String, Long> counts = innovationService.countInnovationsByStatus();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/innovationPerCategory")
    public ResponseEntity<List<Map<String, Object>>> getInnovationCountPerCategory() {
        List<InnovationRepository.CategoryInnovationCount> data = innovationService.getInnovationCountPerCategory();

        List<Map<String, Object>> response = data.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("categoryName", d.getCategoryName());
            map.put("totalInnovation", d.getInnovationCount());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/board-status-count")
    public ResponseEntity<Map<InnovStatus, Long>> getInnovationCountsByStatus() {
        Map<InnovStatus, Long> result = innovationService.countInnovationsByStatusForBoardMember();
        return ResponseEntity.ok(result);
    }

}