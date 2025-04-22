package mcit.ddr.innovation.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "innovation_history")
public class InnovationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "archived_innovation_data", columnDefinition = "TEXT")
    private String archivedInnovationData; // Stores JSON of rejected innovation

    @Column(name = "date_created")
    private LocalDate dateCreated;

    @OneToOne
    @JoinColumn(name = "review_id")
    @JsonIgnoreProperties("InnovationHistory")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "innovation_id")
    @JsonIgnoreProperties("InnovationHistory") // optional if bidirectional
    private Innovation innovation;
}
