package mcit.ddr.innovation.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import mcit.ddr.innovation.dto.ReviewAssigneesLogsResponseDTO;
import mcit.ddr.innovation.dto.ReviewLogsResponseDTO;
import mcit.ddr.innovation.entity.Review;
import mcit.ddr.innovation.exception.FileStorageException;
import mcit.ddr.innovation.repository.InnovationRepository;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.repository.ReviewRepository;
import mcit.ddr.innovation.service.PVFileDownloadService;
import mcit.ddr.innovation.service.FileDownloadService;
import mcit.ddr.innovation.service.InnovationService;
import mcit.ddr.innovation.service.ReviewService;

@RestController
@RequestMapping("/api/innovation")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    private PVFileDownloadService pvfileDownloadService;


    @Autowired  // Ensure this is here
    public ReviewController(PVFileDownloadService pvfileDownloadService) {
        this.pvfileDownloadService = pvfileDownloadService;
    }

    @GetMapping("/{innovationId}/reviews")
    public ResponseEntity<List<ReviewLogsResponseDTO>> getReviews(@PathVariable Long innovationId) {
        List<ReviewLogsResponseDTO> reviews = reviewService.getReviews(innovationId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{innovationId}/reviews/assigned")
    public ResponseEntity<List<ReviewAssigneesLogsResponseDTO>> getAssignedReviews(@PathVariable Long innovationId) {
        List<ReviewAssigneesLogsResponseDTO> reviews = reviewService.getAssignedReviews(innovationId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/previous/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Resource file = pvfileDownloadService.loadFileById(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
    
}