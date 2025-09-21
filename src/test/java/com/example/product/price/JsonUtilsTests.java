package com.example.product.price;

import com.example.product.price.models.ProductPriceDetail;
import com.example.product.price.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsonUtilsTests {

    @Test
    public void testToString() throws IOException {
        String json = JsonUtils.toString(productPriceDetail());
        assertNotNull(json);
    }

    @Test
    public void testToStringWhenContains() throws IOException {
        String json = JsonUtils.toString(productPriceDetail());
        assertNotNull(json);
        assertTrue(json.contains("test"));
    }

    @Test
    public void testToStringWhenContainsNull() throws IOException {
        String json = JsonUtils.toString(null);
        assertNotNull(json);
        assertTrue(json.contains("null"));
    }

    private ProductPriceDetail productPriceDetail() {
        ProductPriceDetail productPriceDetail = new ProductPriceDetail();
        productPriceDetail.setProductName("test");
        productPriceDetail.setAvailability("");
        productPriceDetail.setDiscount("");
        productPriceDetail.setPrice(00.28);
        productPriceDetail.setRating(5);
        productPriceDetail.setStoreName("");
        productPriceDetail.setStoreType("");
        productPriceDetail.setWebsite("");
        return productPriceDetail;
    }
}
