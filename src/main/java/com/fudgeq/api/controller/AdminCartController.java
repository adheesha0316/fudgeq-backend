package com.fudgeq.api.controller;

import com.fudgeq.api.dto.CartResponseDto;
import com.fudgeq.api.dto.StandardResponse;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<StandardResponse<List<CartResponseDto>>> getUserCart(@PathVariable String userId) {
        List<CartResponseDto> userCart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(
                StandardResponse.success("Cart retrieved for user: " + userId, userCart)
        );
    }

    @GetMapping("/all-active")
    public ResponseEntity<StandardResponse<List<CartResponseDto>>> getAllActiveCarts() {
        List<CartResponseDto> activeCarts = cartService.getAllActiveCarts();
        return ResponseEntity.ok(
                StandardResponse.success("All active carts in the system retrieved", activeCarts)
        );
    }
}
