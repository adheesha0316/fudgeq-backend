package com.fudgeq.api.controller;

import com.fudgeq.api.dto.OrderRequestDto;
import com.fudgeq.api.dto.OrderResponseDto;
import com.fudgeq.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponseDto> placeOrder(@Valid @RequestBody OrderRequestDto dto) {
        // Creates a new order from current user's cart
        return ResponseEntity.ok(orderService.placeOrder(dto));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderResponseDto>> getMyHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Returns paginated order history for the logged-in customer
        return ResponseEntity.ok(orderService.getMyOrderHistory(page, size));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable String orderId) {
        // Detailed view of a specific order
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }
}
