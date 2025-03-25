package mcit.ddr.innovation.controller;

import mcit.ddr.innovation.dto.InnovationDTO;
import mcit.ddr.innovation.dto.PartialInnovationUpdateDTO;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.Review;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.repository.InnovationRepository;
import mcit.ddr.innovation.repository.ReviewRepository;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.exception.FileStorageException;
import mcit.ddr.innovation.exception.FileValidationException;
import mcit.ddr.innovation.exception.ResourceNotFoundException;
import mcit.ddr.innovation.service.FileDownloadService;
import mcit.ddr.innovation.service.InnovationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import mcit.ddr.innovation.service.SecurityUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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

    public InnovationController(InnovationRepository innovationRepository,ReviewRepository reviewRepository,MyUserRepository myUserRepository, InnovationService innovationService,FileDownloadService fileDownloadService) {
        this.innovationRepository = innovationRepository;
        this.reviewRepository = reviewRepository;
        this.myUserRepository = myUserRepository;
        this.innovationService = innovationService;
        this.fileDownloadService = fileDownloadService;
    }

    // add innovation
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createInnovation(@Valid @RequestPart("innovation") Innovation innovation,
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
        Innovation createdInnovation = innovationService.createInnovation(innovation,attachmentFile);
        return ResponseEntity.ok(createdInnovation);
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

    // assign innovation api
    @PutMapping("/assign/{id}")
    public ResponseEntity<Innovation> assignInnovation(@PathVariable Long id,@RequestBody Map<String, String> payload ) {
        Long assignerId = Long.parseLong(payload.get("assignerId"));
        Long boardMemberId = Long.parseLong(payload.get("boardMemberId"));
        String statusValue = payload.get("status");
        String comment = payload.get("comment");

        Innovation updatedInnovation = innovationService.assignInnovation(id, assignerId, boardMemberId, statusValue, comment);
        return ResponseEntity.ok(updatedInnovation);
    }

    //innovation status changing api
    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateInnovationStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        InnovationDTO responseDTO = innovationService.updateInnovationStatus(id, payload);
            return ResponseEntity.ok(responseDTO);
    }

    // get all innovation
    @GetMapping
    public ResponseEntity<List<Innovation>> getAllInnovations() {
        return ResponseEntity.ok(innovationService.getAllInnovations());
    }

    // Get specific innovation
    @GetMapping("/{id}")
    public ResponseEntity<Innovation> getInnovationById(@PathVariable Long id) {
        return innovationService.getInnovationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Download the innovation attachment
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

    // Delete the innovation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvolvedPerson(@PathVariable Long id) {
        innovationService.deleteInnovation(id);
        return ResponseEntity.noContent().build();
    }
}
