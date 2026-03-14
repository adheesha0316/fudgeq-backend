package com.fudgeq.api.controller;

import com.fudgeq.api.dto.OrderResponseDto;
import com.fudgeq.api.dto.StandardResponse;
import com.fudgeq.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping("/all")
    public ResponseEntity<StandardResponse<Page<OrderResponseDto>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderResponseDto> allOrders = orderService.getAllOrdersForAdmin(page, size);
        return ResponseEntity.ok(
                StandardResponse.success("System orders retrieved successfully", allOrders)
        );
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<StandardResponse<OrderResponseDto>> updateStatus(
            @PathVariable String orderId,
            @RequestParam String status,
            @RequestParam(required = false) String reason) {

        OrderResponseDto updatedOrder = orderService.updateOrderStatus(orderId, status, reason);
        String message = "Order " + orderId + " has been successfully updated to " + status;

        return ResponseEntity.ok(
                StandardResponse.success(message, updatedOrder)
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<StandardResponse<OrderResponseDto>> getOrderDetails(@PathVariable String orderId) {
        OrderResponseDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(
                StandardResponse.success("Order details retrieved for admin", order)
        );
    }
}
