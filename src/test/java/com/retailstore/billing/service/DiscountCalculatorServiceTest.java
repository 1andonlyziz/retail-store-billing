package com.retailstore.billing.service;

import com.retailstore.billing.config.DiscountConfiguration;
import com.retailstore.billing.model.enums.UserType;
import com.retailstore.billing.model.jpa.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountCalculatorServiceTest {

    @Mock
    private DiscountConfiguration discountConfiguration;

    @InjectMocks
    private DiscountCalculatorService discountCalculatorService;

    @Test
    void calculatePercentageDiscountEmployeeShouldApply30Percent() {
        // Arrange
        UserEntity employee = UserEntity.builder()
                .id(1).userType(UserType.EMPLOYEE)
                .createdAt(LocalDateTime.now())
                .build();
        when(discountConfiguration.getEmployee()).thenReturn(new BigDecimal("0.30"));

        // Act
        BigDecimal discount = discountCalculatorService.calculatePercentageDiscount(
                new BigDecimal("1000"), employee);

        // Assert — 1000 * 0.30 = 300
        assertThat(discount).isEqualByComparingTo(new BigDecimal("300"));
    }

    @Test
    void calculatePercentageDiscountAffiliateShouldApply10Percent() {
        // Arrange
        UserEntity affiliate = UserEntity.builder()
                .id(2).userType(UserType.AFFILIATE)
                .createdAt(LocalDateTime.now())
                .build();
        when(discountConfiguration.getAffiliate()).thenReturn(new BigDecimal("0.10"));

        // Act
        BigDecimal discount = discountCalculatorService.calculatePercentageDiscount(
                new BigDecimal("1000"), affiliate);

        // Assert — 1000 * 0.10 = 100
        assertThat(discount).isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    void calculatePercentageDiscountLoyalCustomerShouldApply5Percent() {
        // Arrange — customer registered more than 2 years ago
        UserEntity loyalCustomer = UserEntity.builder()
                .id(3).userType(UserType.NORMAL)
                .createdAt(LocalDateTime.now().minusYears(3))
                .build();
        when(discountConfiguration.getLoyal()).thenReturn(new BigDecimal("0.05"));

        // Act
        BigDecimal discount = discountCalculatorService.calculatePercentageDiscount(
                new BigDecimal("1000"), loyalCustomer);

        // Assert — 1000 * 0.05 = 50
        assertThat(discount).isEqualByComparingTo(new BigDecimal("50"));
    }

    @Test
    void calculatePercentageDiscountNormalNewCustomerShouldApplyZeroPercent() {
        // Arrange — customer registered less than 2 years ago, no discount config needed
        UserEntity newCustomer = UserEntity.builder()
                .id(4).userType(UserType.NORMAL)
                .createdAt(LocalDateTime.now().minusMonths(6))
                .build();

        // Act
        BigDecimal discount = discountCalculatorService.calculatePercentageDiscount(
                new BigDecimal("1000"), newCustomer);

        // Assert — 1000 * 0 = 0
        assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calculateFlatDiscountShouldApply5PerEvery100() {
        // $990 → floor(990/100) * 5 = 9 * 5 = 45
        BigDecimal discount = discountCalculatorService.calculateFlatDiscount(new BigDecimal("990"));
        assertThat(discount).isEqualByComparingTo(new BigDecimal("45"));
    }

    @Test
    void calculateFlatDiscountExactHundredShouldApply5() {
        // $100 → 1 * 5 = 5
        BigDecimal discount = discountCalculatorService.calculateFlatDiscount(new BigDecimal("100"));
        assertThat(discount).isEqualByComparingTo(new BigDecimal("5"));
    }

    @Test
    void calculateFlatDiscountBelowHundredShouldApplyZero() {
        // $50 → floor(50/100) * 5 = 0
        BigDecimal discount = discountCalculatorService.calculateFlatDiscount(new BigDecimal("50"));
        assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
