package com.cartify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private String customerName;
    private String customerEmail;
    private List<OrderItem> items;
    private double totalAmount;
    private String status;
    private Date orderDate;
    private Date updatedAt;

    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = new Date();
        this.updatedAt = new Date();
    }

    public Order(int id, String customerName, String customerEmail, String status) {
        this();
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = new Date();
    }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public void addItem(OrderItem item) {
        this.items.add(item);
        calculateTotal();
    }

    private void calculateTotal() {
        this.totalAmount = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}