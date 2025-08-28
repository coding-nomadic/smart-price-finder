package com.example.product.price.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactForm {
    private String name;
    private String email;
    private String message;
}
