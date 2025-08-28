package com.example.product.price;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProductPriceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductPriceApplication.class, args);
	}
}
