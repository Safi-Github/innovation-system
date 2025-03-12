package mcit.ddr.innovation.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import mcit.ddr.innovation.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{
    
}
