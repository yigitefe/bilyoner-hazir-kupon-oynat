package com.bilyoner.assignment.couponapi.service;

import com.bilyoner.assignment.couponapi.entity.CouponEntity;
import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.exception.CouponApiException;
import com.bilyoner.assignment.couponapi.model.CouponCreateRequest;
import com.bilyoner.assignment.couponapi.model.CouponDTO;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;
import com.bilyoner.assignment.couponapi.model.enums.EventTypeEnum;
import com.bilyoner.assignment.couponapi.repository.CouponRepository;
import com.bilyoner.assignment.couponapi.repository.CouponSelectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    private static final BigDecimal SINGLE_COUPON_PRICE_TL = BigDecimal.valueOf(5);

    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CouponSelectionRepository couponSelectionRepository;
    @Mock
    private EventService eventService;
    @Mock
    private BalanceService balanceService;

    private CouponService couponService;

    @BeforeEach
    void setUp() {
        couponService = new CouponService(couponRepository, couponSelectionRepository, eventService, balanceService);
    }

    @Test
    @DisplayName("Should get all coupons by coupon status")
    void shouldGetAllCouponsByCouponStatus() {
        // TODO
    }

    @Test
    @DisplayName("Should create coupon")
    void shouldCreateCoupon() {
        EventEntity e1 = EventEntity.builder()
                .name("Anadolu Efes-Galatasaray")
                .mbs(2)
                .type(EventTypeEnum.BASKETBALL)
                .eventDate(LocalDateTime.now().plusHours(1))
                .build();
        EventEntity e2 = EventEntity.builder()
                .name("Türk Telekom-Darüşşafaka")
                .mbs(2)
                .type(EventTypeEnum.BASKETBALL)
                .eventDate(LocalDateTime.now().plusHours(2))
                .build();
        CouponEntity c = CouponEntity.builder()
                .id(1L)
                .status(CouponStatusEnum.CREATED)
                .cost(SINGLE_COUPON_PRICE_TL)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        List<Long> eventIds = Arrays.asList(e1.getId(), e2.getId());
        CouponCreateRequest request = CouponCreateRequest.builder()
                .eventIds(eventIds).build();

        when(eventService.getEventsById(any())).thenReturn(Arrays.asList(e1, e2));
        when(couponRepository.save(any())).thenReturn(c);
        when(couponSelectionRepository.saveAll(any())).thenReturn(any());

        CouponDTO expectedDTO = CouponDTO.mapToCouponDTO(c, eventIds);
        CouponDTO returnedDTO = couponService.createCoupon(request);

        assertAll(() -> assertEquals(expectedDTO.getId(), returnedDTO.getId()),
                () -> assertEquals(expectedDTO.getCost(), returnedDTO.getCost()),
                () -> assertEquals(expectedDTO.getStatus(), returnedDTO.getStatus()),
                () -> assertEquals(expectedDTO.getPlayDate(), returnedDTO.getPlayDate()),
                () -> assertEquals(expectedDTO.getUserId(), returnedDTO.getUserId()),
                () -> assertIterableEquals(expectedDTO.getEventIds(), returnedDTO.getEventIds()));
    }

    @Test
    @DisplayName("Should throw incompatible events error")
    void shouldThrowIncompatibleEventsError() {
        EventEntity e1 = EventEntity.builder()
                .name("Anadolu Efes-Galatasaray")
                .mbs(2)
                .type(EventTypeEnum.FOOTBALL)
                .eventDate(LocalDateTime.now().plusHours(1))
                .build();
        EventEntity e2 = EventEntity.builder()
                .name("Türk Telekom-Darüşşafaka")
                .mbs(2)
                .type(EventTypeEnum.TENNIS)
                .eventDate(LocalDateTime.now().plusHours(2))
                .build();
        CouponEntity c = CouponEntity.builder()
                .id(1L)
                .status(CouponStatusEnum.CREATED)
                .cost(SINGLE_COUPON_PRICE_TL)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        List<Long> eventIds = Arrays.asList(e1.getId(), e2.getId());
        CouponCreateRequest request = CouponCreateRequest.builder()
                .eventIds(eventIds).build();

        when(eventService.getEventsById(any())).thenReturn(Arrays.asList(e1, e2));

        CouponApiException ex = assertThrows(CouponApiException.class, () -> couponService.createCoupon(request));
        assertEquals("Incompatible events found.", ex.getErrorCode().getMessage());
        assertEquals(1004, ex.getErrorCode().getCode());
    }

    @Test
    void playCoupons() {
        // TODO
    }

    @Test
    void cancelCoupon() {
        // TODO
    }

    @Test
    void getPlayedCoupons() {
        // TODO
    }
}
