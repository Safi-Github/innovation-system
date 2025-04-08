package mcit.ddr.innovation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ForgotPassword {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String otpCode;
        private LocalDateTime otpExpirationDate;
        private Boolean isUsed;
        private LocalDateTime createdDate;

        // Many-to-One relationship with User
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private MyUser user;

        // Getters and Setters
        public String getOtpCode() {
            return otpCode;
        }

        public void setOtpCode(String otpCode) {
            this.otpCode = otpCode;
        }

        public LocalDateTime getOtpExpirationDate() {
            return otpExpirationDate;
        }

        public void setOtpExpirationDate(LocalDateTime otpExpirationDate) {
            this.otpExpirationDate = otpExpirationDate;
        }

        public Boolean getIsUsed() {
            return isUsed;
        }

        public void setIsUsed(Boolean isUsed) {
            this.isUsed = isUsed;
        }

        public LocalDateTime getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
        }

        public MyUser getUser() {
            return user;
        }

        public void setUser(MyUser user) {
            this.user = user;
        }
}
