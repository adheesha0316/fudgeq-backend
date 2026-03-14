package com.fudgeq.api.dto;

import com.fudgeq.api.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private String orderId;
    private String userId;
    private String customerName;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String deliveryAddress;
    private String contactNumber;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
}
