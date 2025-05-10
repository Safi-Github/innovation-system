package mcit.ddr.innovation.dto;

import lombok.Data;

@Data
public class CommitteeMemberDTO {
    private Long userId;
    private Long committeeId;
    private Boolean isHead;

}
