package com.retailstore.billing.controller;

import com.retailstore.billing.dto.BillItemDto;
import com.retailstore.billing.dto.BillResponseDto;
import com.retailstore.billing.dto.CalculateBillRequestDto;
import com.retailstore.billing.dto.CalculateBillResponseDto;
import com.retailstore.billing.exception.BillNotFoundException;
import com.retailstore.billing.model.enums.UserType;
import com.retailstore.billing.model.mongo.DiscountBreakDown;
import com.retailstore.billing.service.BillService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillControllerTest {

    @Mock
    private BillService billService;

    @InjectMocks
    private BillController billController;

    @Test
    void calculateBill() {
        // Arrange
        CalculateBillRequestDto request = CalculateBillRequestDto.builder()
                .userId(1)
                .billItems(List.of(new BillItemDto("item001", 1)))
                .build();

        CalculateBillResponseDto serviceResponse = CalculateBillResponseDto.builder()
                .billId("bill123")
                .netPayableAmount(new BigDecimal("650"))
                .totalDiscount(new BigDecimal("350"))
                .discountBreakDown(DiscountBreakDown.builder()
                        .percentageDiscount(new BigDecimal("300"))
                        .flatDiscount(new BigDecimal("50"))
                        .totalDiscount(new BigDecimal("350"))
                        .build())
                .build();

        when(billService.calculateNetPayableAmount(any())).thenReturn(serviceResponse);

        // Act
        ResponseEntity<CalculateBillResponseDto> response = billController.calculateBill(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNetPayableAmount()).isEqualByComparingTo(new BigDecimal("650"));
        assertThat(response.getBody().getBillId()).isEqualTo("bill123");
    }

    @Test
    void fetchBill() {
        // Arrange
        BillResponseDto serviceResponse = BillResponseDto.builder()
                .id("bill123")
                .userId("1")
                .userType(UserType.EMPLOYEE)
                .totalAmount(new BigDecimal("1000"))
                .netPayableAmount(new BigDecimal("650"))
                .discountBreakDown(DiscountBreakDown.builder()
                        .percentageDiscount(new BigDecimal("300"))
                        .flatDiscount(new BigDecimal("50"))
                        .totalDiscount(new BigDecimal("350"))
                        .build())
                .createdAt(LocalDateTime.now())
                .build();

        when(billService.retrieveBillById("bill123")).thenReturn(serviceResponse);

        // Act
        ResponseEntity<BillResponseDto> response = billController.fetchBill("bill123");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("bill123");
        assertThat(response.getBody().getNetPayableAmount()).isEqualByComparingTo(new BigDecimal("650"));
    }

    @Test
    void fetchBillNotFound() {
        // Arrange
        when(billService.retrieveBillById("nonexistent"))
                .thenThrow(new BillNotFoundException("Bill not found"));

        // Act & Assert
        assertThrows(BillNotFoundException.class,
                () -> billController.fetchBill("nonexistent"));
    }
}
