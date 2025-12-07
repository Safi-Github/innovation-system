package mcit.ddr.innovation.dto;

import lombok.Data;
import mcit.ddr.innovation.entity.Category;

import java.util.Date;
import java.util.List;

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
    private String attachment;
    private Date createdDate;
    private String innovationIntroduciton;
    private String digitalRequirements;
    private String innovationCategory;
    private Category category;
    private AdminUserDTO assigner;
    private AdminUserDTO createdBy;
    private CommitteeDTO assignedTo;

    private List<InvolvedPersonDTO> involvedPersons;
}
