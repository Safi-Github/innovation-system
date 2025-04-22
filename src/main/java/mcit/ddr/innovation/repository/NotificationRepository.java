package mcit.ddr.innovation.repository;

import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Fetch notifications for a user
    List<Notification> findByNotifyingUserIdOrderByCreatedAtDesc(Long userId);

    // Get only unread notifications for a user
    List<Notification> findByNotifyingUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);

    // Get only read notifications for a user
    List<Notification> findByNotifyingUserIdAndReadTrueOrderByCreatedAtDesc(Long userId);

    // Optional: Fetch notifications for a specific user and specific read/unread state
    List<Notification> findByNotifyingUserAndRead(MyUser user, Boolean read);
}
