package mcit.ddr.innovation.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import mcit.ddr.innovation.enums.PersonType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvolvedPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "First name cannot be null")
    private String firstname;

    private String lastname;
    private String fathername;

    @Column(unique = true)
    private String nid;

    private String phone;

    @Column(unique = true)
    @Email
    private String email;

    private Integer involvedPercentage;

    @Enumerated(EnumType.STRING)
    private PersonType personType;


    // @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "innovation_id", nullable = false)
    // private Innovation innovation;


    @ManyToMany(mappedBy = "involvedPersons")  // This is the inverse side of the relationship
    @JsonIgnoreProperties("involvedPersons")
    private List<Innovation> innovations = new ArrayList<>();

}