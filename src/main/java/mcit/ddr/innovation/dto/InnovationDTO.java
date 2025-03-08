package mcit.ddr.innovation.dto;

import mcit.ddr.innovation.enums.InnovStatus;
import jakarta.validation.constraints.NotBlank;

public class InnovationDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String tittle;

    private InnovStatus innovStatus;

    // Constructors
    public InnovationDTO() {}

    public InnovationDTO(Long id, String tittle, InnovStatus innovStatus) {
        this.id = id;
        this.tittle = tittle;
        this.innovStatus = innovStatus;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public InnovStatus getInnovStatus() {
        return innovStatus;
    }

    public void setInnovStatus(InnovStatus innovStatus) {
        this.innovStatus = innovStatus;
    }
}
