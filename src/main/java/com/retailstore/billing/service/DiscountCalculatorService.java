package com.retailstore.billing.service;

import com.retailstore.billing.config.DiscountConfiguration;
import com.retailstore.billing.model.jpa.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Encapsulates the discount calculation rules for the retail store.
 * <p>
 * Two types of discounts are supported, both applied only to non-grocery items:
 * <ul>
 *   <li><b>Percentage discount</b> — based on user type (employee, affiliate, loyal customer).</li>
 *   <li><b>Flat discount</b> — $5 for every $100 spent on non-grocery items.</li>
 * </ul>
 * Discount rates are configured via {@link DiscountConfiguration} so they can be
 * adjusted without code changes.
 */
@Service
@AllArgsConstructor
@Slf4j
public class DiscountCalculatorService {

    private final DiscountConfiguration discountConfiguration;

    /**
     * Calculates percentage discount on non-grocery items based on user type.
     * Only one discount applies per bill:
     * - EMPLOYEE: 30%
     * - AFFILIATE: 10%
     * - NORMAL (loyal 2+ years): 5%
     * - NORMAL (under 2 years): 0%
     */
    public BigDecimal calculatePercentageDiscount(BigDecimal nonGroceryTotal, UserEntity user) {
        BigDecimal discountRate = determineDiscountRate(user);
        BigDecimal discount = nonGroceryTotal.multiply(discountRate);
        log.info("Percentage discount rate: {} for user type: {}, discount amount: {}",
                discountRate, user.getUserType(), discount);
        return discount;
    }

    /**
     * Calculates flat discount: $5 for every $100 on non-grocery total.
     */
    public BigDecimal calculateFlatDiscount(BigDecimal nonGroceryTotal) {
        BigDecimal flatDiscount = nonGroceryTotal
                .divideToIntegralValue(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(5));
        log.info("Flat discount: {} for non-grocery total: {}", flatDiscount, nonGroceryTotal);
        return flatDiscount;
    }

    private BigDecimal determineDiscountRate(UserEntity user) {
        return switch (user.getUserType()) {
            case EMPLOYEE -> discountConfiguration.getEmployee();
            case AFFILIATE -> discountConfiguration.getAffiliate();
            case NORMAL -> isLoyalCustomer(user) ? discountConfiguration.getLoyal() : BigDecimal.ZERO;
        };
    }

    private boolean isLoyalCustomer(UserEntity user) {
        return user.getCreatedAt() != null &&
                user.getCreatedAt().isBefore(LocalDateTime.now().minusYears(2));
    }
}
