package com.retailstore.billing.service;

import com.retailstore.billing.dto.BillItemDto;
import com.retailstore.billing.dto.BillResponseDto;
import com.retailstore.billing.dto.CalculateBillRequestDto;
import com.retailstore.billing.dto.CalculateBillResponseDto;
import com.retailstore.billing.exception.BillNotFoundException;
import com.retailstore.billing.exception.ItemNotFoundException;
import com.retailstore.billing.exception.UserNotFoundException;
import com.retailstore.billing.model.enums.ItemType;
import com.retailstore.billing.model.enums.UserType;
import com.retailstore.billing.model.jpa.UserEntity;
import com.retailstore.billing.model.mongo.BillEntity;
import com.retailstore.billing.model.mongo.DiscountBreakDown;
import com.retailstore.billing.model.mongo.ItemEntity;
import com.retailstore.billing.repository.mongo.BillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.retailstore.billing.model.enums.UserType.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillServiceTest {


    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private BillRepository billRepository;
    @Mock
    private DiscountCalculatorService discountCalculatorService;

    @InjectMocks
    private BillService billService;

    @Test
    void shouldThrowExceptionWhenSomeItemsNotFound() {

        //arrange
        CalculateBillRequestDto request = CalculateBillRequestDto.builder()
                .userId(1)
                .billItems(List.of(
                        new BillItemDto("item001", 1),
                        new BillItemDto("item002", 1))
                ).build();


        UserEntity normal = UserEntity.builder()
                .id(1)
                .userType(NORMAL)
                .createdAt(LocalDateTime.now())
                .build();

        List<String> itemIds = List.of("item001", "item002");

        ItemEntity laptop = ItemEntity.builder()
                .id("item001")
                .name("egg")
                .price(new BigDecimal("35.50"))
                .type(ItemType.NON_GROCERY)
                .build();

        when(userService.findUserById(1)).thenReturn(normal);
        when(itemService.findItemsById(itemIds)).thenReturn((List.of(laptop)));

        //assert & act
        assertThrows(ItemNotFoundException.class,
                () -> billService.calculateNetPayableAmount(request));
    }

    @Test
    void normalUserWithoutDiscount() {
        // Arrange
        UserEntity normal = UserEntity.builder()
                .id(1)
                .userType(NORMAL)
                .createdAt(LocalDateTime.now())
                .build();

        ItemEntity laptop = ItemEntity.builder()
                .id("item001")
                .name("egg")
                .price(new BigDecimal("35.50"))
                .type(ItemType.NON_GROCERY)
                .build();

        CalculateBillRequestDto request = CalculateBillRequestDto.builder()
                .userId(1)
                .billItems(List.of(new BillItemDto("item001", 1)))
                .build();

        when(userService.findUserById(1)).thenReturn(normal);
        when(itemService.findItemsById(List.of("item001"))).thenReturn(List.of(laptop));
        when(discountCalculatorService.calculatePercentageDiscount(any(), any())).thenReturn(BigDecimal.ZERO);
        when(discountCalculatorService.calculateFlatDiscount(any())).thenReturn(BigDecimal.ZERO);

        // Act
        CalculateBillResponseDto response = billService.calculateNetPayableAmount(request);

        // Assert
        assertThat(response.getNetPayableAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(35.50));
        verify(billRepository).save(any(BillEntity.class));
    }

    @Test
    void normalUserApplyOnlyFlatDiscount() {
        // Arrange - NORMAL user under 2 years gets 0% percentage discount, only flat discount applies
        UserEntity normal = UserEntity.builder()
                .id(1)
                .userType(NORMAL)
                .createdAt(LocalDateTime.now())
                .build();

        ItemEntity laptop = ItemEntity.builder()
                .id("item001")
                .name("laptop")
                .price(new BigDecimal("1000.00"))
                .type(ItemType.NON_GROCERY)
                .build();

        CalculateBillRequestDto request = CalculateBillRequestDto.builder()
                .userId(1)
                .billItems(List.of(new BillItemDto("item001", 1)))
                .build();

        when(userService.findUserById(1)).thenReturn(normal);
        when(itemService.findItemsById(List.of("item001"))).thenReturn(List.of(laptop));
        when(discountCalculatorService.calculatePercentageDiscount(any(), any())).thenReturn(BigDecimal.ZERO);
        when(discountCalculatorService.calculateFlatDiscount(any())).thenReturn(new BigDecimal("50"));

        // Act
        CalculateBillResponseDto response = billService.calculateNetPayableAmount(request);

        // Assert - 1000 - 0%(0) - flat(50) = 950
        assertThat(response.getNetPayableAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(950.00));
        verify(billRepository).save(any(BillEntity.class));
    }


    @Test
    void shouldApply30PercentDiscountForEmployee() {
        // Arrange
        UserEntity employee = UserEntity.builder()
                .id(1)
                .userType(EMPLOYEE)
                .createdAt(LocalDateTime.now())
                .build();

        ItemEntity laptop = ItemEntity.builder()
                .id("item001")
                .name("laptop")
                .price(new BigDecimal("1000"))
                .type(ItemType.NON_GROCERY)
                .build();

        CalculateBillRequestDto request = CalculateBillRequestDto.builder()
                .userId(1)
                .billItems(List.of(new BillItemDto("item001", 1)))
                .build();

        when(userService.findUserById(1)).thenReturn(employee);
        when(itemService.findItemsById(List.of("item001"))).thenReturn(List.of(laptop));
        when(discountCalculatorService.calculatePercentageDiscount(any(), any())).thenReturn(new BigDecimal("300"));
        when(discountCalculatorService.calculateFlatDiscount(any())).thenReturn(new BigDecimal("50"));

        // Act
        CalculateBillResponseDto response = billService.calculateNetPayableAmount(request);

        // Assert
        assertThat(response.getNetPayableAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(650.00));
        // 1000 - 30%(300) - flat(50) = 650
        verify(billRepository).save(any(BillEntity.class));
    }

    @Test
    void shouldApply10PercentDiscountForAffiliate() {
        // Arrange
        UserEntity employee = UserEntity.builder()
                .id(1)
                .userType(AFFILIATE)
                .createdAt(LocalDateTime.now())
                .build();

        ItemEntity laptop = ItemEntity.builder()
                .id("item001")
                .name("laptop")
                .price(new BigDecimal("1000"))
                .type(ItemType.NON_GROCERY)
                .build();

        CalculateBillRequestDto request = CalculateBillRequestDto.builder()
                .userId(1)
                .billItems(List.of(new BillItemDto("item001", 1)))
                .build();

        when(userService.findUserById(1)).thenReturn(employee);
        when(itemService.findItemsById(List.of("item001"))).thenReturn(List.of(laptop));
        when(discountCalculatorService.calculatePercentageDiscount(any(), any())).thenReturn(new BigDecimal("100"));
        when(discountCalculatorService.calculateFlatDiscount(any())).thenReturn(new BigDecimal("50"));

        // Act
        CalculateBillResponseDto response = billService.calculateNetPayableAmount(request);

        // Assert
        assertThat(response.getNetPayableAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(850.00));

        // 1000 - 10%(100) - flat(50) = 850
        verify(billRepository).save(any(BillEntity.class));
    }

    @Test
    void shouldApply5PercentDiscountForLoyal() {
        // Arrange
        UserEntity employee = UserEntity.builder()
                .id(1)
                .userType(NORMAL)
                .createdAt(LocalDateTime.now().minusYears(2).minusDays(5))
                .build();

        ItemEntity laptop = ItemEntity.builder()
                .id("item001")
                .name("laptop")
                .price(new BigDecimal("1000"))
                .type(ItemType.NON_GROCERY)
                .build();

        CalculateBillRequestDto request = CalculateBillRequestDto.builder()
                .userId(1)
                .billItems(List.of(new BillItemDto("item001", 1)))
                .build();

        when(userService.findUserById(1)).thenReturn(employee);
        when(itemService.findItemsById(List.of("item001"))).thenReturn(List.of(laptop));
        when(discountCalculatorService.calculatePercentageDiscount(any(), any())).thenReturn(new BigDecimal("50"));
        when(discountCalculatorService.calculateFlatDiscount(any())).thenReturn(new BigDecimal("50"));

        // Act
        CalculateBillResponseDto response = billService.calculateNetPayableAmount(request);

        // Assert
        assertThat(response.getNetPayableAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(900.00));
        // 1000 - 5%(50) - flat(50) = 900
        verify(billRepository).save(any(BillEntity.class));
    }

    @Test
    void shouldApplyDiscountOnlyOnNonGroceries() {
        // Arrange
        UserEntity employee = UserEntity.builder()
                .id(1)
                .userType(EMPLOYEE)
                .createdAt(LocalDateTime.now().minusYears(2))
                .build();

        ItemEntity eggs = ItemEntity.builder()
                .id("item001")
                .name("Eggs")
                .price(new BigDecimal("5"))
                .type(ItemType.GROCERY)
                .build();

        ItemEntity laptop = ItemEntity.builder()
                .id("item002")
                .name("Laptop")
                .price(new BigDecimal("1000"))
                .type(ItemType.NON_GROCERY)
                .build();

        CalculateBillRequestDto request = CalculateBillRequestDto.builder()
                .userId(1)
                .billItems(List.of(new BillItemDto("item001", 500), new BillItemDto("item002", 2)))
                .build();

        when(userService.findUserById(1)).thenReturn(employee);
        when(itemService.findItemsById(List.of("item001", "item002"))).thenReturn(List.of(eggs, laptop));
        when(discountCalculatorService.calculatePercentageDiscount(any(), any())).thenReturn(new BigDecimal("600"));
        when(discountCalculatorService.calculateFlatDiscount(any())).thenReturn(new BigDecimal("100"));

        // Act
        CalculateBillResponseDto response = billService.calculateNetPayableAmount(request);

        // item001 500 * 5 = 2500 GROCERY // no discount
        // item002 1000 * 2 = 2000 NON_GROCERY flat discount is  20 * 5 = 100 , Employee discount 30% -- 600 totalDiscount = 700
        // total Amount is 4500 and net payable will be 4500 - 700 = 3800
        // Assert
        assertThat(response.getTotalAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(4500.00));

        assertThat(response.getNetPayableAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(3800.00));

        verify(billRepository).save(any(BillEntity.class));
    }

    @Test
    void shouldThrowItemNotFoundExceptionWhenItemsAreEmpty() {
        // Arrange
        UserEntity employee = UserEntity.builder()
                .id(1)
                .userType(EMPLOYEE)
                .createdAt(LocalDateTime.now())
                .build();

        CalculateBillRequestDto request = CalculateBillRequestDto.builder()
                .userId(1)
                .billItems(List.of(new BillItemDto("nonexistent", 1)))
                .build();

        when(userService.findUserById(1)).thenReturn(employee);
        when(itemService.findItemsById(List.of("nonexistent"))).thenReturn(List.of());

        // Act & Assert
        assertThrows(ItemNotFoundException.class,
                () -> billService.calculateNetPayableAmount(request));
    }

    @Test
    void shouldPropagateUserNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        CalculateBillRequestDto request = CalculateBillRequestDto.builder()
                .userId(99)
                .billItems(List.of(new BillItemDto("item001", 1)))
                .build();

        when(userService.findUserById(any())).thenThrow(new UserNotFoundException("User with id 99 not found"));

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> billService.calculateNetPayableAmount(request));
    }

    @Test
    void checkIfBillExists() {
        // Arrange
        BillEntity billEntity = BillEntity.builder()
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

        when(billRepository.findById("bill123")).thenReturn(Optional.of(billEntity));

        // Act
        BillResponseDto response = billService.retrieveBillById("bill123");

        // Assert
        assertThat(response.getId()).isEqualTo("bill123");
        assertThat(response.getUserId()).isEqualTo("1");
        assertThat(response.getNetPayableAmount()).isEqualByComparingTo(new BigDecimal("650"));
        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("1000"));
    }

    @Test
    void checkIfBillDontExists() {
        // Arrange
        when(billRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BillNotFoundException.class,
                () -> billService.retrieveBillById("nonexistent"));
    }


}
