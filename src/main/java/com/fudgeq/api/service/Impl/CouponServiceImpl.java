package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.CouponRequestDto;
import com.fudgeq.api.dto.CouponResponseDto;
import com.fudgeq.api.entity.Coupon;
import com.fudgeq.api.entity.Product;
import com.fudgeq.api.repo.CouponRepo;
import com.fudgeq.api.repo.ProductRepo;
import com.fudgeq.api.service.AuditService;
import com.fudgeq.api.service.CouponService;
import com.fudgeq.api.utill.AppConstants;
import com.fudgeq.api.utill.CustomIdGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepo couponRepo;
    private final ProductRepo productRepo;
    private final AuditService auditService;
    private final CustomIdGenerator idGenerator;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public CouponResponseDto createCoupon(CouponRequestDto dto) {
        if (couponRepo.existsByCode(dto.getCode())) {
            throw new RuntimeException("Coupon code already exists: " + dto.getCode());
        }

        Product product = null;
        if (dto.getProductId() != null && !dto.getProductId().isEmpty()) {
            product = productRepo.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found for the given ID"));
        }

        Coupon coupon = Coupon.builder()
                .couponId(idGenerator.generateNextId(AppConstants.PREFIX_COUPON))
                .code(dto.getCode().toUpperCase())
                .discountPercentage(dto.getDiscountPercentage())
                .expiryDate(dto.getExpiryDate())
                .isActive(true)
                .product(product)
                .build();

        Coupon saved = couponRepo.save(coupon);

        // Log action
        auditService.logAction("ADMIN", "COUPON_CREATE",
                "Created coupon: " + saved.getCode(), saved.getCouponId(), false);

        return convertToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponseDto> getAllCoupons() {
        // Now returns List<Coupon> directly because of JpaRepository
        return couponRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponseDto getCouponByCode(String code) {
        Coupon coupon = couponRepo.findByCodeAndIsActiveTrue(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        return convertToDto(coupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(String couponId) {
        if (!couponRepo.existsById(couponId)) {
            throw new RuntimeException("Coupon not found");
        }
        couponRepo.deleteById(couponId);
    }

    @Override
    @Transactional
    public void toggleCouponStatus(String couponId, boolean status) {
        Coupon coupon = couponRepo.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        coupon.setActive(status);
        couponRepo.save(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDto validateCoupon(String code, String productId) {
        Coupon coupon = couponRepo.findByCodeAndIsActiveTrue(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Invalid or inactive coupon code"));

        // 1. Check Expiry
        if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This coupon has expired");
        }

        // 2. Check Product Specificity
        if (coupon.getProduct() != null) {
            if (productId == null || !coupon.getProduct().getProductId().equals(productId)) {
                throw new RuntimeException("This coupon is only valid for product: " + coupon.getProduct().getName());
            }
        }

        return convertToDto(coupon);
    }

    private CouponResponseDto convertToDto(Coupon coupon) {
        CouponResponseDto dto = mapper.map(coupon, CouponResponseDto.class);
        if (coupon.getProduct() != null) {
            dto.setProductId(coupon.getProduct().getProductId());
            dto.setProductName(coupon.getProduct().getName());
        }
        return dto;
    }
}
