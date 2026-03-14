package com.fudgeq.api.service;

import com.fudgeq.api.dto.OrderItemDto;

import java.util.List;

public interface OrderItemService {

    // Get all items belonging to a specific order
    List<OrderItemDto> getItemsByOrderId(String orderId);

    // For Admin: Get all items sold (useful for stock & AI analysis)
    List<OrderItemDto> getAllSoldItems();

    // For Admin: Get top selling products (AI Insight base)
    List<OrderItemDto> getTopSellingProducts(int limit);
}
