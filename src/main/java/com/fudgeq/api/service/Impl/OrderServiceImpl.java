package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.CouponResponseDto;
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
import com.fudgeq.api.service.*;
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
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final NotificationService notificationService;
    private final UserService userService;
    private final CouponService couponService;
    private final CustomIdGenerator idGenerator;
    private final ModelMapper mapper;
    private final InvoiceService invoiceService;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto dto) {
        User currentUser = userService.getCurrentUserEntity();
        List<CartItem> cartItems = cartRepo.findByUser(currentUser);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        // 1. Calculate and build Order Items while checking for Product-Specific Coupons
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            BigDecimal itemSubTotal = cartItem.getSubTotal();

            // Logic: If coupon matches this specific product, apply discount here
            if (dto.getCouponCode() != null) {
                try {
                    CouponResponseDto coupon = couponService.validateCoupon(dto.getCouponCode(), cartItem.getProduct().getProductId());
                    // Apply discount: Price - (Price * percentage / 100)
                    BigDecimal discountFactor = coupon.getDiscountPercentage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                    BigDecimal discountAmount = itemSubTotal.multiply(discountFactor);
                    itemSubTotal = itemSubTotal.subtract(discountAmount);
                } catch (Exception e) {
                    // If validation fails for this specific item, we just skip and check for global later
                }
            }

            return OrderItem.builder()
                    .orderItemId(idGenerator.generateNextId(AppConstants.PREFIX_ORDER_ITEM))
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(cartItem.getUnitPrice())
                    .subTotal(itemSubTotal)
                    .build();
        }).collect(Collectors.toList());

        // 2. Calculate initial total from items
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Check for Global Coupon (if it wasn't a product-specific one)
        if (dto.getCouponCode() != null) {
            try {
                // If the coupon is global (productId is null in DB), apply to the whole total
                CouponResponseDto coupon = couponService.getCouponByCode(dto.getCouponCode());
                if (coupon.getProductId() == null) {
                    BigDecimal globalDiscountFactor = coupon.getDiscountPercentage().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                    BigDecimal globalDiscountAmount = totalAmount.multiply(globalDiscountFactor);
                    totalAmount = totalAmount.subtract(globalDiscountAmount);
                }
            } catch (Exception e) {
                // Invalid coupon for this context - optional: log or throw error
            }
        }

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

        // Link items to the order
        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepo.save(order);
        cartRepo.deleteAll(cartItems);

        notificationService.createNotification(
                currentUser,
                "Order Placed! 📦",
                "Your order " + savedOrder.getOrderId() + " has been placed. Final total: LKR " + totalAmount,
                savedOrder
        );

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
        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        String title = "";
        String message = "";

        switch (newStatus) {
            case CONFIRMED -> {
                title = "Order Confirmed! 🎉";
                message = "Your order " + orderId + " has been confirmed. We've sent the order invoice to your email.";

                // Trigger: Create ORDERING Invoice when confirmed (mainly for COD/Advance info)
                invoiceService.createInvoice(order, com.fudgeq.api.enums.InvoiceType.ORDERING);
            }
            case REJECTED -> {
                order.setRejectionReason(reason);
                title = "Order Rejected ❌";
                message = "Sorry, your order " + orderId + " was rejected. Reason: " + (reason != null ? reason : "Not specified");
            }
            case SHIPPED -> {
                title = "Order Shipped! 🚚";
                message = "Good news! Your order " + orderId + " is on its way.";
            }
            case DELIVERED -> {
                title = "Order Delivered! ✅";
                message = "Your order " + orderId + " has been delivered. Enjoy your fudge!";
            }
            case CANCELLED -> {
                title = "Order Cancelled 🚫";
                message = "Order " + orderId + " has been cancelled.";
            }
        }

        Order savedOrder = orderRepo.save(order);
        if (!title.isEmpty()) {
            notificationService.createNotification(order.getUser(), title, message, savedOrder);
        }
        return convertToResponseDto(savedOrder);
    }

    @Override
    @Transactional
    public void markAsPaid(String orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Payment can only be processed if the admin has already confirmed the order
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Order must be confirmed by admin before processing payment.");
        }

        order.setPaid(true);

        // Transition order status to PROCESSING automatically upon payment receipt
        order.setStatus(OrderStatus.PROCESSING);
        Order savedOrder = orderRepo.save(order);

        // Trigger: Create PAYMENT Invoice (Final Receipt) upon successful payment
        invoiceService.createInvoice(savedOrder, com.fudgeq.api.enums.InvoiceType.PAYMENT);

        // Notify user about successful payment - passing the savedOrder object as per new relational mapping
        notificationService.createNotification(
                order.getUser(),
                "Payment Received ✅",
                "Payment for order " + orderId + " received. Preparing your order!",
                savedOrder
        );
    }

    /**
     * Logic to prevent illegal status changes
     */
    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean isValid = switch (current) {
            case PENDING_REVIEW -> (next == OrderStatus.CONFIRMED || next == OrderStatus.REJECTED || next == OrderStatus.CANCELLED);
            case CONFIRMED -> (next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED);
            case PROCESSING -> (next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED);
            case SHIPPED -> (next == OrderStatus.DELIVERED);
            case DELIVERED, REJECTED, CANCELLED -> false; // Terminal states
            default -> false;
        };

        if (!isValid) {
            throw new RuntimeException("Illegal status transition from " + current + " to " + next);
        }
    }

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
