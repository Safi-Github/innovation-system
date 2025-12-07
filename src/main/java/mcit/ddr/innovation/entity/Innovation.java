package mcit.ddr.innovation.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
// import mcit.ddr.innovation.enums.Category;
import mcit.ddr.innovation.enums.InnovStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Innovation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String purpose;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    @Column(columnDefinition = "TEXT")
    private String additionalInfo;

   @Column(columnDefinition = "TEXT")
    private String innovationIntroduciton;

    @Column(columnDefinition = "TEXT")
    private String digitalRequirements;

    @Column(columnDefinition = "TEXT")
    private String innovationCategory;

    @Enumerated(EnumType.STRING)
    private InnovStatus status;

    @Column(columnDefinition = "TEXT")
    private String reasonsProvingYouCanInvent;

    private String impact;

    @Column(columnDefinition = "TEXT")
    private String resourcesNeeded;

    private String attachment;

    @Column(name = "CREATE_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy/MM/dd")
    @CreationTimestamp
    private Date createDate;



    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    private Boolean isAssigned;

    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedDate;

    @ManyToOne
    @JoinColumn(name = "assigner_id")
    private MyUser assigner;

//    @ManyToOne
//    @JoinColumn(name = "board_member_id")
//    private MyUser boardMember;

    @ManyToOne
    @JoinColumn(name = "committee_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonBackReference
    private Committee committee;

    // Audit fields
    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "created_by", updatable = false)
    private MyUser createdBy;

    //InvovledPersons
    @ManyToMany
    @JoinTable(
        name = "innovation_involved_person",  // Join table for the relationship
        joinColumns = @JoinColumn(name = "innovation_id"),
        inverseJoinColumns = @JoinColumn(name = "involved_person_id")
    )
    @JsonIgnoreProperties("innovations")
    private List<InvolvedPerson> involvedPersons = new ArrayList<>();
}