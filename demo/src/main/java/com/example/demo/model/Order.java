package com.example.demo.model;

import java.time.LocalDateTime;

public class Order {

    private Long order_id;
    private LocalDateTime order_date; // Maps to order_date
    private String order_status;      // Maps to order_status
    private Long customer_id;         // Maps to customer_id (Foreign Key)

    // --- Constructors ---
    public Order() {}

    // --- Getters and Setters ---

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public LocalDateTime getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDateTime order_date) {
        this.order_date = order_date;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public Long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Long customer_id) {
        this.customer_id = customer_id;
    }
}