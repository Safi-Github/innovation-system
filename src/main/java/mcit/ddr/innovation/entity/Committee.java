package mcit.ddr.innovation.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Committee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private MyUser createdBy;

    private LocalDate createdDate;

    private boolean isClosed;

    private String attachment;


    @OneToMany(mappedBy = "committee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CommitteeMember> members = new ArrayList<>();


    @OneToMany(mappedBy = "committee")
    @JsonBackReference
    private List<Innovation> innovations;


    public void setIsClosed(boolean isClosed) {
        this.isClosed=isClosed;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}