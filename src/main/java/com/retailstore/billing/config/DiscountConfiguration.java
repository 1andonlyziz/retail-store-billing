package com.retailstore.billing.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Externally configurable discount rates loaded from {@code application.yaml}
 * under the {@code configuration.discount} prefix.
 * <p>
 * Allows discount percentages to be tuned without recompiling.
 */
@Component
@ConfigurationProperties(prefix = "configuration.discount")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountConfiguration {

    private BigDecimal employee;
    private BigDecimal loyal;
    private BigDecimal affiliate;
    private BigDecimal normal;

}
