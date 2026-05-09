package com.retailstore.billing.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UserNotFoundException}.
 * Tests exception instantiation, message handling, and inheritance behavior.
 */
@ExtendWith(MockitoExtension.class)
class UserNotFoundExceptionTest {

    @Test
    void testExceptionWithMessage() {
        // Given
        String errorMessage = "User with ID 999 not found";

        // When
        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        // Then
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        // Given
        UserNotFoundException exception = new UserNotFoundException("Test message");

        // Then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void testExceptionCanBeThrown() {
        // Given
        String errorMessage = "User not found in system";

        // When & Then
        Exception thrown = assertThrows(UserNotFoundException.class, () -> {
            throw new UserNotFoundException(errorMessage);
        });

        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void testExceptionWithNullMessage() {
        // When
        UserNotFoundException exception = new UserNotFoundException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testExceptionWithEmptyMessage() {
        // Given
        String emptyMessage = "";

        // When
        UserNotFoundException exception = new UserNotFoundException(emptyMessage);

        // Then
        assertNotNull(exception);
        assertEquals(emptyMessage, exception.getMessage());
    }

    @Test
    void testExceptionStackTrace() {
        // When
        UserNotFoundException exception = new UserNotFoundException("Test exception");

        // Then
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }
}
