package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mcit.ddr.innovation.entity.MyUser;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private MyUser User;
    private String comment;
    private LocalDate commentedAt;
}
