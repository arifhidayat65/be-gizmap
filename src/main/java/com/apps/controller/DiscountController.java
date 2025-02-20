package com.apps.controller;

import com.apps.payload.request.DiscountRequest;
import com.apps.payload.response.DiscountResponse;
import com.apps.payload.response.MessageResponse;
import com.apps.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountResponse> createDiscount(@Valid @RequestBody DiscountRequest discountRequest) {
        DiscountResponse discount = discountService.createDiscount(discountRequest);
        return new ResponseEntity<>(discount, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DiscountResponse>> getAllDiscounts() {
        List<DiscountResponse> discounts = discountService.getAllDiscounts();
        return ResponseEntity.ok(discounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountResponse> getDiscountById(@PathVariable Long id) {
        DiscountResponse discount = discountService.getDiscountById(id);
        return ResponseEntity.ok(discount);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DiscountResponse>> getActiveDiscounts() {
        List<DiscountResponse> activeDiscounts = discountService.getActiveDiscounts();
        return ResponseEntity.ok(activeDiscounts);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<DiscountResponse>> getDiscountsByProduct(@PathVariable Long productId) {
        List<DiscountResponse> discounts = discountService.getDiscountsByProduct(productId);
        return ResponseEntity.ok(discounts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountResponse> updateDiscount(
            @PathVariable Long id,
            @Valid @RequestBody DiscountRequest discountRequest) {
        DiscountResponse updatedDiscount = discountService.updateDiscount(id, discountRequest);
        return ResponseEntity.ok(updatedDiscount);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok(new MessageResponse("Discount deleted successfully"));
    }
}
