package com.example.demo.gui;

import com.example.demo.model.Product;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SellerService {
    private final String BASE_URL = "http://localhost:8080/api";
    private final RestTemplate restTemplate;

    public SellerService() {
        this.restTemplate = new RestTemplate();
    }

    // Product Methods
    public List<Product> getAllProducts() {
        try {
            ResponseEntity<Product[]> response = restTemplate.getForEntity(BASE_URL + "/products", Product[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean saveProduct(Product product) {
        try {
            ResponseEntity<Product> response = restTemplate.postForEntity(BASE_URL + "/products", product, Product.class);
            return response.getStatusCode().is2xxSuccessful();
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

    // Order Methods
    public List<Order> getAllOrders() {
        // For demo - in real implementation, you'd fetch from your order API
        return new ArrayList<>();
    }

    public List<Order> getOrdersByStatus(String status) {
        if ("ALL".equals(status)) {
            return getAllOrders();
        }
        return getAllOrders().stream()
                .filter(order -> status.equals(order.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean updateOrderStatus(Long orderId, String newStatus) {
        // For demo - in real implementation, you'd call your order API
        return true;
    }

    // Statistics
    public int getTotalProducts() {
        return getAllProducts().size();
    }

    public int getTotalOrders() {
        return getAllOrders().size();
    }

    public int getPendingOrders() {
        return (int) getAllOrders().stream()
                .filter(order -> "PENDING".equals(order.getStatus()))
                .count();
    }
}
