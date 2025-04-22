package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InnovationHistoryResponseDTO{
    
    private Long id;
    private String title;
    private String purpose;
    private String category;
    private String description;
    private String additionalInfo;
    private String reasonsProvingYouCanInvent;
    private String impact;
    private String resourcesNeeded;
    private String attachment;

}