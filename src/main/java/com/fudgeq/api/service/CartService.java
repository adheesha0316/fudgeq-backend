package com.fudgeq.api.service;

import com.fudgeq.api.dto.CartRequestDto;
import com.fudgeq.api.dto.CartResponseDto;

import java.util.List;

public interface CartService {
    CartResponseDto addToCart(CartRequestDto dto);
    List<CartResponseDto> getMyCart();
    void removeFromCart(String cartItemId);
    void updateQuantity(String cartItemId, int quantity);

    // Admin methods
    List<CartResponseDto> getCartByUserId(String userId);
    List<CartResponseDto> getAllActiveCarts();
}
