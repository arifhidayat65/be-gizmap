package com.apps.controller;

import com.apps.payload.request.PromoRequest;
import com.apps.payload.response.PromoResponse;
import com.apps.payload.response.MessageResponse;
import com.apps.service.PromoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/promos")
public class PromoController {

    @Autowired
    private PromoService promoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromoResponse> createPromo(@Valid @RequestBody PromoRequest promoRequest) {
        PromoResponse promo = promoService.createPromo(promoRequest);
        return new ResponseEntity<>(promo, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PromoResponse>> getAllPromos() {
        List<PromoResponse> promos = promoService.getAllPromos();
        return ResponseEntity.ok(promos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromoResponse> getPromoById(@PathVariable Long id) {
        PromoResponse promo = promoService.getPromoById(id);
        return ResponseEntity.ok(promo);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PromoResponse>> getActivePromos() {
        List<PromoResponse> activePromos = promoService.getActivePromos();
        return ResponseEntity.ok(activePromos);
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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromoResponse> updatePromo(
            @PathVariable Long id,
            @Valid @RequestBody PromoRequest promoRequest) {
        PromoResponse updatedPromo = promoService.updatePromo(id, promoRequest);
        return ResponseEntity.ok(updatedPromo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePromo(@PathVariable Long id) {
        promoService.deletePromo(id);
        return ResponseEntity.ok(new MessageResponse("Promo deleted successfully"));
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
