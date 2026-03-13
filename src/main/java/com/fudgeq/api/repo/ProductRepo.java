package com.fudgeq.api.repo;

import com.fudgeq.api.entity.Product;
import com.fudgeq.api.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, String> {

    // Find products by status (e.g., all APPROVED products for customers)
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    // Find products added by a specific moderator
    Page<Product> findByAddedBy(String email, Pageable pageable);

    // Find signature products for the homepage
    Page<Product> findByIsSignatureTrueAndStatus(ProductStatus status, Pageable pageable);
}
