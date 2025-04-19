package mcit.ddr.innovation.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import mcit.ddr.innovation.enums.LiteracyLevel;
import mcit.ddr.innovation.enums.Role;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotBlank
    private String fathername;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String nid;

    @NotBlank
    private String phone;

    @Enumerated(EnumType.STRING)
    private LiteracyLevel literacyLevel;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @Column(name = "profile_image")
    private String profileImage;

//    @Column(columnDefinition = "boolean default false")
//    private Boolean isEmailVerified = false;
//
//    private String emailVerificationToken;



}
