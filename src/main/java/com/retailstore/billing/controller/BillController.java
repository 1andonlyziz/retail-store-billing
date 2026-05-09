package com.retailstore.billing.controller;

import com.retailstore.billing.dto.BillResponseDto;
import com.retailstore.billing.dto.CalculateBillRequestDto;
import com.retailstore.billing.dto.CalculateBillResponseDto;
import com.retailstore.billing.service.BillService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing endpoints for bill calculation and retrieval.
 * <p>
 * Provides two operations:
 * <ul>
 *   <li>{@code POST /bill/calculate} — Calculate net payable amount for a given bill.</li>
 *   <li>{@code GET  /bill/retrieve}  — Retrieve a previously calculated bill by ID.</li>
 * </ul>
 */
@RestController
@RequestMapping(path = "/bill")
@AllArgsConstructor
@Slf4j
public class BillController {

    private final BillService billService;

    /**
     * Calculates the net payable amount for a given bill request.
     * Applies user-specific percentage discounts and a flat discount on non-grocery items.
     *
     * @param calculateBillRequestDto the bill calculation request containing user ID and bill items
     * @return the calculated bill including total amount, discounts, and net payable amount
     */
    @PostMapping("/calculate")
    public ResponseEntity<CalculateBillResponseDto> calculateBill(@Valid @RequestBody CalculateBillRequestDto calculateBillRequestDto) {
        log.info("Calculate bill request {}", calculateBillRequestDto);
        CalculateBillResponseDto response = billService.calculateNetPayableAmount(calculateBillRequestDto);
        log.info("Calculate bill response {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a previously calculated bill by its unique ID.
     *
     * @param id the bill ID
     * @return the stored bill details
     */
    @GetMapping("/retrieve")
    public ResponseEntity<BillResponseDto> fetchBill(@RequestParam("bill-id") String id) {
        log.info("request to retrieve bill with bill-id {}", id);
        BillResponseDto response = billService.retrieveBillById(id);
        log.info("response to Retrieve bill {}", response);
        return ResponseEntity.ok(response);
    }
}
