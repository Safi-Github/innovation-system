package mcit.ddr.innovation.repository;
import mcit.ddr.innovation.enums.InnovStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mcit.ddr.innovation.entity.Innovation;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// public interface InnovationRepository extends JpaRepository<Innovation, Long>{}
public interface InnovationRepository extends JpaRepository<Innovation, Long>, JpaSpecificationExecutor<Innovation> {


    long countByStatus(InnovStatus status);
    @Query("SELECT i.category.id AS categoryId, i.category.name AS categoryName, COUNT(i.id) AS innovationCount " +
            "FROM Innovation i " +
            "GROUP BY i.category.id, i.category.name")
    List<CategoryInnovationCount> countInnovationsPerCategory();

    interface CategoryInnovationCount {
        String getCategoryName();
        Long getInnovationCount();
    }



}
