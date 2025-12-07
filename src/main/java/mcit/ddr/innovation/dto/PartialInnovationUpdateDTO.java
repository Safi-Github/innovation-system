package mcit.ddr.innovation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import mcit.ddr.innovation.entity.Category;

@Data
public class PartialInnovationUpdateDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String purpose;

    private String innovationIntroduciton;
    private String digitalRequirements;
    private String innovationCategory;

    private Category category;

    private String additionalInfo;

    private String reasonsProvingYouCanInvent;

    private String impact;

    private String resourcesNeeded;

    private String attachment; // If the innovator wishes to update the attachment as well
}