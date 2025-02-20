package com.apps.controller;

import com.apps.model.Notification;
import com.apps.service.NotificationService;
import com.apps.payload.request.NotificationRequest;
import com.apps.payload.response.NotificationResponse;
import com.apps.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(Authentication authentication) {
        String username = authentication.getName();
        List<Notification> notifications = notificationService.getUserNotifications(username);
        List<NotificationResponse> response = notifications.stream()
            .map(n -> new NotificationResponse(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getType(),
                n.getUser().getUsername(),
                n.isRead(),
                n.getCreatedAt()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(Authentication authentication) {
        String username = authentication.getName();
        List<Notification> notifications = notificationService.getUnreadNotifications(username);
        List<NotificationResponse> response = notifications.stream()
            .map(n -> new NotificationResponse(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getType(),
                n.getUser().getUsername(),
                n.isRead(),
                n.getCreatedAt()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(notificationService.getUnreadCount(username));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<MessageResponse> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(new MessageResponse("Notification marked as read"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<MessageResponse> markAllAsRead(Authentication authentication) {
        String username = authentication.getName();
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok(new MessageResponse("All notifications marked as read"));
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        notificationService.sendNotification(
            request.getUsername(),
            request.getTitle(),
            request.getMessage(),
            request.getType()
        );
        return ResponseEntity.ok(new MessageResponse("Notification sent successfully"));
    }

    // WebSocket endpoint for sending notifications
    @MessageMapping("/send-notification")
    public void handleWebSocketNotification(@Payload NotificationRequest request) {
        notificationService.sendNotification(
            request.getUsername(),
            request.getTitle(),
            request.getMessage(),
            request.getType()
        );
    }
}
