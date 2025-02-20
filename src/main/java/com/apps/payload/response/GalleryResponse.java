package com.apps.payload.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String imageType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
