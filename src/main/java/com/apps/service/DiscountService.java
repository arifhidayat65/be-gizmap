package com.apps.service;

import com.apps.model.Discount;
import com.apps.model.Product;
import com.apps.repository.DiscountRepository;
import com.apps.repository.ProductRepository;
import com.apps.payload.request.DiscountRequest;
import com.apps.payload.response.DiscountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public DiscountResponse createDiscount(DiscountRequest discountRequest) {
        Product product = productRepository.findById(discountRequest.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + discountRequest.getProductId()));

        Discount discount = new Discount();
        discount.setName(discountRequest.getName());
        discount.setDescription(discountRequest.getDescription());
        discount.setPercentage(discountRequest.getPercentage());
        discount.setProduct(product);
        discount.setStartDate(discountRequest.getStartDate());
        discount.setEndDate(discountRequest.getEndDate());
        discount.setIsActive(discountRequest.getIsActive());

        Discount savedDiscount = discountRepository.save(discount);
        return convertToResponse(savedDiscount);
    }

    @Transactional(readOnly = true)
    public List<DiscountResponse> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiscountResponse getDiscountById(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found with id: " + id));
        return convertToResponse(discount);
    }

    @Transactional(readOnly = true)
    public List<DiscountResponse> getActiveDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        return discountRepository.findByIsActiveAndStartDateBeforeAndEndDateAfter(true, now, now).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DiscountResponse> getDiscountsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        return discountRepository.findByProduct(product).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiscountResponse updateDiscount(Long id, DiscountRequest discountRequest) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found with id: " + id));

        Product product = productRepository.findById(discountRequest.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + discountRequest.getProductId()));

        discount.setName(discountRequest.getName());
        discount.setDescription(discountRequest.getDescription());
        discount.setPercentage(discountRequest.getPercentage());
        discount.setProduct(product);
        discount.setStartDate(discountRequest.getStartDate());
        discount.setEndDate(discountRequest.getEndDate());
        discount.setIsActive(discountRequest.getIsActive());

        Discount updatedDiscount = discountRepository.save(discount);
        return convertToResponse(updatedDiscount);
    }

    @Transactional
    public void deleteDiscount(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new EntityNotFoundException("Discount not found with id: " + id);
        }
        discountRepository.deleteById(id);
    }

    private DiscountResponse convertToResponse(Discount discount) {
        DiscountResponse response = new DiscountResponse();
        response.setId(discount.getId());
        response.setName(discount.getName());
        response.setDescription(discount.getDescription());
        response.setPercentage(discount.getPercentage());
        response.setProductId(discount.getProduct().getId());
        response.setProductName(discount.getProduct().getName());
        response.setStartDate(discount.getStartDate());
        response.setEndDate(discount.getEndDate());
        response.setIsActive(discount.getIsActive());
        response.setCreatedAt(discount.getCreatedAt());
        response.setUpdatedAt(discount.getUpdatedAt());
        return response;
    }
}
