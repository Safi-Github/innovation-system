package mcit.ddr.innovation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mcit.ddr.innovation.dto.ReviewAssigneesLogsResponseDTO;
import mcit.ddr.innovation.dto.ReviewLogsResponseDTO;
import mcit.ddr.innovation.entity.Review;
import mcit.ddr.innovation.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // public List<ReviewAssigneesLogsResponseDTO> getAssignedReviews(Long innovationId) {
    //     return reviewRepository.findByInnovationIdAndAssignedToIsNotNull(innovationId);
    // }  
    
    public List<ReviewLogsResponseDTO> getReviews(Long innovationId) {
        return reviewRepository.findReviewLogs(innovationId);
    }

    public List<ReviewAssigneesLogsResponseDTO> getAssignedReviews(Long innovationId) {
        return reviewRepository.findReviewAssignedLogs(innovationId);
    }

}
