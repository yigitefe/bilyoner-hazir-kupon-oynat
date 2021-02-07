package com.bilyoner.assignment.couponapi.repository;

import com.bilyoner.assignment.couponapi.entity.CouponEntity;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    List<CouponEntity> findByUserIdAndStatus(Long userId, CouponStatusEnum status);
    List<CouponEntity> findByStatus(CouponStatusEnum status);
}
