package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.entity.MyUser;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InnovationStatusChangeResponseDTO {
    private Long id;
    
    private String tittle;
    
    private InnovStatus innovStatus;
    
    private MyUser createdBy;  // The user who created the innovation (assigner)
    
    // private MyUser assignedTo; // The board member (assigned person)

    private LocalDate createdDate; // The date of the assignment
    private String comment;

}
