package com.bilyoner.assignment.couponapi.controller;

import com.bilyoner.assignment.couponapi.model.CouponCreateRequest;
import com.bilyoner.assignment.couponapi.model.CouponDTO;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;
import com.bilyoner.assignment.couponapi.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CouponControllerTest {

    @Mock
    private CouponService couponService;

    private CouponController couponController;

    @BeforeEach
    void setUp() {
        couponController = new CouponController(couponService);
    }

    @Test
    @DisplayName("Should return expected DTO from create coupon")
    void shouldReturnExpectedDtoFromCreateCoupon() {
        CouponDTO couponDTO = CouponDTO.builder()
                .id(123L)
                .cost(BigDecimal.valueOf(5))
                .status(CouponStatusEnum.CREATED)
                .eventIds(Arrays.asList(2L, 3L))
                .build();
        CouponCreateRequest couponCreateRequest = CouponCreateRequest.builder()
                .eventIds(Arrays.asList(2L, 3L)).build();

        when(couponService.createCoupon(couponCreateRequest)).thenReturn(couponDTO);

        CouponDTO returnedCouponDTO = couponController.createCoupon(couponCreateRequest);

        assertAll(() -> assertTrue(returnedCouponDTO.getId() > 0),
                () -> assertEquals(couponDTO.getCost(), returnedCouponDTO.getCost()),
                () -> assertEquals(couponDTO.getStatus(), returnedCouponDTO.getStatus()),
                () -> assertIterableEquals(couponDTO.getEventIds(), returnedCouponDTO.getEventIds()));
    }

}
