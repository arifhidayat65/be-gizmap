package com.apps.payload.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private String type;
    private String username;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponse(Long id, String title, String message, String type, String username, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.username = username;
        this.read = read;
        this.createdAt = createdAt;
    }
}
