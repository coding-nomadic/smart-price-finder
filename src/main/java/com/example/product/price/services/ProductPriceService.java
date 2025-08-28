package com.example.product.price.services;

import com.example.product.price.models.ProductPriceDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductPriceService {

    private static final Logger logger = LoggerFactory.getLogger(ProductPriceService.class);
    private final String apiUrl;
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ProductPriceService(@Value("${gemini.api.url}") final String apiUrl,
                               @Value("${gemini.api.key}") final String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches product store details from the API using the given prompt.
     */
    @Cacheable(value = "stores", key = "#prompt")
    public List<ProductPriceDetail> fetchStores(final String prompt) {
        try {
            final String responseBody = sendRequest(prompt);
            final String jsonArrayText = extractJsonArray(responseBody);
            return parseStores(jsonArrayText);
        } catch (IOException | InterruptedException e) {
            logger.error("Error fetching stores for prompt '{}'", prompt, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Sends the HTTP POST request to the API with the given prompt.
     */
    private String sendRequest(final String prompt) throws IOException, InterruptedException {
        final Map<String, Object> part = Map.of("text", prompt);
        final Map<String, Object> content = Map.of("role", "user", "parts", List.of(part));
        final Map<String, Object> body = Map.of("contents", List.of(content));

        final String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing request body for prompt '{}'", prompt, e);
            throw new RuntimeException("Failed to serialize request body", e);
        }

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("X-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * Extracts the raw JSON array text from the API response.
     */
    private String extractJsonArray(final String responseBody) throws JsonProcessingException {
        final JsonNode rootNode = objectMapper.readTree(responseBody);
        final String rawText = rootNode
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        // Remove triple backticks if present
        return rawText.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "");
    }

    /**
     * Parses JSON array text into a list of ProductPriceDetail objects.
     */
    private List<ProductPriceDetail> parseStores(final String jsonArrayText) {
        final List<ProductPriceDetail> stores = new ArrayList<>();

        // Quick check if it looks like JSON array
        if (jsonArrayText == null || !jsonArrayText.trim().startsWith("[")) {
            logger.warn("Response is not a JSON array: {}", jsonArrayText);
            return stores; // return empty list
        }

        try {
            final JsonNode jsonArray = objectMapper.readTree(jsonArrayText);
            if (jsonArray.isArray()) {
                for (final JsonNode node : jsonArray) {
                    stores.add(objectMapper.treeToValue(node, ProductPriceDetail.class));
                }
            } else {
                logger.warn("Expected JSON array but received: {}", jsonArrayText);
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON array: {}", jsonArrayText, e);
        }

        return stores;
    }
}
