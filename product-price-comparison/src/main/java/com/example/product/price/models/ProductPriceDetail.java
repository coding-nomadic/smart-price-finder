package com.example.product.price.models;

import com.example.product.price.configs.DateOnlyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceDetail {
    private String productName;
    private String storeName;
    private double price;
    private String discount;
    //@JsonDeserialize(using = DateOnlyDeserializer.class)
    private String lastUpdated;
    private String website;
    private String distance;
    private String availability;
    private String category;
    private double rating;
    private String location;
    private String storeType;
}