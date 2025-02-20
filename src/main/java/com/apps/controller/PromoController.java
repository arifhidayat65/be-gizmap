package com.apps.controller;

import com.apps.payload.request.PromoRequest;
import com.apps.payload.response.MessageResponse;
import com.apps.payload.response.PromoResponse;
import com.apps.service.BaseService;
import com.apps.service.PromoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/promos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PromoController extends AbstractBaseController<PromoResponse, PromoRequest, Long> {

    @Autowired
    private PromoService promoService;

    @Override
    protected BaseService<PromoResponse, PromoRequest, Long> getService() {
        return promoService;
    }

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromoResponse> create(@RequestBody PromoRequest request) {
        return super.create(request);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PromoResponse>> getAll() {
        return super.getAll();
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromoResponse> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromoResponse> update(@PathVariable Long id, @RequestBody PromoRequest request) {
        return super.update(id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromoResponse>> getActivePromos() {
        List<PromoResponse> activePromos = promoService.getActivePromos();
        return ResponseEntity.ok(activePromos);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<PromoResponse>> getNearbyActivePromos(
            @RequestParam @NotNull(message = "Latitude is required") Double latitude,
            @RequestParam @NotNull(message = "Longitude is required") Double longitude) {
        List<PromoResponse> promos = promoService.getNearbyActivePromos(latitude, longitude);
        return ResponseEntity.ok(promos);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validatePromo(
            @RequestParam String code,
            @RequestParam Double purchaseAmount) {
        try {
            PromoResponse promo = promoService.validateAndGetPromo(code, purchaseAmount);
            return ResponseEntity.ok(promo);
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/{code}/use")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> usePromo(@PathVariable String code) {
        try {
            promoService.usePromo(code);
            return ResponseEntity.ok(new MessageResponse("Promo code used successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
}
