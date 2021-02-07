package com.bilyoner.assignment.balanceapi.service;

import com.bilyoner.assignment.balanceapi.exception.BalanceApiException;
import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.model.enums.TransactionTypeEnum;
import com.bilyoner.assignment.balanceapi.persistence.entity.UserBalanceEntity;
import com.bilyoner.assignment.balanceapi.persistence.repository.UserBalanceHistoryRepository;
import com.bilyoner.assignment.balanceapi.persistence.repository.UserBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private UserBalanceRepository userBalanceRepository;
    @Mock
    private UserBalanceHistoryRepository userBalanceHistoryRepository;

    private BalanceService balanceService;

    @BeforeEach
    void setUp() {
        balanceService = new BalanceService(userBalanceRepository, userBalanceHistoryRepository);
    }

    @Test
    @DisplayName("Should deposit successfully")
    void shouldDepositSuccessfully() {
        UpdateBalanceRequest request = UpdateBalanceRequest.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(10))
                .transactionId("1234")
                .transactionType(TransactionTypeEnum.DEPOSIT)
                .build();
        UserBalanceEntity fetchedUserBalance = UserBalanceEntity.builder()
                .userId(request.getUserId())
                .amount(BigDecimal.valueOf(10))
                .build();
        UserBalanceEntity returnedUserBalance = UserBalanceEntity.builder()
                .userId(request.getUserId())
                .amount(BigDecimal.valueOf(20))
                .build();

        doReturn(fetchedUserBalance).when(userBalanceRepository).findByUserId(request.getUserId());
        doReturn(returnedUserBalance).when(userBalanceRepository).save(any());

        assertDoesNotThrow(() -> balanceService.updateBalance(request));
    }

    @Test
    @DisplayName("Should throw not supported transaction error")
    void shouldThrowNotSupportedTransactionError() {
        UpdateBalanceRequest request = UpdateBalanceRequest.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(10))
                .transactionId("1234")
                .transactionType(null)
                .build();
        UserBalanceEntity fetchedUserBalance = UserBalanceEntity.builder()
                .userId(request.getUserId())
                .amount(BigDecimal.valueOf(10))
                .build();

        doReturn(fetchedUserBalance).when(userBalanceRepository).findByUserId(request.getUserId());

        BalanceApiException ex = assertThrows(BalanceApiException.class, () -> balanceService.updateBalance(request));
        assertEquals("Not supported transaction type.", ex.getErrorCode().getMessage());
        assertEquals(1005, ex.getErrorCode().getCode());
    }

    @Test
    @DisplayName("Should throw user not found error")
    void shouldThrowUserNotFoundError() {
        UpdateBalanceRequest request = UpdateBalanceRequest.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(10))
                .transactionId("1234")
                .transactionType(TransactionTypeEnum.WITHDRAW)
                .build();

        doReturn(null).when(userBalanceRepository).findByUserId(request.getUserId());

        BalanceApiException ex = assertThrows(BalanceApiException.class, () -> balanceService.updateBalance(request));
        assertEquals("User not found.", ex.getErrorCode().getMessage());
        assertEquals(1004, ex.getErrorCode().getCode());
    }

    @Test
    @DisplayName("Should fail to withdraw if balance is insufficient")
    void shouldFailToWithdrawIfBalanceInsufficient() {
        UpdateBalanceRequest request = UpdateBalanceRequest.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(20))
                .transactionId("1234")
                .transactionType(TransactionTypeEnum.WITHDRAW)
                .build();
        UserBalanceEntity fetchedUserBalance = UserBalanceEntity.builder()
                .userId(request.getUserId())
                .amount(BigDecimal.valueOf(10))
                .build();

        doReturn(fetchedUserBalance).when(userBalanceRepository).findByUserId(request.getUserId());

        BalanceApiException ex = assertThrows(BalanceApiException.class, () -> balanceService.updateBalance(request));
        assertEquals("Insufficient balance.", ex.getErrorCode().getMessage());
        assertEquals(1003, ex.getErrorCode().getCode());
    }

}
