package com.example.product.price.utils;

public final class ProductPriceUtils {

    private ProductPriceUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
   public static String setPrompt(String query, String city, String province) {
        return """
                Find a list of %s products available near %s, %s. Only include stores within 20 km of the city center. Return up to 50 nearest stores, sorted by ascending price. For each entry, provide the following fields: productName (string) – name of the product, storeName (string) – name of the store, price (number) – in CAD, e.g., 4.49, discount (string) – e.g., "5%% off" or "No discount", lastUpdated (string) – ISO 8601 date format, e.g., "2025-08-26", website (string) – URL to the store’s product page, availability (string) – e.g., "In Stock", "Low Stock", "Out of Stock", rating (number) – 0 to 5, e.g., 4.5, storeType (string) – either "Retail" or "Online", distance (string) – distance from city center, e.g., "4.49 km", location (string) – full address. Return the result in JSON array format only, with no extra commentary. Example entry: { "productName": "Paracetamol 500mg Tablets (24)", "storeName": "Midtown Pharmacy", "price": 4.49, "distance": "4.49 km", "location": "2416 Lake Shore Blvd W, Etobicoke, ON M8V 1C4", "discount": "5%% off", "lastUpdated": "2025-08-26", "website": "https://www.midtownpharmacy.ca", "availability": "In Stock", "rating": 4.5, "storeType": "Retail" }
                               
                """.formatted(query, city, province);
    }
}
