package com.fudgeq.api.controller;

import com.fudgeq.api.dto.CartRequestDto;
import com.fudgeq.api.dto.CartResponseDto;
import com.fudgeq.api.dto.StandardResponse;
import com.fudgeq.api.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@CrossOrigin
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<StandardResponse<CartResponseDto>> addToCart(@Valid @RequestBody CartRequestDto dto) {
        CartResponseDto cartItem = cartService.addToCart(dto);
        return ResponseEntity.ok(
                StandardResponse.success("Item added to cart successfully", cartItem)
        );
    }

    @GetMapping("/my-cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<StandardResponse<List<CartResponseDto>>> getMyCart() {
        List<CartResponseDto> cart = cartService.getMyCart();
        return ResponseEntity.ok(
                StandardResponse.success("Cart retrieved successfully", cart)
        );
    }

    @DeleteMapping("/remove/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<StandardResponse<Void>> removeFromCart(@PathVariable String cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok(
                StandardResponse.success("Item removed from cart", null)
        );
    }

    @PatchMapping("/update-quantity/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<StandardResponse<Void>> updateQuantity(
            @PathVariable String cartItemId,
            @RequestParam int quantity) {
        cartService.updateQuantity(cartItemId, quantity);
        return ResponseEntity.ok(
                StandardResponse.success("Cart quantity updated to " + quantity, null)
        );
    }
}
