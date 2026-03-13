package com.fudgeq.api.controller;

import com.fudgeq.api.dto.CartRequestDto;
import com.fudgeq.api.dto.CartResponseDto;
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
    @PreAuthorize("hasRole('CUSTOMER')") // Only registered customers can add to cart
    public ResponseEntity<CartResponseDto> addToCart(@Valid @RequestBody CartRequestDto dto) {
        return ResponseEntity.ok(cartService.addToCart(dto));
    }

    @GetMapping("/my-cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<CartResponseDto>> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @DeleteMapping("/remove/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> removeFromCart(@PathVariable String cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-quantity/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> updateQuantity(
            @PathVariable String cartItemId,
            @RequestParam int quantity) {
        cartService.updateQuantity(cartItemId, quantity);
        return ResponseEntity.ok().build();
    }
}
