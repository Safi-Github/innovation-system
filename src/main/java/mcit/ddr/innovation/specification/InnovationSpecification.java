package mcit.ddr.innovation.specification;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import mcit.ddr.innovation.dto.InnovationSearchCriteriaDTO;
import mcit.ddr.innovation.entity.Innovation;

import java.util.ArrayList;
import java.util.List;

public class InnovationSpecification {

    public static Specification<Innovation> filterByCriteria(InnovationSearchCriteriaDTO criteria) {
        
        return (Root<Innovation> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 🟢 Filter by Category
            if (criteria.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), criteria.getCategory()));
            }

            // 🟢 Filter by Status
            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            // 🟢 Filter by Create Date (Exact Date Match)
            if (criteria.getCreateDate() != null) {
                predicates.add(cb.equal(root.get("createDate"), criteria.getCreateDate()));
            }

            // 🟢 Filter by Last Modified Date (Exact Date Match)
            if (criteria.getLastModifiedDate() != null) {
                predicates.add(cb.equal(root.get("lastModifiedDate"), criteria.getLastModifiedDate()));
            }

            // 🟢 Filter by Assigned Date (Exact Date Match)
            if (criteria.getAssignedDate() != null) {
                predicates.add(cb.equal(root.get("assignedDate"), criteria.getAssignedDate()));
            }

            // 🟢 Filter by Assigner
            if (criteria.getAssigner() != null) {
                predicates.add(cb.equal(root.get("assigner"), criteria.getAssigner()));
            }

            // 🟢 Filter by Board Member
            if (criteria.getBoardMember() != null) {
                predicates.add(cb.equal(root.get("boardMember"), criteria.getBoardMember()));
            }

            // 🟢 Filter by Created By
            if (criteria.getCreatedBy() != null) {
                predicates.add(cb.equal(root.get("createdBy"), criteria.getCreatedBy()));
            }

            // 🟢 Sorting: Most Recent First
            query.orderBy(cb.desc(root.get("createDate")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
