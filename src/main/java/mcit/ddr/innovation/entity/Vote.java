package mcit.ddr.innovation.entity;

import jakarta.persistence.*;
import lombok.Data;
import mcit.ddr.innovation.enums.VoteDecision;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "vote", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "innovation_id"})
})
public class Vote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false)
    private VoteDecision decision;

    @Column(name = "voted_at", nullable = false)
    private LocalDate votedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "innovation_id", nullable = false)
    private Innovation innovation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user; // Must be Committee Member (enforced in service layer)
}
