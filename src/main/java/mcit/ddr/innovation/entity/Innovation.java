package mcit.ddr.innovation.entity;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.enums.LiteracyLevel;
import mcit.ddr.innovation.enums.Role;

@Entity
@Table(name = "innovation")
public class Innovation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String tittle;

    @Enumerated(EnumType.STRING)
    private InnovStatus innovStatus;

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
