package mcit.ddr.innovation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private String contentUrl;

    private LocalDate createdAt;

    private LocalDate readAt ; // New field to track if notification is read

    @ManyToOne(fetch = FetchType.EAGER) // Change to EAGER
    @JoinColumn(name = "notifying_user_id")
    private MyUser notifyingUser;


    public Notification() {
        this.createdAt = LocalDate
        .now();
    }
}