package mcit.ddr.innovation.service;

import jakarta.transaction.Transactional;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.Notification;
import mcit.ddr.innovation.exception.ResourceNotFoundException;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MyUserRepository myUserRepository;

    public NotificationService(NotificationRepository notificationRepository, MyUserRepository myUserRepository) {
        this.notificationRepository = notificationRepository;
        this.myUserRepository = myUserRepository;
    }

    // Send notification
    public void sendNotification(Long userId, String message, String contentUrl) {
        MyUser user = myUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setNotifyingUser(user);
        notification.setMessage(message);
        notification.setContentUrl(contentUrl); // Include the frontend link
        notification.setCreatedAt(LocalDate.now());

        notificationRepository.save(notification);
    }

    // Fetch all notifications for a user (both read and unread)
    // public List<Notification> getNotifications(Long userId) {
    //     return notificationRepository.findByNotifyingUserIdOrderByCreatedAtDesc(userId);
    // }

    // // Fetch only unread notifications for a user
    // public List<Notification> getUnreadNotifications(Long userId) {
    //     return notificationRepository.findByNotifyingUserIdOrderByCreatedAtDesc(userId);
    // }

    // // Fetch only read notifications for a user
    // public List<Notification> getReadNotifications(Long userId) {
    //     return notificationRepository.findByNotifyingUserIdOrderByCreatedAtDesc(userId);
    // }

    // // Fetch only unread notifications for the logged in user
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadByNotifyingUser(userId);
    }

    // Mark notification as read
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setReadAt(LocalDate.now());
        notificationRepository.save(notification);
    }
}