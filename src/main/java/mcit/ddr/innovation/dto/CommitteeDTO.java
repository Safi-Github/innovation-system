package mcit.ddr.innovation.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommitteeDTO {


    private Long id;
    private String name;
    private String description;
    private List<AdminUserDTO> members;

    // private Long createdById; // ID of the user creating the committee
}
