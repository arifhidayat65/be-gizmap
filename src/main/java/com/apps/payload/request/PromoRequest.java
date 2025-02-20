package com.apps.payload.request;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class PromoRequest {
    @NotBlank(message = "Code is required")
    @Pattern(regexp = "^[A-Z0-9]{4,10}$", message = "Code must be 4-10 characters long and contain only uppercase letters and numbers")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Discount amount is required")
    @Positive(message = "Discount amount must be greater than 0")
    private Double discountAmount;

    @NotNull(message = "Minimum purchase is required")
    @PositiveOrZero(message = "Minimum purchase must be 0 or greater")
    private Double minimumPurchase;

    @NotNull(message = "Maximum uses is required")
    @Positive(message = "Maximum uses must be greater than 0")
    private Integer maxUses;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    private Boolean isActive = true;

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getMinimumPurchase() {
        return minimumPurchase;
    }

    public void setMinimumPurchase(Double minimumPurchase) {
        this.minimumPurchase = minimumPurchase;
    }

    public Integer getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
