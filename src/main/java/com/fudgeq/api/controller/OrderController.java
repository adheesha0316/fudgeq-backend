package com.fudgeq.api.controller;

import com.fudgeq.api.dto.OrderRequestDto;
import com.fudgeq.api.dto.OrderResponseDto;
import com.fudgeq.api.dto.StandardResponse;
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
    public ResponseEntity<StandardResponse<OrderResponseDto>> placeOrder(@Valid @RequestBody OrderRequestDto dto) {
        OrderResponseDto order = orderService.placeOrder(dto);
        return ResponseEntity.ok(
                StandardResponse.success("Order placed successfully. Awaiting admin review.", order)
        );
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<StandardResponse<Page<OrderResponseDto>>> getMyHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderResponseDto> history = orderService.getMyOrderHistory(page, size);
        return ResponseEntity.ok(
                StandardResponse.success("Order history retrieved successfully", history)
        );
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<StandardResponse<OrderResponseDto>> getOrder(@PathVariable String orderId) {
        OrderResponseDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(
                StandardResponse.success("Order details retrieved", order)
        );
    }
}
