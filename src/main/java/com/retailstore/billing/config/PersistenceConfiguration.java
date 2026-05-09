package com.retailstore.billing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.retailstore.billing.repository.mongo")
@EnableJpaRepositories(basePackages = "com.retailstore.billing.repository.jpa")
public class PersistenceConfiguration {
}
