package com.apps.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class InfoRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    @Size(min = 3, max = 50)
    private String category;

    private String imageUrl;

    private boolean isActive = true;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
