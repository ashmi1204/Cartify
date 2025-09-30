package com.cartify.cartify.model;

import jakarta.persistence.*;

@Entity
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int productId;

    private String productName;
    private String productDescription;
    private int productPrice;
    private int productQty;
    private String category;

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
    public int getProductPrice() { return productPrice; }
    public void setProductPrice(int productPrice) { this.productPrice = productPrice; }
    public int getProductQty() { return productQty; }
    public void setProductQty(int productQty) { this.productQty = productQty; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return productId + ": " + productName + " | " + productDescription + " | Price: " + productPrice + " | Qty: " + productQty + " | Category: " + category;
    }
}
