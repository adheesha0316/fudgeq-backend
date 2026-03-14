package com.fudgeq.api.controller;

import com.fudgeq.api.dto.ProductResponseDto;
import com.fudgeq.api.dto.StandardResponse;
import com.fudgeq.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    @GetMapping("/active")
    public ResponseEntity<StandardResponse<Page<ProductResponseDto>>> getActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<ProductResponseDto> products = productService.getActiveProducts(page, size);
        return ResponseEntity.ok(
                StandardResponse.success("Active products retrieved successfully", products)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<ProductResponseDto>> getProductById(@PathVariable String id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(
                StandardResponse.success("Product details retrieved successfully", product)
        );
    }
}
