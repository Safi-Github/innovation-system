package mcit.ddr.innovation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import mcit.ddr.innovation.enums.InnovStatus;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;

import java.util.Date;

@Entity
@Data
@DynamicUpdate
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

    @Enumerated(EnumType.STRING)
    private InnovStatus status;

    @Column(columnDefinition = "TEXT")
    private String reasonsProvingYouCanInvent;

    private String impact;

    @Column(columnDefinition = "TEXT")
    private String resourcesNeeded;

    private String attachment;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy/MM/dd")
    @JsonIgnore
    private Date createDate;

    //@UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy/MM/dd")
    @JsonIgnore
    private Date updateDate;


    private Boolean isAssigned;

    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedDate;

    @ManyToOne
    @JoinColumn(name = "assigner_id")
    private MyUser assigner;

    @ManyToOne
    @JoinColumn(name = "board_member_id")
    private MyUser boardMember;

    // Audit fields
    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "created_by", updatable = false)
    private MyUser createdBy;
}