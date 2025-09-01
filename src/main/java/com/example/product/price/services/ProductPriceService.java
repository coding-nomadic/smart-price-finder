package com.example.product.price.services;

import com.example.product.price.models.ProductPriceDetail;
import com.example.product.price.utils.ProductPriceUtils;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Service
public class ProductPriceService {

    private static final Logger logger = LoggerFactory.getLogger(ProductPriceService.class);
    private final String apiUrl;
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public ProductPriceService(@Value("${gemini.api.url}") final String apiUrl,
                               @Value("${gemini.api.key}") final String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .executor(virtualThreadExecutor)
                .build();
    }

    @Cacheable(value = "stores", key = "#query + '-' + #city + '-' + #province")
    public List<ProductPriceDetail> fetchStores(String query, String city, String province) {
        final String prompt = ProductPriceUtils.setPrompt(query, city, province);

        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return sendRequestAndParse(prompt);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, virtualThreadExecutor).join();
        } catch (Exception e) {
            logger.error("Error fetching stores for prompt '{}'", prompt, e);
            return List.of();
        }
    }

    private List<ProductPriceDetail> sendRequestAndParse(String prompt) throws IOException, InterruptedException {
        final String responseBody = sendRequest(prompt);
        final String jsonArrayText = extractJsonArray(responseBody);
        return parseStores(jsonArrayText);
    }

    private String sendRequest(String prompt) throws IOException, InterruptedException {
        final Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        final String requestBody = objectMapper.writeValueAsString(body);

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("X-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        logger.info("Running fetchStores on thread: {}, isVirtual: {}",
                Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    private String extractJsonArray(final String responseBody) throws JsonProcessingException {
        final JsonNode rootNode = objectMapper.readTree(responseBody);
        final String rawText = rootNode.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();
        return rawText.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "");
    }

    private List<ProductPriceDetail> parseStores(final String jsonArrayText) {
        final List<ProductPriceDetail> stores = new ArrayList<>();
        if (jsonArrayText == null || !jsonArrayText.trim().startsWith("[")) {
            logger.warn("Response is not a JSON array: {}", jsonArrayText);
            return stores;
        }
        try {
            final JsonNode jsonArray = objectMapper.readTree(jsonArrayText);
            if (jsonArray.isArray()) {
                for (final JsonNode node : jsonArray) {
                    stores.add(objectMapper.treeToValue(node, ProductPriceDetail.class));
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON array: {}", jsonArrayText, e);
        }
        return stores;
    }
}
