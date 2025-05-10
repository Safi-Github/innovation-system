package mcit.ddr.innovation.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "committee_id"}))
@Data
public class CommitteeMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user;

    @ManyToOne
    @JoinColumn(name = "committee_id", nullable = false)
//    @JsonIgnore // ← This breaks the loop from CommitteeMember → Committee → CreatedBy (MyUser)
    @JsonBackReference
    private Committee committee;


    private boolean isHead;

    public void setIsHead(Boolean isHead) {
        this.isHead=isHead;
    }

}
