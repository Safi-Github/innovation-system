package mcit.ddr.innovation.repository;

import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // // Fetch notifications for a user
    // List<Notification> findByNotifyingUserIdOrderByCreatedAtDesc(Long userId);

    // // Get only unread notifications for a user
    // List<Notification> findByNotifyingUserIdOrderByCreatedAtDesc(Long userId);

    // // Get only read notifications for a user
    // List<Notification> findByNotifyingUserIdOrderByCreatedAtDesc(Long userId);

    // Optional: Fetch notifications for a specific user and specific read/unread state
    @Query("SELECT n FROM Notification n WHERE n.notifyingUser.id = :userId AND n.readAt IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByNotifyingUser(@Param("userId") Long userId);
}