package com.fudgeq.api.repo;

import com.fudgeq.api.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, String> {
    List<OrderItem> findByOrder_OrderId(String orderId);

    // AI Insight Query: Groups items by product and sums the quantity to find winners
    @Query("SELECT oi FROM OrderItem oi GROUP BY oi.product.productId ORDER BY SUM(oi.quantity) DESC")
    List<OrderItem> findTopSellingProducts(Pageable pageable);
}
