package com.bilyoner.assignment.balanceapi.model;

import com.bilyoner.assignment.balanceapi.model.enums.TransactionTypeEnum;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceRequest {

    @NotNull
    private Long userId;
    @NotNull
    @Min(0)
    private BigDecimal amount;
    @NotBlank
    private String transactionId;
    @NotNull
    private TransactionTypeEnum transactionType;
}
