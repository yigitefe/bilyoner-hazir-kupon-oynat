package com.bilyoner.assignment.couponapi.repository;

import com.bilyoner.assignment.couponapi.entity.CouponEntity;
import com.bilyoner.assignment.couponapi.entity.CouponSelectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponSelectionRepository extends JpaRepository<CouponSelectionEntity, Long> {
    List<CouponSelectionEntity> findByCoupon(CouponEntity coupon);
}
