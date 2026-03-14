package com.fudgeq.api.controller;

import com.fudgeq.api.dto.OrderItemDto;
import com.fudgeq.api.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AnalyticsController {

    private final OrderItemService orderItemService;

    /**
     * Get top-selling products for dashboard charts
     */
    @GetMapping("/top-selling")
    public ResponseEntity<List<OrderItemDto>> getTopSelling(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(orderItemService.getTopSellingProducts(limit));
    }

    /**
     * Get all items sold for detailed sales reports
     */
    @GetMapping("/sales-report")
    public ResponseEntity<List<OrderItemDto>> getFullSalesReport() {
        return ResponseEntity.ok(orderItemService.getAllSoldItems());
    }

    /**
     * Get items by a specific order ID (Admin view)
     */
    @GetMapping("/order-items/{orderId}")
    public ResponseEntity<List<OrderItemDto>> getItemsByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(orderItemService.getItemsByOrderId(orderId));
    }

    /**
     * AI / Trend Analysis - This could return grouped data in the future
     * For now, we use the existing sales-report logic
     */
}
