package com.bilyoner.assignment.couponapi.model;

import com.bilyoner.assignment.couponapi.entity.CouponEntity;
import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CouponDTO implements Serializable {

    private Long id;

    private Long userId;

    @NotNull
    private CouponStatusEnum status;

    @NotNull
    private BigDecimal cost;

    private List<Long> eventIds;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime playDate;

    public static CouponDTO mapToCouponDTO(CouponEntity couponEntity, List<Long> eventIds) {
        return CouponDTO.builder()
                .id(couponEntity.getId())
                .userId(couponEntity.getUserId())
                .cost(couponEntity.getCost())
                .status(couponEntity.getStatus())
                .playDate(couponEntity.getPlayDate())
                .eventIds(eventIds)
                .build();
    }
}
