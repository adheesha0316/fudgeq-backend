package com.fudgeq.api.dto;

import com.fudgeq.api.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private String productId;
    private String name;
    private String description;
    private String ingredients;
    private BigDecimal price;
    private int dailyCapacity;
    private ProductStatus status;
    private String addedBy;
    private List<String> imageUrls;
    private boolean isAvailable;
    private boolean isSignature;
    private Double weightGrams;
}
