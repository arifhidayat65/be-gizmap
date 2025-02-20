package com.apps.payload.request;

import lombok.Data;

@Data
public class ProductSearchRequest {
    private String name;
    private String description;
    private Double minPrice;
    private Double maxPrice;
}
