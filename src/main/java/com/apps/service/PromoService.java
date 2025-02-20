package com.apps.service;

import com.apps.model.Promo;
import com.apps.repository.PromoRepository;
import com.apps.payload.request.PromoRequest;
import com.apps.payload.response.PromoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromoService {

    @Autowired
    private PromoRepository promoRepository;

    @Transactional
    public PromoResponse createPromo(PromoRequest promoRequest) {
        // Check if promo code already exists
        if (promoRepository.findByCode(promoRequest.getCode()).isPresent()) {
            throw new IllegalStateException("Promo code already exists: " + promoRequest.getCode());
        }

        Promo promo = new Promo();
        promo.setCode(promoRequest.getCode().toUpperCase());
        promo.setName(promoRequest.getName());
        promo.setDescription(promoRequest.getDescription());
        promo.setDiscountAmount(promoRequest.getDiscountAmount());
        promo.setMinimumPurchase(promoRequest.getMinimumPurchase());
        promo.setMaxUses(promoRequest.getMaxUses());
        promo.setCurrentUses(0);
        promo.setStartDate(promoRequest.getStartDate());
        promo.setEndDate(promoRequest.getEndDate());
        promo.setIsActive(promoRequest.getIsActive());

        Promo savedPromo = promoRepository.save(promo);
        return convertToResponse(savedPromo);
    }

    @Transactional(readOnly = true)
    public List<PromoResponse> getAllPromos() {
        return promoRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PromoResponse getPromoById(Long id) {
        Promo promo = promoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promo not found with id: " + id));
        return convertToResponse(promo);
    }

    @Transactional(readOnly = true)
    public List<PromoResponse> getActivePromos() {
        LocalDateTime now = LocalDateTime.now();
        return promoRepository.findByIsActiveAndStartDateBeforeAndEndDateAfterAndCurrentUsesLessThan(
                true, now, now, Integer.MAX_VALUE).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PromoResponse validateAndGetPromo(String code, Double purchaseAmount) {
        LocalDateTime now = LocalDateTime.now();
        Promo promo = promoRepository.findByCodeAndIsActiveAndStartDateBeforeAndEndDateAfterAndCurrentUsesLessThan(
                code.toUpperCase(), true, now, now, Integer.MAX_VALUE)
                .orElseThrow(() -> new IllegalStateException("Invalid or expired promo code: " + code));

        if (purchaseAmount < promo.getMinimumPurchase()) {
            throw new IllegalStateException(
                    String.format("Purchase amount %.2f is less than minimum required %.2f", 
                    purchaseAmount, promo.getMinimumPurchase()));
        }

        return convertToResponse(promo);
    }

    @Transactional
    public PromoResponse updatePromo(Long id, PromoRequest promoRequest) {
        Promo promo = promoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promo not found with id: " + id));

        // Check if updated code conflicts with existing codes
        if (!promo.getCode().equals(promoRequest.getCode()) &&
            promoRepository.findByCode(promoRequest.getCode()).isPresent()) {
            throw new IllegalStateException("Promo code already exists: " + promoRequest.getCode());
        }

        promo.setCode(promoRequest.getCode().toUpperCase());
        promo.setName(promoRequest.getName());
        promo.setDescription(promoRequest.getDescription());
        promo.setDiscountAmount(promoRequest.getDiscountAmount());
        promo.setMinimumPurchase(promoRequest.getMinimumPurchase());
        promo.setMaxUses(promoRequest.getMaxUses());
        promo.setStartDate(promoRequest.getStartDate());
        promo.setEndDate(promoRequest.getEndDate());
        promo.setIsActive(promoRequest.getIsActive());

        Promo updatedPromo = promoRepository.save(promo);
        return convertToResponse(updatedPromo);
    }

    @Transactional
    public void deletePromo(Long id) {
        if (!promoRepository.existsById(id)) {
            throw new EntityNotFoundException("Promo not found with id: " + id);
        }
        promoRepository.deleteById(id);
    }

    @Transactional
    public void usePromo(String code) {
        Promo promo = promoRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Promo not found with code: " + code));
        
        if (promo.getCurrentUses() >= promo.getMaxUses()) {
            throw new IllegalStateException("Promo code has reached maximum uses: " + code);
        }

        promo.setCurrentUses(promo.getCurrentUses() + 1);
        promoRepository.save(promo);
    }

    private PromoResponse convertToResponse(Promo promo) {
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
        return response;
    }
}
