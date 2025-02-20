package com.apps.payload.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GalleryRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String imageType;
}
