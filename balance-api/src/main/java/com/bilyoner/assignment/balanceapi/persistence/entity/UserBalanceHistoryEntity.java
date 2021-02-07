package com.bilyoner.assignment.balanceapi.persistence.entity;

import com.bilyoner.assignment.balanceapi.model.enums.TransactionTypeEnum;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceHistoryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserBalanceEntity userBalance;

    @Column(nullable = false)
    private BigDecimal oldAmount;

    @Column(nullable = false)
    private BigDecimal newAmount;

    @Column(nullable = false)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionTypeEnum transactionType;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createDate;

}
