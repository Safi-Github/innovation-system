package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.entity.InnovationHistory;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLogsResponseDTO{
    private Long id;
    private InnovStatus stateChangedTo;
    private MyUser createdBy;
    private LocalDate createdDate;
    private String consideration;
    @JsonProperty("previous_submission")
    private InnovationHistoryResponseDTO innovationHistoryResponseDTO;
}