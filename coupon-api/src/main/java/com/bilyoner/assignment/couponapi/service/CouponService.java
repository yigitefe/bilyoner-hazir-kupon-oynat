package com.bilyoner.assignment.couponapi.service;

import com.bilyoner.assignment.couponapi.entity.CouponEntity;
import com.bilyoner.assignment.couponapi.entity.CouponSelectionEntity;
import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.exception.CouponApiException;
import com.bilyoner.assignment.couponapi.exception.ErrorCodeEnum;
import com.bilyoner.assignment.couponapi.model.CouponCreateRequest;
import com.bilyoner.assignment.couponapi.model.CouponDTO;
import com.bilyoner.assignment.couponapi.model.CouponPlayRequest;
import com.bilyoner.assignment.couponapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;
import com.bilyoner.assignment.couponapi.model.enums.EventTypeEnum;
import com.bilyoner.assignment.couponapi.model.enums.TransactionTypeEnum;
import com.bilyoner.assignment.couponapi.repository.CouponRepository;
import com.bilyoner.assignment.couponapi.repository.CouponSelectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private static final BigDecimal SINGLE_COUPON_PRICE_TL = BigDecimal.valueOf(5);
    private static final int COUPON_CANCEL_PERIOD_IN_MINUTES = 5;

    private final CouponRepository couponRepository;
    private final CouponSelectionRepository couponSelectionRepository;
    private final EventService eventService;
    private final BalanceService balanceService;

    public List<CouponDTO> getAllCouponsByCouponStatus(CouponStatusEnum couponStatus) {
        List<CouponEntity> coupons = couponRepository.findByStatus(couponStatus);
        return mapAllToCouponDTO(coupons);
    }

    @Transactional
    public CouponDTO createCoupon(CouponCreateRequest couponCreateRequest) {
        List<EventEntity> selectedEvents = eventService.getEventsById(couponCreateRequest.getEventIds());
        validateSelectedEvents(selectedEvents);

        final CouponEntity createdCouponEntity = couponRepository.save(CouponEntity.builder()
                .status(CouponStatusEnum.CREATED)
                .cost(SINGLE_COUPON_PRICE_TL)
                .build());

        addCouponSelections(createdCouponEntity, selectedEvents);

        final CouponDTO response = CouponDTO.mapToCouponDTO(createdCouponEntity, couponCreateRequest.getEventIds());

        return response;
    }

    private void validateSelectedEvents(List<EventEntity> selectedEvents) {
        checkExpiredEvents(selectedEvents);
        checkIncompatibleEvents(selectedEvents);
        checkMBSRules(selectedEvents);
    }

    private void checkExpiredEvents(List<EventEntity> events) {
        if (containsExpiredEvent(events))
            throw new CouponApiException(ErrorCodeEnum.EXPIRED_EVENT_ERROR);
    }

    private boolean containsExpiredEvent(List<EventEntity> events) {
        return events.stream().filter(i -> LocalDateTime.now().isAfter(i.getEventDate())).findAny().isPresent();
    }

    private void checkIncompatibleEvents(List<EventEntity> events) {
        boolean foundFootball = containsEventType(events, EventTypeEnum.FOOTBALL);
        boolean foundTennis = containsEventType(events, EventTypeEnum.TENNIS);
        if (foundFootball && foundTennis)
            throw new CouponApiException(ErrorCodeEnum.INCOMPATIBLE_EVENTS_ERROR);
    }

    private boolean containsEventType(List<EventEntity> events, EventTypeEnum eventType) {
        return events.stream().filter(i -> eventType.equals(i.getType())).findAny().isPresent();
    }

    private void checkMBSRules(List<EventEntity> events) {
        int eventCount = events.size();
        int highestMBS = events.stream().map(i -> i.getMbs()).max(Integer::compareTo).get();
        if (highestMBS > eventCount)
            throw new CouponApiException(ErrorCodeEnum.MBS_CHECK_ERROR);
    }

    private void addCouponSelections(CouponEntity createdCouponEntity, List<EventEntity> selectedEvents) {
        List<CouponSelectionEntity> selections = new ArrayList<>();
        for (EventEntity event : selectedEvents)
            selections.add(buildCouponSelectionEntity(createdCouponEntity, event));
        couponSelectionRepository.saveAll(selections);
    }

    private CouponSelectionEntity buildCouponSelectionEntity(CouponEntity createdCouponEntity, EventEntity event) {
        return CouponSelectionEntity.builder()
                .coupon(createdCouponEntity)
                .event(event)
                .build();
    }

    @Transactional
    public List<CouponDTO> playCoupons(CouponPlayRequest couponPlayRequest) {
        List<CouponEntity> coupons = couponRepository.findAllById(couponPlayRequest.getCouponIds());
        checkCouponsToPlay(coupons, couponPlayRequest);
        BigDecimal totalCouponCost = calculateTotalCouponCost(coupons);
        updateUserBalance(couponPlayRequest.getUserId(), totalCouponCost, TransactionTypeEnum.WITHDRAW);
        playCoupons(couponPlayRequest.getUserId(), coupons);
        return mapAllToCouponDTO(coupons);
    }

    private List<CouponDTO> mapAllToCouponDTO(List<CouponEntity> coupons) {
        return coupons.stream().map(i -> mapToCouponDTO(i)).collect(Collectors.toList());
    }

    private CouponDTO mapToCouponDTO(CouponEntity coupon) {
        List<CouponSelectionEntity> couponSelections = couponSelectionRepository.findByCoupon(coupon);
        List<Long> eventIds = couponSelections.stream().map(i -> i.getEvent().getId()).collect(Collectors.toList());
        return CouponDTO.mapToCouponDTO(coupon, eventIds);
    }

    private void updateUserBalance(Long userId, BigDecimal amount, TransactionTypeEnum transactionType) {
        UpdateBalanceRequest request = UpdateBalanceRequest.builder()
                .userId(userId)
                .transactionType(transactionType)
                .amount(amount)
                .transactionId(generateTransactionId())
                .build();
        balanceService.updateBalance(request);
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    private BigDecimal calculateTotalCouponCost(List<CouponEntity> coupons) {
        return coupons.stream().map(i -> i.getCost())
                .reduce(BigDecimal.ZERO, (subtotal, element) -> element.add(subtotal)
                        .setScale(2, RoundingMode.HALF_DOWN));
    }

    private void playCoupons(Long userId, List<CouponEntity> coupons) {
        for (CouponEntity coupon : coupons) {
            coupon.setUserId(userId);
            coupon.setStatus(CouponStatusEnum.PLAYED);
            coupon.setPlayDate(LocalDateTime.now());
        }
        couponRepository.saveAll(coupons);
    }

    private void checkCouponsToPlay(List<CouponEntity> coupons, CouponPlayRequest request) {
        if (coupons.size() < request.getCouponIds().size())
            throw new CouponApiException(ErrorCodeEnum.COUPON_NOT_FOUND_ERROR);
        boolean foundPlayedCoupon = coupons.stream().anyMatch(i -> CouponStatusEnum.PLAYED.equals(i.getStatus()));
        if (foundPlayedCoupon) {
            throw new CouponApiException(ErrorCodeEnum.RECURRING_COUPON_PLAY_ERROR);
        }
    }

    @Transactional
    public CouponDTO cancelCoupon(Long couponId) {
        Optional<CouponEntity> couponOpt = couponRepository.findById(couponId);
        if (!couponOpt.isPresent()) {
            throw new CouponApiException(ErrorCodeEnum.COUPON_NOT_FOUND_ERROR);
        }
        CouponEntity coupon = couponOpt.get();
        checkCouponCancelPeriod(coupon);
        updateUserBalance(coupon.getUserId(), coupon.getCost(), TransactionTypeEnum.DEPOSIT);
        cancelCoupon(coupon);
        return mapToCouponDTO(coupon);
    }

    private void cancelCoupon(CouponEntity coupon) {
        coupon.setPlayDate(null);
        coupon.setStatus(CouponStatusEnum.CREATED);
        coupon.setUserId(null);
        couponRepository.save(coupon);
    }

    private void checkCouponCancelPeriod(CouponEntity coupon) {
        if (isCancelPeriodExpired(coupon)) {
            throw new CouponApiException(ErrorCodeEnum.CANCEL_PERIOD_EXPIRED_ERROR);
        }
    }

    private boolean isCancelPeriodExpired(CouponEntity coupon) {
        return LocalDateTime.now().isAfter(coupon.getPlayDate().plusMinutes(COUPON_CANCEL_PERIOD_IN_MINUTES));
    }

    public List<CouponDTO> getPlayedCoupons(Long userId) {
        List<CouponEntity> playedCoupons = couponRepository.findByUserIdAndStatus(userId, CouponStatusEnum.PLAYED);
        return mapAllToCouponDTO(playedCoupons);
    }
}
