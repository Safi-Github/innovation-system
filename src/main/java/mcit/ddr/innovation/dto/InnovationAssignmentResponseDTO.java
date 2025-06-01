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
public class InnovationAssignmentResponseDTO {
    private Long innovationId;
    private String innovationTitle;
    private String status;
    private String committeeName;
    private String assignedBy;
    private String consideration;

    public InnovationAssignmentResponseDTO(Long innovationId, String innovationTitle, String status, String committeeName, String assignedBy, String consideration) {
        this.innovationId = innovationId;
        this.innovationTitle = innovationTitle;
        this.status = status;
        this.committeeName = committeeName;
        this.assignedBy = assignedBy;
        this.consideration = consideration;
    }
}

