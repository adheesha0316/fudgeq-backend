package com.fudgeq.api.repo;

import com.fudgeq.api.entity.CartItem;
import com.fudgeq.api.entity.Product;
import com.fudgeq.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<CartItem, String> {

    // Find all items in a specific user's cart
    List<CartItem> findByUser(User user);

    // Find if a specific product is already in the user's cart
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    // Clear cart after a successful order
    void deleteByUser(User user);
}
