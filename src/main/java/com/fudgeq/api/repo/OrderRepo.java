package com.fudgeq.api.repo;

import com.fudgeq.api.entity.Order;
import com.fudgeq.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order, String> {

    // For Customer to see their history
    Page<Order> findByUser(User user, Pageable pageable);

    // For Admin to filter by user
    Page<Order> findByUserId(String userId, Pageable pageable);
}
