package com.fudgeq.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRequestDto {
    private String code;
    private BigDecimal discountPercentage;
    private LocalDateTime expiryDate;
    private String productId; // Optional: Only if linked to a specific product
}
