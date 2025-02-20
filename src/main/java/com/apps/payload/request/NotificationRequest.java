package com.apps.payload.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    private String type;

    @NotBlank(message = "Username is required")
    private String username;
}
