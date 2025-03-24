package mcit.ddr.innovation.specification;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.enums.Category;
import mcit.ddr.innovation.enums.InnovStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class InnovationSpecification {

    public static Specification<Innovation> filterByCriteria(
            Category category, InnovStatus status, Date createDate, Date lastModifiedDate,
            Date assignedDate, MyUser assigner, MyUser boardMember, MyUser createdBy) {
        
        return (Root<Innovation> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 🟢 Filter by Category
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            // 🟢 Filter by Status
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 🟢 Filter by Create Date (Exact Date Match)
            if (createDate != null) {
                predicates.add(cb.equal(root.get("createDate"), createDate));
            }

            // 🟢 Filter by Last Modified Date (Exact Date Match)
            if (lastModifiedDate != null) {
                predicates.add(cb.equal(root.get("lastModifiedDate"), lastModifiedDate));
            }

            // 🟢 Filter by Assigned Date (Exact Date Match)
            if (assignedDate != null) {
                predicates.add(cb.equal(root.get("assignedDate"), assignedDate));
            }

            // 🟢 Filter by Assigner
            if (assigner != null) {
                predicates.add(cb.equal(root.get("assigner"), assigner));
            }

            // 🟢 Filter by Board Member
            if (boardMember != null) {
                predicates.add(cb.equal(root.get("boardMember"), boardMember));
            }

            // 🟢 Filter by Created By
            if (createdBy != null) {
                predicates.add(cb.equal(root.get("createdBy"), createdBy));
            }

            // 🟢 Sorting: Most Recent First
            query.orderBy(cb.desc(root.get("createDate")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
