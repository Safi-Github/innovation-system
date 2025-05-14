package mcit.ddr.innovation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.dto.InnovationAssignmentResponseDTO;
import mcit.ddr.innovation.dto.InnovationPaginatedResponseDTO;
import mcit.ddr.innovation.dto.InnovationSearchCriteriaDTO;
import mcit.ddr.innovation.dto.InnovationStatusChangeResponseDTO;
import mcit.ddr.innovation.dto.PartialInnovationUpdateDTO;
import mcit.ddr.innovation.entity.Innovation;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                if (originalFileName != null && !originalFileName.toLowerCase().endsWith(".pdf")) {
                    throw new FileValidationException("Only PDF files are allowed!");
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
        String comment = payload.get("comment");

        InnovationAssignmentResponseDTO updatedInnovation = innovationService.assignInnovation(id, assignerId, committeeId, statusValue, comment);
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
    public ResponseEntity<InnovationPaginatedResponseDTO> searchInnovations(
        InnovationSearchCriteriaDTO criteria, // Using DTO
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "id,asc") String[] sort) {

        Page<Innovation> result = innovationService.searchInnovations(criteria, page, size, sort);
        
        InnovationPaginatedResponseDTO<Innovation> response = new InnovationPaginatedResponseDTO<>(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext(),
            result.hasPrevious()
        );
    
        return ResponseEntity.ok(response);
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


}