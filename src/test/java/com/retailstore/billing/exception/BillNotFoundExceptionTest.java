package com.retailstore.billing.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BillNotFoundException}.
 * Tests exception instantiation, message handling, and inheritance behavior.
 */
@ExtendWith(MockitoExtension.class)
class BillNotFoundExceptionTest {

    @Test
    void testExceptionWithMessage() {
        // Given
        String errorMessage = "Bill with ID 12345 not found";

        // When
        BillNotFoundException exception = new BillNotFoundException(errorMessage);

        // Then
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        // Given
        BillNotFoundException exception = new BillNotFoundException("Test message");

        // Then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void testExceptionCanBeThrown() {
        // Given
        String errorMessage = "Bill not found in database";

        // When & Then
        Exception thrown = assertThrows(BillNotFoundException.class, () -> {
            throw new BillNotFoundException(errorMessage);
        });

        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void testExceptionWithNullMessage() {
        // When
        BillNotFoundException exception = new BillNotFoundException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testExceptionWithEmptyMessage() {
        // Given
        String emptyMessage = "";

        // When
        BillNotFoundException exception = new BillNotFoundException(emptyMessage);

        // Then
        assertNotNull(exception);
        assertEquals(emptyMessage, exception.getMessage());
    }

    @Test
    void testExceptionStackTrace() {
        // When
        BillNotFoundException exception = new BillNotFoundException("Test exception");

        // Then
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }
}
