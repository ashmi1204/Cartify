package com.example.demo.model.dto;

import java.util.List;

public class OrderRequestDTO {
    private Long customerId;
    private List<OrderItemDTO> items;

    // Inner class for items in the request
    public static class OrderItemDTO {
        private Long productId;
        private int quantity;
        private double price;

        // Getters & Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    // Getters & Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
