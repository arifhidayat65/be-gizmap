package com.apps.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class KontenRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    @Size(min = 3, max = 50)
    private String type;

    private String imageUrl;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
