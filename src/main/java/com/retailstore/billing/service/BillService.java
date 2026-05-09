package com.retailstore.billing.service;


import com.retailstore.billing.dto.BillItemDto;
import com.retailstore.billing.dto.BillResponseDto;
import com.retailstore.billing.dto.CalculateBillRequestDto;
import com.retailstore.billing.dto.CalculateBillResponseDto;
import com.retailstore.billing.exception.BillNotFoundException;
import com.retailstore.billing.exception.ItemNotFoundException;
import com.retailstore.billing.exception.UserNotFoundException;
import com.retailstore.billing.model.enums.ItemType;
import com.retailstore.billing.model.jpa.UserEntity;
import com.retailstore.billing.model.mongo.BillEntity;
import com.retailstore.billing.model.mongo.BillItem;
import com.retailstore.billing.model.mongo.DiscountBreakDown;
import com.retailstore.billing.model.mongo.ItemEntity;
import com.retailstore.billing.repository.mongo.BillRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Orchestrates bill calculation by coordinating user lookup, item lookup,
 * discount calculation, and bill persistence.
 * <p>
 * The bill calculation flow:
 * <ol>
 *   <li>Fetch the user by ID (throws {@link UserNotFoundException} if missing).</li>
 *   <li>Fetch all items by ID (throws {@link ItemNotFoundException} if none found).</li>
 *   <li>Compute total amount and non-grocery total.</li>
 *   <li>Delegate discount calculation to {@link DiscountCalculatorService}.</li>
 *   <li>Persist the bill in MongoDB and return the response.</li>
 * </ol>
 */
@Service
@AllArgsConstructor
@Slf4j
public class BillService {

    private final ItemService itemService;
    private final UserService userService;
    private final BillRepository billRepository;
    private final DiscountCalculatorService discountCalculatorService;

    /**
     * Retrieves a previously calculated bill from MongoDB by its ID.
     *
     * @param id the bill ID
     * @return the bill response DTO
     * @throws BillNotFoundException if no bill exists with the given ID
     */
    public BillResponseDto retrieveBillById(String id) {
        BillEntity billEntity = billRepository.findById(id).orElseThrow(() -> new BillNotFoundException(id));
        return BillResponseDto.toDto(billEntity);
    }

    /**
     * Calculates the net payable amount for the given bill request and persists the result.
     *
     * @param calculateBillRequestDto the request containing user ID and bill items
     * @return the calculated bill response with discount breakdown and net payable amount
     * @throws UserNotFoundException if the user does not exist
     * @throws ItemNotFoundException if no matching items are found or some items not found.
     */
    public CalculateBillResponseDto calculateNetPayableAmount(CalculateBillRequestDto calculateBillRequestDto) {
        log.info("calculating net payable amount for bill request: {}", calculateBillRequestDto);

        UserEntity user = userService.findUserById(calculateBillRequestDto.getUserId());

        List<String> itemIds = calculateBillRequestDto.getBillItems().stream()
                .map(BillItemDto::getItemId)
                .toList();

        log.info("calculating net payable amount for items: {}", itemIds);

        Map<String, ItemEntity> itemEntityMap = itemService.findItemsById(itemIds).stream()
                .collect(Collectors.toMap(ItemEntity::getId, item -> item));

        if (itemEntityMap.isEmpty() || itemIds.size() != itemEntityMap.size()) {
            throw new ItemNotFoundException("Items or some items not found");
        }

        List<BillItem> billItems = calculateBillRequestDto.getBillItems().stream()
                .map(dto -> {
                    ItemEntity itemEntity = itemEntityMap.get(dto.getItemId());
                    int quantity = dto.getQuantity();
                    BigDecimal totalPrice = itemEntity.getPrice().multiply(new BigDecimal(quantity));
                    return BillItem.builder()
                            .name(itemEntity.getName())
                            .price(itemEntity.getPrice())
                            .type(itemEntity.getType())
                            .quantity(quantity)
                            .totalPrice(totalPrice)
                            .description(itemEntity.getDescription())
                            .build();
                }).collect(Collectors.toList());

        log.info("calculating net payable amount for bill items: {}", billItems);

        BigDecimal totalAmount = billItems.stream().map(BillItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal nonGroceryTotal = billItems.stream()
                .filter(item -> ItemType.NON_GROCERY.equals(item.getType()))
                .map(BillItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("totalAmount: {} , non Grocery Total: {}", totalAmount, nonGroceryTotal);

        BigDecimal percentageDiscount = discountCalculatorService.calculatePercentageDiscount(nonGroceryTotal, user);
        BigDecimal flatDiscount = discountCalculatorService.calculateFlatDiscount(nonGroceryTotal);
        BigDecimal totalDiscount = percentageDiscount.add(flatDiscount);
        BigDecimal netPayableAmount = totalAmount.subtract(totalDiscount);

        DiscountBreakDown discountBreakDown = DiscountBreakDown.builder()
                .percentageDiscount(percentageDiscount)
                .flatDiscount(flatDiscount)
                .totalDiscount(totalDiscount)
                .build();

        BillEntity billEntity = BillEntity.builder()
                .userId(String.valueOf(user.getId()))
                .userType(user.getUserType())
                .billItems(billItems)
                .totalAmount(totalAmount)
                .discountBreakDown(discountBreakDown)
                .netPayableAmount(netPayableAmount)
                .createdAt(LocalDateTime.now())
                .build();

        log.info("saving bill entity: {}", billEntity);
        billRepository.save(billEntity);
        log.info("bill entity saved.");

        log.info("Net payable amount: {}, total discount: {}, discount breakdown: {}", netPayableAmount, totalDiscount, discountBreakDown);
        return CalculateBillResponseDto.builder()
                .billId(billEntity.getId())
                .netPayableAmount(netPayableAmount)
                .totalDiscount(totalDiscount)
                .discountBreakDown(discountBreakDown)
                .totalAmount(totalAmount)
                .build();
    }

}
