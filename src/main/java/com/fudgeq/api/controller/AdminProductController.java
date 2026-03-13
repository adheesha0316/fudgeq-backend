package com.fudgeq.api.controller;

import com.fudgeq.api.dto.ProductRequestDto;
import com.fudgeq.api.dto.ProductResponseDto;
import com.fudgeq.api.dto.ProductStatusUpdateDto;
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
    public ResponseEntity<ProductResponseDto> createProduct(
            @ModelAttribute ProductRequestDto dto,
            Principal principal) {
        return ResponseEntity.ok(productService.createProduct(dto, principal.getName()));
    }

    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable String id,
            @ModelAttribute ProductRequestDto dto,
            Principal principal) {
        return ResponseEntity.ok(productService.updateProduct(id, dto, principal.getName()));
    }

    @PatchMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> updateStatus(
            @PathVariable String id,
            @RequestBody ProductStatusUpdateDto dto,
            Principal principal) {
        return ResponseEntity.ok(productService.approveOrRejectProduct(id, dto, principal.getName()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<ProductResponseDto>> getAllForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.getAllProductsForAdmin(page, size));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<String> deleteProduct(@PathVariable String id, Principal principal) {
        productService.deleteProduct(id, principal.getName());
        return ResponseEntity.ok("Product archived successfully.");
    }

    @PatchMapping("/toggle-availability/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Void> toggleAvailability(
            @PathVariable String id,
            @RequestParam boolean isAvailable,
            Principal principal) {
        productService.toggleProductAvailability(id, isAvailable, principal.getName());
        return ResponseEntity.ok().build();
    }
}
