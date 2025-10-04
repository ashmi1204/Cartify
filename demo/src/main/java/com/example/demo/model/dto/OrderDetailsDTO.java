package com.example.demo.model.dto;

import java.time.LocalDateTime;
import java.util.List;

// This DTO combines data for API responses
public class OrderDetailsDTO {
    private Long id;
    private String customerName;
    private String customerEmail;
    private List<OrderItemDetailDTO> items;
    private double totalAmount;
    private String status;
    private LocalDateTime orderDate;

    public static class OrderItemDetailDTO {
        private String productName;
        private int quantity;
        private double price;

        // Constructor, Getters & Setters
        public OrderItemDetailDTO(String productName, int quantity, double price) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public List<OrderItemDetailDTO> getItems() { return items; }
    public void setItems(List<OrderItemDetailDTO> items) { this.items = items; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
}