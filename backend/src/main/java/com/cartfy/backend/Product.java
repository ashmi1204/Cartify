package com.cartfy.backend;

public class Product {
    private String name;
    private double price;
    private int stock;
    private String imageUrl;

    // This is a constructor. It's used to create new Product objects.
    public Product(String name, double price, int stock, String imageUrl) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    // --- Getters ---
    // These methods let other parts of the code read the values of the private fields.
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // --- Setters ---
    // These methods let other parts of the code change the values of the private fields.
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}