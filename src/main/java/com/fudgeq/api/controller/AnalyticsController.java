package com.fudgeq.api.controller;

import com.fudgeq.api.dto.OrderItemDto;
import com.fudgeq.api.dto.StandardResponse;
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

    @GetMapping("/top-selling")
    public ResponseEntity<StandardResponse<List<OrderItemDto>>> getTopSelling(@RequestParam(defaultValue = "5") int limit) {
        List<OrderItemDto> topProducts = orderItemService.getTopSellingProducts(limit);
        return ResponseEntity.ok(
                StandardResponse.success("Top selling products retrieved for analytics", topProducts)
        );
    }

    @GetMapping("/sales-report")
    public ResponseEntity<StandardResponse<List<OrderItemDto>>> getFullSalesReport() {
        List<OrderItemDto> salesReport = orderItemService.getAllSoldItems();
        return ResponseEntity.ok(
                StandardResponse.success("Detailed sales report generated", salesReport)
        );
    }

    @GetMapping("/order-items/{orderId}")
    public ResponseEntity<StandardResponse<List<OrderItemDto>>> getItemsByOrder(@PathVariable String orderId) {
        List<OrderItemDto> items = orderItemService.getItemsByOrderId(orderId);
        return ResponseEntity.ok(
                StandardResponse.success("Items for order " + orderId + " retrieved", items)
        );
    }
}
