package com.cartfy.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;

@RestController
public class ProductController {

    // A private list to hold our products
    private final List<Product> productList;

    // This is a constructor. It runs when the controller is created.
    public ProductController() {
        this.productList = new ArrayList<>();
        // Add all your mock data here
        this.productList.add(new Product("Laptop", 1200.00, 50, "https://i.imgur.com/jwk5Vv7.jpeg"));
        this.productList.add(new Product("Mouse", 25.50, 200, "https://i.imgur.com/ka3rR4j.jpeg"));
        this.productList.add(new Product("Keyboard", 75.00, 150, "https://i.imgur.com/4lG4m24.jpeg"));
        this.productList.add(new Product("Webcam", 50.00, 75, "https://i.imgur.com/a5n9lk0.jpeg"));
        this.productList.add(new Product("Monitor", 350.00, 100, "https://i.imgur.com/k2t6p4L.jpeg"));
    }

    @GetMapping("/api/products")
    public List<Product> getAllProducts() {
        // Now, this method returns our actual list
        return this.productList;
    }
}
