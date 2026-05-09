package com.retailstore.billing.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountBreakDown {
    private BigDecimal percentageDiscount;
    private BigDecimal flatDiscount;
    private BigDecimal totalDiscount;
}
