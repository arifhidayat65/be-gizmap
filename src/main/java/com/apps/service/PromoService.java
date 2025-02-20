package com.apps.service;

import com.apps.model.Promo;
import com.apps.repository.PromoRepository;
import com.apps.payload.request.PromoRequest;
import com.apps.payload.response.PromoResponse;
import com.apps.util.LocationUtil;
import com.apps.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromoService extends AbstractBaseService<Promo, PromoResponse, PromoRequest, Long> {

    @Autowired
    private PromoRepository promoRepository;

    @Override
    protected JpaRepository<Promo, Long> getRepository() {
        return promoRepository;
    }

    @Override
    protected String getEntityName() {
        return "Promo";
    }

    @Override
    protected Promo createEntity(PromoRequest request) {
        validatePromoCode(request.getCode(), null);
        Promo promo = new Promo();
        updateEntity(promo, request);
        promo.setCurrentUses(0);
        return promo;
    }

    @Override
    protected void updateEntity(Promo promo, PromoRequest request) {
        validatePromoCode(request.getCode(), promo.getCode());
        promo.setCode(request.getCode().toUpperCase());
        promo.setName(request.getName());
        promo.setDescription(request.getDescription());
        promo.setDiscountAmount(request.getDiscountAmount());
        promo.setMinimumPurchase(request.getMinimumPurchase());
        promo.setMaxUses(request.getMaxUses());
        promo.setStartDate(request.getStartDate());
        promo.setEndDate(request.getEndDate());
        promo.setIsActive(request.getIsActive());
        promo.setLatitude(request.getLatitude());
        promo.setLongitude(request.getLongitude());
        promo.setRadius(request.getRadius());
    }

    @Override
    protected PromoResponse convertToResponse(Promo promo) {
        return ResponseUtil.convertToResponse(promo);
    }

    @Transactional(readOnly = true)
    public List<PromoResponse> getActivePromos() {
        LocalDateTime now = LocalDateTime.now();
        return convertList(
            promoRepository.findByIsActiveAndStartDateBeforeAndEndDateAfterAndCurrentUsesLessThan(
                true, now, now, Integer.MAX_VALUE),
            this::convertToResponse
        );
    }

    @Transactional(readOnly = true)
    public List<PromoResponse> getNearbyActivePromos(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude are required for nearby search");
        }

        LocalDateTime now = LocalDateTime.now();
        return promoRepository.findByIsActiveAndStartDateBeforeAndEndDateAfterAndCurrentUsesLessThan(
                true, now, now, Integer.MAX_VALUE).stream()
                .filter(promo -> isPromoNearby(promo, latitude, longitude))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PromoResponse validateAndGetPromo(String code, Double purchaseAmount) {
        LocalDateTime now = LocalDateTime.now();
        Promo promo = promoRepository.findByCodeAndIsActiveAndStartDateBeforeAndEndDateAfterAndCurrentUsesLessThan(
                code.toUpperCase(), true, now, now, Integer.MAX_VALUE)
                .orElseThrow(() -> new IllegalStateException("Invalid or expired promo code: " + code));

        validatePurchaseAmount(promo, purchaseAmount);
        return convertToResponse(promo);
    }

    @Transactional
    public void usePromo(String code) {
        Promo promo = findPromoByCode(code);
        validatePromoUsage(promo);
        promo.setCurrentUses(promo.getCurrentUses() + 1);
        promoRepository.save(promo);
    }

    private Promo findPromoByCode(String code) {
        return promoRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Promo not found with code: " + code));
    }

    private void validatePromoCode(String newCode, String currentCode) {
        if (currentCode != null && currentCode.equals(newCode)) {
            return;
        }
        if (promoRepository.findByCode(newCode).isPresent()) {
            throw new IllegalStateException("Promo code already exists: " + newCode);
        }
    }

    private void validatePromoUsage(Promo promo) {
        if (promo.getCurrentUses() >= promo.getMaxUses()) {
            throw new IllegalStateException("Promo code has reached maximum uses: " + promo.getCode());
        }
    }

    private void validatePurchaseAmount(Promo promo, Double purchaseAmount) {
        if (purchaseAmount < promo.getMinimumPurchase()) {
            throw new IllegalStateException(
                    String.format("Purchase amount %.2f is less than minimum required %.2f",
                            purchaseAmount, promo.getMinimumPurchase()));
        }
    }

    private boolean isPromoNearby(Promo promo, Double latitude, Double longitude) {
        if (promo.getLatitude() == null || promo.getLongitude() == null || promo.getRadius() == null) {
            return false;
        }
        return LocationUtil.isWithinRadius(
                latitude, longitude,
                promo.getLatitude(), promo.getLongitude(),
                promo.getRadius()
        );
    }
}
