package com.example.product.price;

import com.example.product.price.utils.ProductPriceUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductPriceUtilsTests {


    @Test
    public void testProductPriceUtilsWhenNotNull() {
        String prompt = ProductPriceUtils.setPrompt("test", "test", "test");
        assertNotNull(prompt);
    }

    @Test
    public void testProductPriceUtilsWhenSetPrompts() {
        String prompt = ProductPriceUtils.setPrompt("Toronto", "Laptop", "Within 50 Kms");
        assertNotNull(prompt);
        assertTrue(prompt.contains("Toronto"));
        assertTrue(prompt.contains("Laptop"));
        assertTrue(prompt.contains("Within 50 Kms"));
    }

    @Test
    public void testProductPriceUtilsWhenNullPrompts() {
        String prompt = ProductPriceUtils.setPrompt(null, null, null);
        assertNotNull(prompt);
        assertTrue(prompt.contains("null"));
    }

    @Test
    public void testProductPriceUtilsWhenEmptyStrings() {
        String prompt = ProductPriceUtils.setPrompt("", "", "");
        assertNotNull(prompt);
        assertTrue(prompt.contains(""));
    }
}
