package mcit.ddr.innovation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import mcit.ddr.innovation.enums.Category;
import mcit.ddr.innovation.enums.InnovStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.w3c.dom.Text;

import java.util.Date;

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

    @Enumerated(EnumType.STRING)
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

    @Column(name = "CREATE_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy/MM/dd")
    @CreationTimestamp
    @JsonIgnore
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

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
    @JsonIgnore
    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "created_by", updatable = false)
    private MyUser createdBy;
}