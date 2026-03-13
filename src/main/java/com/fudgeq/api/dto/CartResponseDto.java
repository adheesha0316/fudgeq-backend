package com.fudgeq.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDto {

    private String cartItemId;
    private String productId;
    private String productName;
    private String productImage; // Main image URL of the fudge
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
}
