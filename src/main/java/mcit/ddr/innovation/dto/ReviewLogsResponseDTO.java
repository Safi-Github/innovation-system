package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.entity.InnovationHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLogsResponseDTO{
    private Long id;
    private InnovStatus stateChangedTo;
    private MyUser createdBy;
    private LocalDateTime createdDate;
    private String consideration;
    private Committee AssignedTo;
    @JsonProperty("previous_submission")
    private InnovationHistoryResponseDTO innovationHistoryResponseDTO;
}