package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.entity.InnovationHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class InnovationHistoryResponseDTO{

    private Long innov_version_id;

    private String title;
    private String purpose;
    private String description;
    private String additionalInfo;
    private String reasonsProvingYouCanInvent;
    private String impact;
    private String resourcesNeeded;
    private String attachment;

    private LocalDateTime dateCreated;

    // optional: you can hide this if you want
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String archivedInnovationData;

}