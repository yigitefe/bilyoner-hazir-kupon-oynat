package com.bilyoner.assignment.balanceapi.model;

import lombok.*;

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
    private BigDecimal amount;
    @NotBlank
    private String transactionId;
    @NotBlank
    private String transactionType;
}
