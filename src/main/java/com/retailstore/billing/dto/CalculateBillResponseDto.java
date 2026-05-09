package com.retailstore.billing.dto;

import com.retailstore.billing.model.mongo.DiscountBreakDown;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateBillResponseDto {
    private String billId;
    private BigDecimal totalAmount;
    private BigDecimal netPayableAmount;
    private BigDecimal totalDiscount;
    private DiscountBreakDown discountBreakDown;
}
