package mcit.ddr.innovation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import mcit.ddr.innovation.dto.InnovationHistoryResponseDTO;
import mcit.ddr.innovation.dto.ReviewAssigneesLogsResponseDTO;
import mcit.ddr.innovation.dto.ReviewLogsResponseDTO;
import mcit.ddr.innovation.entity.Review;
import mcit.ddr.innovation.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ObjectMapper objectMapper; // from Jackson

    // public List<ReviewAssigneesLogsResponseDTO> getAssignedReviews(Long innovationId) {
    //     return reviewRepository.findByInnovationIdAndAssignedToIsNotNull(innovationId);
    // }  
    
    public List<ReviewLogsResponseDTO> getReviews(Long innovationId) {
        List<Review> reviews = reviewRepository.findReviewLogs(innovationId);

        return reviews.stream()
            .map(r -> {
                InnovationHistoryResponseDTO parsedData = null;
                try {
                    if (r.getInnovationHistory() != null && r.getInnovationHistory().getArchivedInnovationData() != null) {
                        parsedData = objectMapper.readValue(
                            r.getInnovationHistory().getArchivedInnovationData(),
                            InnovationHistoryResponseDTO.class
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return new ReviewLogsResponseDTO(
    r.getId(),
    r.getStateChangedTo(),
    r.getCreatedBy(),
    r.getCreatedDate(),
    r.getComment(),
    r.getInnovationHistory() != null && parsedData != null
        ? new InnovationHistoryResponseDTO(
            r.getInnovationHistory().getId(),
            parsedData.getTitle(),
            parsedData.getPurpose(),
            parsedData.getCategory(),
            parsedData.getDescription(),
            parsedData.getAdditionalInfo(),
            parsedData.getReasonsProvingYouCanInvent(),
            parsedData.getImpact(),
            parsedData.getResourcesNeeded(),
            parsedData.getAttachment(),
            r.getInnovationHistory().getDateCreated(),
            null // don't include archivedInnovationData in the response
        )
        : null
);

            })
            .collect(Collectors.toList());
    }

    public List<ReviewAssigneesLogsResponseDTO> getAssignedReviews(Long innovationId) {
        return reviewRepository.findReviewAssignedLogs(innovationId);
    }

}
