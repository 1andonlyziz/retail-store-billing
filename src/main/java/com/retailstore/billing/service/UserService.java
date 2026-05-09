package com.retailstore.billing.service;

import com.retailstore.billing.exception.UserNotFoundException;
import com.retailstore.billing.model.jpa.UserEntity;
import com.retailstore.billing.repository.jpa.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Provides user-related operations backed by a JPA repository (PostgreSQL).
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Finds a user by their unique ID.
     *
     * @param id the user ID
     * @return the matching {@link UserEntity}
     * @throws UserNotFoundException if no user exists with the given ID
     */
    public UserEntity findUserById(Integer id) throws UserNotFoundException {
        log.info("Find user by id: {}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        log.info("Found user by id: {} , UserEntity: {}", user.getId(), user);
        return user;
    }
}
