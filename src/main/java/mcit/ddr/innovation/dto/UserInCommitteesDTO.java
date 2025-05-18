package mcit.ddr.innovation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
public class UserInCommitteesDTO {

    private Long id;

    private String name;

    private Boolean isClosed;

    private LocalDate createdDate;

}
