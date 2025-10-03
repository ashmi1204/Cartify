package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class to handle REST requests
@RequestMapping("/api/products") // Base URL path for all methods: /api/products
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // --- 1. CREATE (POST) /api/products ---
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);

        // Returns the saved object with its new ID and a 201 Created status
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // --- 2. READ ALL (GET) /api/products ---
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();

        // Returns the list and a 200 OK status
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // --- 3. READ BY ID (GET) /api/products/{id} ---
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        // Calls service layer, which returns an Optional<Product>
        return productService.getProductById(id)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK)) // Found: Status 200
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Not Found: Status 404
    }

    // --- 4. UPDATE (PUT) /api/products/{id} ---
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        try {
            // Service handles finding the old product and saving the updated details
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Catches the "Product not found" exception typically thrown by the service
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // --- 5. DELETE (DELETE) /api/products/{id} ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Status 204 No Content
        } catch (RuntimeException e) {
            // Handle case where product doesn't exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}