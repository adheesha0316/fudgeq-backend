package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.OrderItemDto;
import com.fudgeq.api.entity.OrderItem;
import com.fudgeq.api.repo.OrderItemRepo;
import com.fudgeq.api.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepo orderItemRepo;
    private final ModelMapper mapper;

    @Override
    public List<OrderItemDto> getItemsByOrderId(String orderId) {
        // Fetching all items associated with a specific order ID
        return orderItemRepo.findByOrder_OrderId(orderId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItemDto> getAllSoldItems() {
        // Retrieve every single item sold across all orders for global analysis
        return orderItemRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItemDto> getTopSellingProducts(int limit) {
        // Logic to fetch top products based on quantity sold
        // Note: You need to add findTopSellingProducts in OrderItemRepo
        return orderItemRepo.findTopSellingProducts(PageRequest.of(0, limit)).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps OrderItem entity to OrderItemDto and injects product name for the UI/AI
     */
    private OrderItemDto convertToDto(OrderItem item) {
        OrderItemDto dto = mapper.map(item, OrderItemDto.class);
        dto.setProductId(item.getProduct().getProductId());
        dto.setProductName(item.getProduct().getName());
        return dto;
    }
}
