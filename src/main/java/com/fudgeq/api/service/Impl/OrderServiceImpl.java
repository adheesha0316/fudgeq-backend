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
import com.fudgeq.api.service.NotificationService;
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
    private final NotificationService notificationService;
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

        String title = "";
        String message = "";

        if (newStatus == OrderStatus.CONFIRMED) {
            title = "Order Confirmed! 🎉";
            message = "Your order " + orderId + " has been confirmed by Admin. You can now proceed with the payment.";
        } else if (newStatus == OrderStatus.REJECTED || newStatus == OrderStatus.CANCELLED) {
            order.setRejectionReason(reason);
            title = "Order Update: " + newStatus;
            message = "Order " + orderId + " was " + newStatus.toString().toLowerCase() + ". Reason: " + (reason != null ? reason : "Not specified");
        }

        Order savedOrder = orderRepo.save(order);

        // Send notification to user after status update
        if (!title.isEmpty()) {
            notificationService.createNotification(order.getUser(), title, message, orderId);
        }

        return convertToResponseDto(savedOrder);
    }

    @Override
    @Transactional
    public void markAsPaid(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Order must be confirmed by admin before processing payment.");
        }

        order.setPaid(true);
        orderRepo.save(order);

        // Notify user about successful payment receipt
        notificationService.createNotification(
                order.getUser(),
                "Payment Received ✅",
                "We have received your payment for order " + orderId + ". Your fudge is being prepared!",
                orderId
        );
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
