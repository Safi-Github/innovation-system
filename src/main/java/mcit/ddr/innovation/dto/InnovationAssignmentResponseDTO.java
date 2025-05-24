package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.entity.MyUser;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InnovationAssignmentResponseDTO {
    private Long id;
    private String tittle;
    private InnovStatus stateChangedTo;
    private Committee committee;   // Assigned to Committee
    private MyUser createdBy;    // Assigner
    private LocalDate createdDate; // Date of the assignment
    private String consideration;
}
