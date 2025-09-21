package com.example.product.price;

import com.example.product.price.models.ProductPriceDetail;
import com.example.product.price.services.ProductPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductPriceServiceTest {

    private ProductPriceService service;
    private HttpClient httpClient;
    private HttpResponse<String> httpResponse;

    @BeforeEach
    void setUp() {
        service = new ProductPriceService("http://fake-api.com", "dummy-key");
        httpClient = mock(HttpClient.class);
        httpResponse = mock(HttpResponse.class);

        try {
            var field = ProductPriceService.class.getDeclaredField("httpClient");
            field.setAccessible(true);
            field.set(service, httpClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFetchStoresSuccess() throws Exception {
        String apiResponse = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "[{ \\"productName\\": \\"Laptop\\", \\"storeName\\": \\"Tech Store\\", \\"price\\": 1200.99 }]"
                          }
                        ]
                      }
                    }
                  ]
                }
                """;

        when(httpResponse.body()).thenReturn(apiResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        CompletableFuture<List<ProductPriceDetail>> future = service.fetchStores("laptop", "Toronto", "Ontario");

        List<ProductPriceDetail> result = future.join();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getProductName());
        assertEquals("Tech Store", result.get(0).getStoreName());
        assertEquals(1200.99, result.get(0).getPrice());
    }

    @Test
    void testFetchStoresReturnsEmptyOnBadJson() throws Exception {
        String apiResponse = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "INVALID_JSON"
                          }
                        ]
                      }
                    }
                  ]
                }
                """;

        when(httpResponse.body()).thenReturn(apiResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        List<ProductPriceDetail> result = service.fetchStores("tablet", "Vancouver", "BC").join();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void testSendRequestBuildsCorrectHttpRequest() throws Exception {
        String apiResponse = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "[]"
                          }
                        ]
                      }
                    }
                  ]
                }
                """;

        when(httpResponse.body()).thenReturn(apiResponse);
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);

        when(httpClient.send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        service.fetchStores("camera", "Ottawa", "ON").join();

        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals("http://fake-api.com", capturedRequest.uri().toString());
        assertEquals("application/json", capturedRequest.headers().firstValue("Content-Type").orElse(""));
        assertEquals("dummy-key", capturedRequest.headers().firstValue("X-goog-api-key").orElse(""));
        assertEquals("POST", capturedRequest.method());
    }
}
