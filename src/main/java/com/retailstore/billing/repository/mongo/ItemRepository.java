package com.retailstore.billing.repository.mongo;


import com.retailstore.billing.model.mongo.ItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for {@link ItemEntity}.
 */
@Repository
public interface ItemRepository extends MongoRepository<ItemEntity, String> {
}
