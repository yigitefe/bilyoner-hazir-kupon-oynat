package com.bilyoner.assignment.balanceapi.service;

import com.bilyoner.assignment.balanceapi.exception.BalanceApiException;
import com.bilyoner.assignment.balanceapi.exception.ErrorCodeEnum;
import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.model.enums.TransactionTypeEnum;
import com.bilyoner.assignment.balanceapi.persistence.entity.UserBalanceEntity;
import com.bilyoner.assignment.balanceapi.persistence.entity.UserBalanceHistoryEntity;
import com.bilyoner.assignment.balanceapi.persistence.repository.UserBalanceHistoryRepository;
import com.bilyoner.assignment.balanceapi.persistence.repository.UserBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final UserBalanceRepository userBalanceRepository;
    private final UserBalanceHistoryRepository userBalanceHistoryRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateBalance(UpdateBalanceRequest updateBalanceRequest) {
        UserBalanceEntity userBalance = userBalanceRepository.findByUserId(updateBalanceRequest.getUserId());
        if (userBalance == null)
            throw new BalanceApiException(ErrorCodeEnum.USER_NOT_FOUND_ERROR);
        UserBalanceHistoryEntity userBalanceHistory = createUserBalanceHistory(updateBalanceRequest, userBalance);
        updateUserBalance(updateBalanceRequest, userBalance);
        userBalanceRepository.save(userBalance);
        logUserBalanceHistory(userBalance, userBalanceHistory);
    }

    private void logUserBalanceHistory(UserBalanceEntity newUserBalance, UserBalanceHistoryEntity userBalanceHistory) {
        userBalanceHistory.setNewAmount(newUserBalance.getAmount());
        userBalanceHistory.setCreateDate(LocalDateTime.now());
        userBalanceHistoryRepository.save(userBalanceHistory);
    }

    private UserBalanceHistoryEntity createUserBalanceHistory(UpdateBalanceRequest updateBalanceRequest, UserBalanceEntity userBalance) {
        return UserBalanceHistoryEntity.builder()
                .userBalance(userBalance)
                .oldAmount(userBalance.getAmount())
                .transactionId(updateBalanceRequest.getTransactionId())
                .transactionType(updateBalanceRequest.getTransactionType())
                .build();
    }

    private void updateUserBalance(UpdateBalanceRequest updateBalanceRequest, UserBalanceEntity userBalance) {
        if (isDepositTransaction(updateBalanceRequest)) {
            deposit(userBalance, updateBalanceRequest.getAmount());
        } else if (isWithdrawTransaction(updateBalanceRequest)) {
            withdraw(userBalance, updateBalanceRequest.getAmount());
        } else {
            throw new BalanceApiException(ErrorCodeEnum.NOT_SUPPORTED_TRANSACTION_TYPE_ERROR);
        }
    }

    private void deposit(UserBalanceEntity userBalance, BigDecimal amountToDeposit) {
        userBalance.setAmount(userBalance.getAmount().add(amountToDeposit));
    }

    private void withdraw(UserBalanceEntity userBalance, BigDecimal amountToWithdraw) {
        BigDecimal newAmount = userBalance.getAmount().subtract(amountToWithdraw);
        if (!isAmountNegative(newAmount)) {
            userBalance.setAmount(newAmount);
            return;
        }
        throw new BalanceApiException(ErrorCodeEnum.INSUFFICIENT_BALANCE);
    }

    private boolean isAmountNegative(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    private boolean isDepositTransaction(UpdateBalanceRequest request) {
        return TransactionTypeEnum.DEPOSIT.equals(request.getTransactionType());
    }

    private boolean isWithdrawTransaction(UpdateBalanceRequest request) {
        return TransactionTypeEnum.WITHDRAW.equals(request.getTransactionType());
    }

}
