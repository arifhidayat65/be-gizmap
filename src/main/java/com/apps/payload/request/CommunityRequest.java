package com.apps.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommunityRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    private String description;

    @Size(max = 100)
    private String location;

    private String imageUrl;

    private boolean isActive = true;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
