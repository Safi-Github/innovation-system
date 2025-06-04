package mcit.ddr.innovation.dto;

import lombok.Data;
import mcit.ddr.innovation.entity.Category;

import java.util.Date;

@Data
public class InnovationResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String purpose;
    private String additionalInfo;
    private String status;
    private String reasonsProvingYouCanInvent;
    private String impact;
    private String resourcesNeeded;

    private Boolean isAssigned;
    private Date assignedDate;

    private Category category;
    private AdminUserDTO assigner;
    private AdminUserDTO createdBy;
    private CommitteeDTO assignedTo;
}
