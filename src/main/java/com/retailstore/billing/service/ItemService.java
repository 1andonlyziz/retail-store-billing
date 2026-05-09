package com.retailstore.billing.service;

import com.retailstore.billing.model.mongo.ItemEntity;
import com.retailstore.billing.repository.mongo.ItemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides item-related operations backed by a MongoDB repository.
 */
@Service
@AllArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * Finds items by their IDs.
     *
     * @param ids the list of item IDs to look up
     * @return the list of matching items, or an empty list if none are found
     */
    public List<ItemEntity> findItemsById(List<String> ids) {
        log.info("Find items by ids: {}", ids);
        List<ItemEntity> items = itemRepository.findAllById(ids);
        log.info("Found items by ids: {}", items);
        return items;
    }
}
