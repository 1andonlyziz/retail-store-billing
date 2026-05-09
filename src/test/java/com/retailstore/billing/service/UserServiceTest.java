package com.retailstore.billing.service;

import com.retailstore.billing.exception.UserNotFoundException;
import com.retailstore.billing.model.enums.UserType;
import com.retailstore.billing.model.jpa.UserEntity;
import com.retailstore.billing.repository.jpa.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserWhenFound() {
        // Arrange
        UserEntity user = UserEntity.builder()
                .id(1)
                .name("ABDULAZIZ")
                .userType(UserType.NORMAL)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        UserEntity result = userService.findUserById(1);

        // Assert
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("ABDULAZIZ");
        assertThat(result.getUserType()).isEqualTo(UserType.NORMAL);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.findUserById(99));

        assertThat(ex.getMessage()).contains("99");
    }
}
