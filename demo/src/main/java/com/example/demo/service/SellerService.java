package com.example.demo.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SellerService {
    private final String BASE_URL = "http://localhost:8080/api";
    private final RestTemplate restTemplate;
    private final Gson gson;

    public SellerService() {
        this.restTemplate = new RestTemplate();
        // Custom Gson builder to handle LocalDateTime from API
        this.gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                        LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        ).create();
    }

    // --- Product Methods ---
    public List<Product> getAllProducts() {
        try {
            String json = restTemplate.getForObject(BASE_URL + "/products", String.class);
            return gson.fromJson(json, new TypeToken<List<Product>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean saveProduct(Product product) {
        try {
            restTemplate.postForEntity(BASE_URL + "/products", product, Product.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        try {
            restTemplate.put(BASE_URL + "/products/" + product.getId(), product);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(Long productId) {
        try {
            restTemplate.delete(BASE_URL + "/products/" + productId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Order Methods (Implemented) ---
    public List<Order> getAllOrders() {
        try {
            String json = restTemplate.getForObject(BASE_URL + "/orders/all", String.class);
            return gson.fromJson(json, new TypeToken<List<Order>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Order> getOrdersByStatus(String status) {
        List<Order> allOrders = getAllOrders();
        if ("ALL".equalsIgnoreCase(status)) {
            return allOrders;
        }
        return allOrders.stream()
                .filter(order -> status.equalsIgnoreCase(order.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean updateOrderStatus(Long orderId, String newStatus) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(Map.of("status", newStatus), headers);
            restTemplate.exchange(BASE_URL + "/orders/" + orderId + "/status", HttpMethod.PUT, entity, Void.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Statistics ---
    public int getTotalProducts() {
        return getAllProducts().size();
    }

    public int getTotalOrders() {
        return getAllOrders().size();
    }

    public int getPendingOrders() {
        return (int) getAllOrders().stream()
                .filter(order -> "PENDING".equalsIgnoreCase(order.getStatus()))
                .count();
    }

    // --- Nested classes to match API DTOs for the GUI layer ---
    public static class Product {
        private Long id;
        private String name;
        private String description;
        private double price;
        private int quantity;
        private String category;
        private String image;
        // Getters & Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
    }

    public static class Order {
        private Long id;
        private String customerName;
        private String customerEmail;
        private List<OrderItem> items;
        private double totalAmount;
        private String status;
        private LocalDateTime orderDate;
        // Getters & Setters
        public Long getId() { return id; }
        public String getCustomerName() { return customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public List<OrderItem> getItems() { return items; }
        public double getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
        public LocalDateTime getOrderDate() { return orderDate; }
    }

    public static class OrderItem {
        private String productName;
        private int quantity;
        private double price;
        // Getters & Setters
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public double getTotalPrice() { return price * quantity; }
    }
}