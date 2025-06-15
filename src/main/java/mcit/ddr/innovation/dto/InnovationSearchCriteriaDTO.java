package mcit.ddr.innovation.dto;

import lombok.Getter;
import lombok.Setter;
import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.entity.MyUser;
import org.springframework.format.annotation.DateTimeFormat;
import mcit.ddr.innovation.enums.InnovStatus;

import java.util.Date;

@Getter
@Setter
public class InnovationSearchCriteriaDTO {
    private String title;
//    private String category;
    private Long categoryId;
    private InnovStatus status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastModifiedDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date assignedDate;

    // Replacing heavy object with minimal info
    private MyUser assigner;
    private MyUser createdBy;
    private Long committeeId;

}
