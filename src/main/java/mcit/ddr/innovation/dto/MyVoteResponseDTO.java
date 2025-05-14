package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mcit.ddr.innovation.enums.VoteDecision;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MyVoteResponseDTO {
    private VoteDecision decision;
    private LocalDate votedAt;
}
