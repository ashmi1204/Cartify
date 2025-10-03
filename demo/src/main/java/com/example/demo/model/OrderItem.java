package com.example.demo.model;

public class OrderItem {
    private Long order_item_id;
    private Long order_id;
    private Long product_id;
    private int quantity;
    private double unit_price;

    // Getters and Setters
    public Long getOrder_item_id() { return order_item_id; }
    public void setOrder_item_id(Long order_item_id) { this.order_item_id = order_item_id; }
    public Long getOrder_id() { return order_id; }
    public void setOrder_id(Long order_id) { this.order_id = order_id; }
    public Long getProduct_id() { return product_id; }
    public void setProduct_id(Long product_id) { this.product_id = product_id; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnit_price() { return unit_price; }
    public void setUnit_price(double unit_price) { this.unit_price = unit_price; }
}