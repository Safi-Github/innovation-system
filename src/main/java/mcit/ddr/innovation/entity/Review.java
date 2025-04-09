package mcit.ddr.innovation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import mcit.ddr.innovation.enums.InnovStatus;

import java.time.LocalDate;

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

    private String comment;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id", nullable = true)
    private MyUser assignedTo;

    @ManyToOne
    @JoinColumn(name = "innovation_id")
    private Innovation innovation;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public MyUser getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(MyUser assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Innovation getInnovation() {
        return innovation;
    }

    public void setInnovation(Innovation innovation) {
        this.innovation = innovation;
    }
}
