package com.fudgeq.api.dto;

import com.fudgeq.api.enums.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductStatusUpdateDto {
    private ProductStatus status;
    private String adminRemarks; // Rejection reason etc.
    private BigDecimal adjustedPrice; // If admin wants to change the price before approval
}
