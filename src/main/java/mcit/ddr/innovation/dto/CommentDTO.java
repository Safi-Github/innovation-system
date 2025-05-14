package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CommentDTO {
    private String comment;
    private LocalDate commentedAt;
}
