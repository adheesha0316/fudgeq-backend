package com.fudgeq.api.controller;

import com.fudgeq.api.dto.CartResponseDto;
import com.fudgeq.api.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/carts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AdminCartController {

    private final CartService cartService;

    // To see a specific user's current cart (Helpful for customer support or AI analysis)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartResponseDto>> getUserCart(@PathVariable String userId) {
        // Note: You'll need to add a method in CartService to get cart by userId
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    // To see all active carts in the system (For global analytics)
    @GetMapping("/all-active")
    public ResponseEntity<List<CartResponseDto>> getAllActiveCarts() {
        return ResponseEntity.ok(cartService.getAllActiveCarts());
    }
}
