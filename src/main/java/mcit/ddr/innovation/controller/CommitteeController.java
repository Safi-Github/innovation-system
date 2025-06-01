package mcit.ddr.innovation.controller;

import mcit.ddr.innovation.dto.CommitteeDTO;
import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.exception.FileValidationException;
import mcit.ddr.innovation.service.CommitteeFileDownloadService;
import mcit.ddr.innovation.service.CommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/committee")
public class CommitteeController {

    private final CommitteeService committeeService;
    private final ObjectMapper objectMapper;
    private final CommitteeFileDownloadService committeeFileDownloadService;

    public CommitteeController(CommitteeService committeeService,CommitteeFileDownloadService committeeFileDownloadService) {
        this.committeeService = committeeService;
        this.objectMapper = new ObjectMapper();
        this.committeeFileDownloadService = committeeFileDownloadService;
    }


    // @PostMapping("/add")
    // public ResponseEntity<Committee> createCommittee(@RequestBody CommitteeDTO dto) {
    //     Committee saved = committeeService.createCommittee(dto);
    //     return ResponseEntity.ok(saved);
    // }

    @PostMapping(path = "/add",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCommittee(
            @RequestPart("committee") String committeeJson,
            @RequestPart(value = "attachmentFile", required = false) MultipartFile file) {
        try {
            // Validate file type before saving committee
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

            // Convert JSON string to Committee entity
            Committee committee = objectMapper.readValue(committeeJson, Committee.class);

            // Delegate to service to create the committee with or without file attachment
            Committee createdCommittee = committeeService.createCommittee(committee, file);

            return ResponseEntity.ok(createdCommittee);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add committee: " + e.getMessage());
        }
    }

    // @PutMapping("/{id}/name")
    // public ResponseEntity<Committee> updateCommitteeName(@PathVariable Long id, @RequestParam String name) {
    //     Committee updated = committeeService.updateCommitteeName(id, name);
    //     return ResponseEntity.ok(updated);
    // }

    @PatchMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Committee> partialUpdateCommittee(
        @PathVariable Long id,
        @RequestPart("committee" ) CommitteeDTO partialCommitteeUpdateDTO,
        @RequestPart(value = "file", required = false) MultipartFile file) {

            // Optional: Validate file (example: only allow PDF and size < 3MB)
            if (file != null && !file.isEmpty()) {
                String originalFileName = file.getOriginalFilename();
                long fileSize = file.getSize();
                long maxSize = 3 * 1024 * 1024; // 3MB

                if (originalFileName != null && !originalFileName.toLowerCase().endsWith(".pdf")) {
                    throw new FileValidationException("Only PDF files are allowed!");
                }
                if (fileSize > maxSize) {
                    throw new FileValidationException("File size must be less than 3MB!");
                }
            }

        Committee updatedCommittee = committeeService.partialUpdateCommittee(id, partialCommitteeUpdateDTO, file);
        return ResponseEntity.ok(updatedCommittee);
    }

    //open/close a committee
    @PutMapping("/{id}/status")
    public ResponseEntity<Committee> toggleCommitteeStatus(@PathVariable Long id, @RequestParam boolean isClosed) {
        Committee updated = committeeService.setCommitteeClosed(id, isClosed);
        return ResponseEntity.ok(updated);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteCommittee(@PathVariable Long id) {
//        committeeService.deleteCommittee(id);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping
    public List<Committee> getAllCommittees() {
        return committeeService.getAllCommittees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Committee> getCommitteeById(@PathVariable Long id) {
        return ResponseEntity.ok(committeeService.getCommitteeById(id));
    }

    // @GetMapping("/download/{id}")
    // public ResponseEntity<Resource> downloadCommitteeFile(@PathVariable Long id) {
    //     Resource file = committeeFileDownloadService.loadCommitteeFileById(id);
    //     return ResponseEntity.ok()
    //             .header("Content-Disposition", "attachment; filename=\"" + file.getFilename() + "\"")
    //             .body(file);
    // }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadCommitteeFile(@PathVariable Long id) {
        Resource file = committeeFileDownloadService.loadCommitteeFileById(id);

        String filename = file.getFilename(); // Should return the real file name (e.g., فورم اختصاصی صفی الله_1747227173150.pdf)
        String contentType = "application/octet-stream";

        try {
            contentType = Files.probeContentType(Paths.get(file.getFile().getAbsolutePath()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("Sending file: " + filename + ", Content-Type: " + contentType);

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType(contentType))
            .body(file);
    }



}
