package com.fudgeq.api.service;

import com.fudgeq.api.dto.CouponRequestDto;
import com.fudgeq.api.dto.CouponResponseDto;

import java.util.List;

public interface CouponService {
    CouponResponseDto createCoupon(CouponRequestDto dto);
    List<CouponResponseDto> getAllCoupons();
    CouponResponseDto getCouponByCode(String code);
    void deleteCoupon(String couponId);
    void toggleCouponStatus(String couponId, boolean status);

    // Logic to validate and calculate discount
    CouponResponseDto validateCoupon(String code, String productId);
}
