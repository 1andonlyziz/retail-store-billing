package com.retailstore.billing;

import com.retailstore.billing.config.DiscountConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main entry point for the Bill Calculator Spring Boot application.
 */
@SpringBootApplication
@EnableConfigurationProperties(DiscountConfiguration.class)
public class BillCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillCalculatorApplication.class, args);
    }

}
