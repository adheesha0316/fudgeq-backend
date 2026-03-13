package com.fudgeq.api.service;

import com.fudgeq.api.dto.ProductRequestDto;
import com.fudgeq.api.dto.ProductResponseDto;
import com.fudgeq.api.dto.ProductStatusUpdateDto;
import org.springframework.data.domain.Page;

public interface ProductService {
    // 1. Creation & Approval
    ProductResponseDto createProduct(ProductRequestDto dto, String moderatorEmail);
    ProductResponseDto approveOrRejectProduct(String productId, ProductStatusUpdateDto dto, String adminEmail);

    // 2. Retrieval (Customer & Admin views)
    Page<ProductResponseDto> getActiveProducts(int page, int size); // Only APPROVED & isAvailable=true
    Page<ProductResponseDto> getAllProductsForAdmin(int page, int size); // Everything including PENDING
    ProductResponseDto getProductById(String productId);

    // 3. Update Logic
    ProductResponseDto updateProduct(String productId, ProductRequestDto dto, String actorEmail);

    // 4. Availability & Visibility Control
    void toggleProductAvailability(String productId, boolean isAvailable, String actorEmail); // Hide from shop without deleting
    void setSignatureProduct(String productId, boolean isSignature, String actorEmail);

    // 5. Delete Logic (Soft Delete is better for records)
    void deleteProduct(String productId, String actorEmail);
}
