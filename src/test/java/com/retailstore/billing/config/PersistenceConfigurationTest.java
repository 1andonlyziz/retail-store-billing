package com.retailstore.billing.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class PersistenceConfigurationTest {

    @Test
    void shouldInstantiatePersistenceConfiguration() {
        PersistenceConfiguration config = new PersistenceConfiguration();
        assertThat(config).isNotNull();
    }
}