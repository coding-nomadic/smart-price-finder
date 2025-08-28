package com.example.product.price.configs;



import com.example.product.price.models.ProductPriceDetail;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class CacheConfig {

    @Bean
    public com.github.benmanes.caffeine.cache.Cache<String, List<ProductPriceDetail>> productCache() {
        return Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(15)).maximumSize(1000).build();
    }
}