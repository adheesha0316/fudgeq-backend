package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.CartRequestDto;
import com.fudgeq.api.dto.CartResponseDto;
import com.fudgeq.api.entity.CartItem;
import com.fudgeq.api.entity.Product;
import com.fudgeq.api.entity.User;
import com.fudgeq.api.enums.Role;
import com.fudgeq.api.repo.CartRepo;
import com.fudgeq.api.repo.ProductRepo;
import com.fudgeq.api.service.CartService;
import com.fudgeq.api.service.UserService;
import com.fudgeq.api.utill.AppConstants;
import com.fudgeq.api.utill.CustomIdGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final UserService userService;
    private final CustomIdGenerator idGenerator;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public CartResponseDto addToCart(CartRequestDto dto) {
        User currentUser = userService.getCurrentUserEntity();

        // 🛡️ ROLE VALIDATION: "First you need to register" logic
        if (currentUser.getRole() == Role.VISITOR) {
            throw new RuntimeException("Access Denied: Please register as a Customer to add items to cart.");
        }

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if item already exists in cart
        CartItem cartItem = cartRepo.findByUserAndProduct(currentUser, product)
                .map(item -> {
                    // Update existing quantity
                    item.setQuantity(item.getQuantity() + dto.getQuantity());
                    item.setSubTotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    return item;
                })
                .orElseGet(() -> {
                    // Create new cart item
                    return CartItem.builder()
                            .cartItemId(idGenerator.generateNextId(AppConstants.PREFIX_CART))
                            .user(currentUser)
                            .product(product)
                            .quantity(dto.getQuantity())
                            .unitPrice(product.getPrice())
                            .subTotal(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                            .build();
                });

        CartItem savedItem = cartRepo.save(cartItem);
        return convertToDto(savedItem);
    }

    @Override
    public List<CartResponseDto> getMyCart() {
        User currentUser = userService.getCurrentUserEntity();
        return cartRepo.findByUser(currentUser).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFromCart(String cartItemId) {
        cartRepo.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public void updateQuantity(String cartItemId, int quantity) {
        CartItem item = cartRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        item.setSubTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        cartRepo.save(item);
    }

    @Override
    public List<CartResponseDto> getCartByUserId(String userId) {
        User user = userService.getUserEntityById(userId);
        return cartRepo.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CartResponseDto> getAllActiveCarts() {
        return cartRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Existing helper method
    private CartResponseDto convertToDto(CartItem item) {
        CartResponseDto dto = mapper.map(item, CartResponseDto.class);
        dto.setProductName(item.getProduct().getName());
        if (item.getProduct().getImageUrls() != null && !item.getProduct().getImageUrls().isEmpty()) {
            dto.setProductImage(item.getProduct().getImageUrls().get(0));
        }
        return dto;
    }
}
