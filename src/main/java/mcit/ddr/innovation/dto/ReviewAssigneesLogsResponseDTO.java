package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.enums.InnovStatus;

import java.time.LocalDate;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAssigneesLogsResponseDTO{
    private Long id;
    private InnovStatus stateChangedTo;
    private MyUser createdBy;
    private LocalDate createdDate;
    private String comment;
    private Committee assignedTo;
}
