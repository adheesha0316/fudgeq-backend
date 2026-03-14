package com.fudgeq.api.controller;

import com.fudgeq.api.dto.OrderResponseDto;
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
    public ResponseEntity<Page<OrderResponseDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Admin view to see every order in the system
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin(page, size));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable String orderId,
            @RequestParam String status,
            @RequestParam(required = false) String reason) {
        // Critical: Admin manually confirms or rejects based on feasibility
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status, reason));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderDetails(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }
}
