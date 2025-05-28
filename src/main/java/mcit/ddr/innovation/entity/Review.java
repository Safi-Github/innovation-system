package mcit.ddr.innovation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import mcit.ddr.innovation.enums.InnovStatus;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InnovStatus stateChangedTo;

    @ManyToOne
    @JoinColumn(name = "created_id")
    private MyUser createdBy;

    private LocalDate createdDate;

    private String consideration;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id", nullable = true)
    private Committee assignedTo;

    @ManyToOne
    @JoinColumn(name = "innovation_id")
    private Innovation innovation;
    
    @OneToOne(mappedBy = "review", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("review")
    private InnovationHistory innovationHistory;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InnovStatus getStateChangedTo() {
        return stateChangedTo;
    }

    public void setStateChangedTo(InnovStatus stateChangedTo) {
        this.stateChangedTo = stateChangedTo;
    }

    public MyUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(MyUser createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getConsideration() {
        return consideration;
    }

    public void setConsideration(String consideration) {
        this.consideration = consideration;
    }

    public Committee getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(Committee assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Innovation getInnovation() {
        return innovation;
    }

    public void setInnovation(Innovation innovation) {
        this.innovation = innovation;
    }

    public InnovationHistory getInnovationHistory() {
        return innovationHistory;
    }
    
    public void setInnovationHistory(InnovationHistory innovationHistory) {
        this.innovationHistory = innovationHistory;
    }
}
