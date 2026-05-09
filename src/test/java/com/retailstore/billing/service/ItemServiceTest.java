package com.retailstore.billing.service;

import com.retailstore.billing.model.enums.ItemType;
import com.retailstore.billing.model.mongo.ItemEntity;
import com.retailstore.billing.repository.mongo.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void shouldReturnItemsWhenFound() {
        // Arrange
        ItemEntity laptop = ItemEntity.builder()
                .id("item001")
                .name("Laptop")
                .price(new BigDecimal("1200"))
                .type(ItemType.NON_GROCERY)
                .description("High performance laptop")
                .build();

        ItemEntity milk = ItemEntity.builder()
                .id("item002")
                .name("Milk")
                .price(new BigDecimal("2.50"))
                .type(ItemType.GROCERY)
                .description("Full fat milk 1L")
                .build();

        List<String> ids = List.of("item001", "item002");
        when(itemRepository.findAllById(ids)).thenReturn(List.of(laptop, milk));

        // Act
        List<ItemEntity> result = itemService.findItemsById(ids);

        // Assert
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getId()).isEqualTo("item001");
        assertThat(result.get(1).getId()).isEqualTo("item002");
    }

    @Test
    void shouldReturnEmptyListWhenNoItemsMatch() {
        // Arrange
        List<String> ids = List.of("nonexistent001", "nonexistent002");
        when(itemRepository.findAllById(ids)).thenReturn(Collections.emptyList());

        // Act
        List<ItemEntity> result = itemService.findItemsById(ids);

        // Assert
        assertThat(result.isEmpty()).isTrue();
    }
}
