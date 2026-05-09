package com.retailstore.billing.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturn404WhenUserNotFound() {
        // Arrange
        UserNotFoundException ex = new UserNotFoundException("User with id 1 not found");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleUserNotFoundException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("message")).isEqualTo("User with id 1 not found");
    }

    @Test
    void shouldReturn404WhenItemNotFound() {
        // Arrange
        ItemNotFoundException ex = new ItemNotFoundException("Items not found");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleItemNotFoundException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("message")).isEqualTo("Items not found");
    }

    @Test
    void shouldReturn404WhenBillNotFound() {
        // Arrange
        BillNotFoundException ex = new BillNotFoundException("Bill with id bill123 not found");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleItemNotFoundException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("message")).isEqualTo("Bill with id bill123 not found");
    }

    @Test
    void shouldReturn500ForGenericException() {
        // Arrange
        Exception ex = new Exception("Unexpected error");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(500);
        assertThat(response.getBody().get("message")).isEqualTo("An unexpected error occurred");
    }
}
