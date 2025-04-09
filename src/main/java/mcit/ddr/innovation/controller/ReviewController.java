package mcit.ddr.innovation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mcit.ddr.innovation.dto.ReviewAssigneesLogsResponseDTO;
import mcit.ddr.innovation.dto.ReviewLogsResponseDTO;
import mcit.ddr.innovation.entity.Review;
import mcit.ddr.innovation.service.ReviewService;

@RestController
@RequestMapping("/api/innovation")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

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
}