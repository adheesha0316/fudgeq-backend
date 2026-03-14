package com.fudgeq.api.service;

import com.fudgeq.api.dto.OrderRequestDto;
import com.fudgeq.api.dto.OrderResponseDto;
import org.springframework.data.domain.Page;

public interface OrderService {
    // Customer operations
    OrderResponseDto placeOrder(OrderRequestDto dto);
    OrderResponseDto getOrderById(String orderId);
    Page<OrderResponseDto> getMyOrderHistory(int page, int size);

    // Admin operations
    Page<OrderResponseDto> getAllOrdersForAdmin(int page, int size);
    OrderResponseDto updateOrderStatus(String orderId, String status, String reason);

    // Payment update (After online payment success)
    void markAsPaid(String orderId);
}
