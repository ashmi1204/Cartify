package com.cartify;

import javax.swing.*;
import java.util.Date;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private String category;
    private String sellerId;
    private Date createdAt;
    private boolean active;
    private String imageUrl; // New field for image URL

    public Product() {}

    public Product(int id, String name, String description, double price,
                   int stockQuantity, String category, String sellerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.sellerId = sellerId;
        this.createdAt = new Date();
        this.active = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Helper method to get product image
    public ImageIcon getProductImage(int width, int height) {
        return ImageUtils.getProductImage(this.name, width, height);
    }
}