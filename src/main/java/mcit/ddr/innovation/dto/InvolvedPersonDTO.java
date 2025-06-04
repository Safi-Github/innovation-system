package mcit.ddr.innovation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import mcit.ddr.innovation.enums.PersonType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvolvedPersonDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String fathername;
    private String nid;
    private String phone;
    private String email;
    private Integer involvedPercentage;
    private PersonType personType;
}
