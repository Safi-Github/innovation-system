package mcit.ddr.innovation.repository;

import mcit.ddr.innovation.entity.ForgotPassword;
import mcit.ddr.innovation.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, String> {

    Optional<ForgotPassword> findFirstByUserAndIsUsedOrderByCreatedDateDesc(MyUser user, Boolean isUsed);
    ForgotPassword findByOtpCode(String otpCode);

}
