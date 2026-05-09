package com.retailstore.billing.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ItemNotFoundException}.
 * Tests exception instantiation, message handling, and inheritance behavior.
 */
@ExtendWith(MockitoExtension.class)
class ItemNotFoundExceptionTest {

    @Test
    void testExceptionWithMessage() {
        // Given
        String errorMessage = "Item with ID ITEM-001 not found";

        // When
        ItemNotFoundException exception = new ItemNotFoundException(errorMessage);

        // Then
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        // Given
        ItemNotFoundException exception = new ItemNotFoundException("Test message");

        // Then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void testExceptionCanBeThrown() {
        // Given
        String errorMessage = "Item not found in inventory";

        // When & Then
        Exception thrown = assertThrows(ItemNotFoundException.class, () -> {
            throw new ItemNotFoundException(errorMessage);
        });

        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void testExceptionWithNullMessage() {
        // When
        ItemNotFoundException exception = new ItemNotFoundException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testExceptionWithEmptyMessage() {
        // Given
        String emptyMessage = "";

        // When
        ItemNotFoundException exception = new ItemNotFoundException(emptyMessage);

        // Then
        assertNotNull(exception);
        assertEquals(emptyMessage, exception.getMessage());
    }

    @Test
    void testExceptionStackTrace() {
        // When
        ItemNotFoundException exception = new ItemNotFoundException("Test exception");

        // Then
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }
}
