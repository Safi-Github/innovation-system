package mcit.ddr.innovation.dto;

import mcit.ddr.innovation.entity.MyUser;
// import mcit.ddr.innovation.enums.Category;
import mcit.ddr.innovation.enums.InnovStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Getter
@Setter
public class InnovationSearchCriteriaDTO {
    private String category;
    private InnovStatus status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastModifiedDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date assignedDate;

    private MyUser assigner;
    private MyUser boardMember;
    private MyUser createdBy;
}
