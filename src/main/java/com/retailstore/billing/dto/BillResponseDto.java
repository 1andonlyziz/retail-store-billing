package com.retailstore.billing.dto;


import com.retailstore.billing.model.enums.UserType;
import com.retailstore.billing.model.mongo.BillEntity;
import com.retailstore.billing.model.mongo.BillItem;
import com.retailstore.billing.model.mongo.DiscountBreakDown;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillResponseDto {

    private String id;
    private String userId;
    private UserType userType;
    private List<BillItem> billItems;

    private BigDecimal totalAmount;
    private DiscountBreakDown discountBreakDown;
    private BigDecimal netPayableAmount;

    private LocalDateTime createdAt;


    public static BillResponseDto toDto(BillEntity billEntity) {
        return BillResponseDto.builder()
                .id(billEntity.getId())
                .userId(billEntity.getUserId())
                .userType(billEntity.getUserType())
                .discountBreakDown(billEntity.getDiscountBreakDown())
                .billItems(billEntity.getBillItems())
                .totalAmount(billEntity.getTotalAmount())
                .netPayableAmount(billEntity.getNetPayableAmount())
                .createdAt(billEntity.getCreatedAt())
                .build();
    }
}
