package mcit.ddr.innovation.controller;

import mcit.ddr.innovation.entity.Notification;
import mcit.ddr.innovation.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Endpoint to get all notifications for a user read/unread
    @GetMapping
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@RequestParam Long userId) {
        List<Notification> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to get unread notifications for a user
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@RequestParam Long userId) {
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(unreadNotifications);
    }

    // Endpoint to mark a notification as read
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }
}
