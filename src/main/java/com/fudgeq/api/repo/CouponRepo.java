package com.fudgeq.api.repo;

import com.fudgeq.api.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepo extends JpaRepository<Coupon, String> {
    Optional<Coupon> findByCodeAndIsActiveTrue(String code);
    boolean existsByCode(String code);
}
