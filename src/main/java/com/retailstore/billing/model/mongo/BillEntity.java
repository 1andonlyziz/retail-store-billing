package com.retailstore.billing.model.mongo;


import com.retailstore.billing.model.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "calculated_bills")
public class BillEntity {

    @Id
    private String id;
    private String userId;
    private UserType userType;
    private List<BillItem> billItems;

    private BigDecimal totalAmount;
    private DiscountBreakDown discountBreakDown;
    private BigDecimal netPayableAmount;

    private LocalDateTime createdAt;

}
