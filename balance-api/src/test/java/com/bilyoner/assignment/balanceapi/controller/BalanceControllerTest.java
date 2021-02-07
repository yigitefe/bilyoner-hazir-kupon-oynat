package com.bilyoner.assignment.balanceapi.controller;

import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.model.enums.TransactionTypeEnum;
import com.bilyoner.assignment.balanceapi.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class BalanceControllerTest {

    @Mock
    private BalanceService balanceService;

    private BalanceController balanceController;

    @BeforeEach
    void setUp() {
        balanceController = new BalanceController(balanceService);
    }

    @Test
    @DisplayName("Should successfully return from update balance")
    void shouldSuccessfullyReturnFromUpdateBalance() {
        UpdateBalanceRequest request = UpdateBalanceRequest.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(5))
                .transactionId("1234")
                .transactionType(TransactionTypeEnum.WITHDRAW)
                .build();

        assertDoesNotThrow(() -> balanceController.updateBalance(request));
    }
}
