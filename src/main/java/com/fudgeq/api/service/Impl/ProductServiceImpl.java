package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.ProductRequestDto;
import com.fudgeq.api.dto.ProductResponseDto;
import com.fudgeq.api.dto.ProductStatusUpdateDto;
import com.fudgeq.api.entity.Product;
import com.fudgeq.api.enums.ProductStatus;
import com.fudgeq.api.repo.ProductRepo;
import com.fudgeq.api.service.AuditService;
import com.fudgeq.api.service.FileStorageService;
import com.fudgeq.api.service.ProductService;
import com.fudgeq.api.utill.AppConstants;
import com.fudgeq.api.utill.CustomIdGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final FileStorageService fileStorageService;
    private final AuditService auditService;
    private final CustomIdGenerator idGenerator;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto dto, String moderatorEmail) {
        String productId = idGenerator.generateNextId(AppConstants.PREFIX_PRODUCT);

        // Upload images (Max 5 validation should be in Controller/DTO level or here)
        List<String> imageUrls = fileStorageService.storeMultipleFiles(dto.getImages(), "products");

        Product product = Product.builder()
                .productId(productId)
                .name(dto.getName())
                .description(dto.getDescription())
                .ingredients(dto.getIngredients())
                .price(dto.getPrice())
                .dailyCapacity(dto.getDailyCapacity())
                .addedBy(moderatorEmail)
                .imageUrls(imageUrls)
                .status(ProductStatus.PENDING_APPROVAL)
                .isAvailable(true)
                .isSignature(dto.isSignature())
                .weightGrams(dto.getWeightGrams())
                .build();

        Product savedProduct = productRepo.save(product);

        auditService.logAction(moderatorEmail, "PRODUCT_CREATE",
                "New product submitted: " + product.getName(), productId, false);

        return mapper.map(savedProduct, ProductResponseDto.class);
    }

    @Override
    @Transactional
    public ProductResponseDto approveOrRejectProduct(String productId, ProductStatusUpdateDto dto, String adminEmail) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(dto.getStatus());

        // If admin adjusted price during approval
        if (dto.getAdjustedPrice() != null) {
            product.setPrice(dto.getAdjustedPrice());
        }

        Product updated = productRepo.save(product);

        String logDetail = "Product " + dto.getStatus() + ". Remarks: " + dto.getAdminRemarks();
        auditService.logAction(adminEmail, "PRODUCT_STATUS_UPDATE", logDetail, productId, true);

        return mapper.map(updated, ProductResponseDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getActiveProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Custom repo method to find only APPROVED and Available products
        return productRepo.findByStatus(ProductStatus.APPROVED, pageable)
                .map(product -> mapper.map(product, ProductResponseDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProductsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepo.findAll(pageable)
                .map(product -> mapper.map(product, ProductResponseDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(String productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapper.map(product, ProductResponseDto.class);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(String productId, ProductRequestDto dto, String actorEmail) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setIngredients(dto.getIngredients());
        product.setPrice(dto.getPrice());
        product.setDailyCapacity(dto.getDailyCapacity());
        product.setWeightGrams(dto.getWeightGrams());
        product.setSignature(dto.isSignature());

        // Update images only if new ones are provided
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<String> newImages = fileStorageService.storeMultipleFiles(dto.getImages(), "products");
            product.setImageUrls(newImages);
        }

        // Reset to PENDING after major update
        product.setStatus(ProductStatus.PENDING_APPROVAL);

        Product updated = productRepo.save(product);

        auditService.logAction(actorEmail, "PRODUCT_UPDATE",
                "Updated product: " + product.getName(), productId, false);

        return mapper.map(updated, ProductResponseDto.class);
    }

    @Override
    @Transactional
    public void toggleProductAvailability(String productId, boolean isAvailable, String actorEmail) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setAvailable(isAvailable);
        productRepo.save(product);

        String action = isAvailable ? "PRODUCT_VISIBLE" : "PRODUCT_HIDDEN";
        auditService.logAction(actorEmail, action, "Availability changed for: " + product.getName(), productId, false);
    }

    @Override
    @Transactional
    public void setSignatureProduct(String productId, boolean isSignature, String actorEmail) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setSignature(isSignature);
        productRepo.save(product);

        auditService.logAction(actorEmail, "PRODUCT_SIGNATURE_TOGGLE",
                "Signature status changed for: " + product.getName(), productId, false);
    }

    @Override
    @Transactional
    public void deleteProduct(String productId, String actorEmail) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Soft delete: Change status to ARCHIVED
        product.setStatus(ProductStatus.ARCHIVED);
        product.setAvailable(false);
        productRepo.save(product);

        auditService.logAction(actorEmail, "PRODUCT_DELETE",
                "Archived product: " + product.getName(), productId, true);
    }
}
