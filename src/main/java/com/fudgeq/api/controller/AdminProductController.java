package com.fudgeq.api.controller;

import com.fudgeq.api.dto.ProductRequestDto;
import com.fudgeq.api.dto.ProductResponseDto;
import com.fudgeq.api.dto.ProductStatusUpdateDto;
import com.fudgeq.api.dto.StandardResponse;
import com.fudgeq.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@CrossOrigin
public class AdminProductController {

    private final ProductService productService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<StandardResponse<ProductResponseDto>> createProduct(
            @ModelAttribute ProductRequestDto dto,
            Principal principal) {
        ProductResponseDto product = productService.createProduct(dto, principal.getName());
        return ResponseEntity.ok(
                StandardResponse.success("Product created successfully", product)
        );
    }

    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<StandardResponse<ProductResponseDto>> updateProduct(
            @PathVariable String id,
            @ModelAttribute ProductRequestDto dto,
            Principal principal) {
        ProductResponseDto product = productService.updateProduct(id, dto, principal.getName());
        return ResponseEntity.ok(
                StandardResponse.success("Product updated successfully", product)
        );
    }

    @PatchMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<ProductResponseDto>> updateStatus(
            @PathVariable String id,
            @RequestBody ProductStatusUpdateDto dto,
            Principal principal) {
        ProductResponseDto product = productService.approveOrRejectProduct(id, dto, principal.getName());
        return ResponseEntity.ok(
                StandardResponse.success("Product status updated to " + dto.getStatus(), product)
        );
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<StandardResponse<Page<ProductResponseDto>>> getAllForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductResponseDto> allProducts = productService.getAllProductsForAdmin(page, size);
        return ResponseEntity.ok(
                StandardResponse.success("All products retrieved for administration", allProducts)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<StandardResponse<Void>> deleteProduct(@PathVariable String id, Principal principal) {
        productService.deleteProduct(id, principal.getName());
        return ResponseEntity.ok(
                StandardResponse.success("Product archived successfully", null)
        );
    }

    @PatchMapping("/toggle-availability/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<StandardResponse<Void>> toggleAvailability(
            @PathVariable String id,
            @RequestParam boolean isAvailable,
            Principal principal) {
        productService.toggleProductAvailability(id, isAvailable, principal.getName());
        String message = isAvailable ? "Product is now available" : "Product is now unavailable";
        return ResponseEntity.ok(
                StandardResponse.success(message, null)
        );
    }
}
