package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.OrderItemDto;
import com.fudgeq.api.dto.OrderRequestDto;
import com.fudgeq.api.dto.OrderResponseDto;
import com.fudgeq.api.entity.CartItem;
import com.fudgeq.api.entity.Order;
import com.fudgeq.api.entity.OrderItem;
import com.fudgeq.api.entity.User;
import com.fudgeq.api.enums.OrderStatus;
import com.fudgeq.api.repo.CartRepo;
import com.fudgeq.api.repo.OrderRepo;
import com.fudgeq.api.service.OrderService;
import com.fudgeq.api.service.UserService;
import com.fudgeq.api.utill.AppConstants;
import com.fudgeq.api.utill.CustomIdGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final UserService userService;
    private final CustomIdGenerator idGenerator;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto dto) {
        User currentUser = userService.getCurrentUserEntity();
        List<CartItem> cartItems = cartRepo.findByUser(currentUser);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        // Calculate total order amount from cart items
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Initialize order with PENDING_REVIEW status (Admin must confirm delivery date)
        Order order = Order.builder()
                .orderId(idGenerator.generateNextId(AppConstants.PREFIX_ORDER))
                .user(currentUser)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING_REVIEW)
                .deliveryAddress(dto.getDeliveryAddress())
                .contactNumber(dto.getContactNumber())
                .note(dto.getNote())
                .preferredDeliveryDate(dto.getPreferredDeliveryDate())
                .paymentMethod(dto.getPaymentMethod())
                .isPaid(false)
                .build();

        // Convert CartItems to OrderItems
        List<OrderItem> orderItems = cartItems.stream().map(cartItem ->
                OrderItem.builder()
                        .orderItemId(idGenerator.generateNextId(AppConstants.PREFIX_ORDER_ITEM))
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .priceAtPurchase(cartItem.getUnitPrice())
                        .subTotal(cartItem.getSubTotal())
                        .build()
        ).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepo.save(order);

        // Clear user's cart after successful order placement
        cartRepo.deleteAll(cartItems);

        return convertToResponseDto(savedOrder);
    }

    @Override
    public OrderResponseDto getOrderById(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToResponseDto(order);
    }

    @Override
    public Page<OrderResponseDto> getMyOrderHistory(int page, int size) {
        User currentUser = userService.getCurrentUserEntity();
        return orderRepo.findByUser(currentUser, PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::convertToResponseDto);
    }

    @Override
    public Page<OrderResponseDto> getAllOrdersForAdmin(int page, int size) {
        return orderRepo.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::convertToResponseDto);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(String orderId, String status, String reason) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus newStatus = OrderStatus.valueOf(status);
        order.setStatus(newStatus);

        // Handle rejection or cancellation reasons
        if (newStatus == OrderStatus.REJECTED || newStatus == OrderStatus.CANCELLED) {
            order.setRejectionReason(reason);
        }

        return convertToResponseDto(orderRepo.save(order));
    }

    @Override
    @Transactional
    public void markAsPaid(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Business Logic: Payment is only allowed for Admin-confirmed orders
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Order must be confirmed by admin before processing payment.");
        }

        order.setPaid(true);
        orderRepo.save(order);
    }

    /**
     * Helper to convert Entity to Response DTO with complex mappings
     */
    private OrderResponseDto convertToResponseDto(Order order) {
        OrderResponseDto dto = mapper.map(order, OrderResponseDto.class);
        dto.setUserId(order.getUser().getUserId());
        dto.setCustomerName(order.getUser().getFirstName() + " " + order.getUser().getLastName());

        List<OrderItemDto> itemDtos = order.getOrderItems().stream().map(item -> {
            OrderItemDto itemDto = mapper.map(item, OrderItemDto.class);
            itemDto.setProductName(item.getProduct().getName());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }
}
