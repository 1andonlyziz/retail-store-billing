package com.retailstore.billing.model.mongo;

import com.retailstore.billing.model.enums.ItemType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseItem {
    private String name;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private ItemType type;
    private String description;
}
