package com.apps.util;

import com.apps.model.Faq;
import com.apps.model.Promo;
import com.apps.payload.response.FaqResponse;
import com.apps.payload.response.PromoResponse;

public class ResponseUtil {

    public static FaqResponse convertToResponse(Faq faq) {
        FaqResponse response = new FaqResponse();
        response.setId(faq.getId());
        response.setQuestion(faq.getQuestion());
        response.setAnswer(faq.getAnswer());
        response.setCategory(faq.getCategory());
        response.setOrderIndex(faq.getOrderIndex());
        response.setIsActive(faq.getIsActive());
        response.setCreatedAt(faq.getCreatedAt());
        response.setUpdatedAt(faq.getUpdatedAt());
        return response;
    }

    public static PromoResponse convertToResponse(Promo promo) {
        PromoResponse response = new PromoResponse();
        response.setId(promo.getId());
        response.setCode(promo.getCode());
        response.setName(promo.getName());
        response.setDescription(promo.getDescription());
        response.setDiscountAmount(promo.getDiscountAmount());
        response.setMinimumPurchase(promo.getMinimumPurchase());
        response.setMaxUses(promo.getMaxUses());
        response.setCurrentUses(promo.getCurrentUses());
        response.setStartDate(promo.getStartDate());
        response.setEndDate(promo.getEndDate());
        response.setIsActive(promo.getIsActive());
        response.setCreatedAt(promo.getCreatedAt());
        response.setUpdatedAt(promo.getUpdatedAt());
        response.setLatitude(promo.getLatitude());
        response.setLongitude(promo.getLongitude());
        response.setRadius(promo.getRadius());
        return response;
    }
}
