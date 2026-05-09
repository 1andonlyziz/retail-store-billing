package com.retailstore.billing.repository.mongo;


import com.retailstore.billing.model.mongo.BillEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for {@link BillEntity}.
 */
@Repository
public interface BillRepository extends MongoRepository<BillEntity, String> {
}
