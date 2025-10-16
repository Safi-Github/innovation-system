package mcit.ddr.innovation.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Data
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment", nullable = false, columnDefinition = "Text")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime commentedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "innovation_id", nullable = false)
    private Innovation innovation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user; // Must be Committee Member (enforced in service layer)
}