package com.retailstore.billing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for {@link BillCalculatorApplication}.
 * Tests application context loading and main method execution.
 */
@SpringBootTest
@ActiveProfiles("local")
class BillCalculatorApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodDoesNotThrow() {
        // Given
        String[] args = {};

        // When & Then
        assertDoesNotThrow(() -> {
            BillCalculatorApplication.main(args);
        });
    }
}
