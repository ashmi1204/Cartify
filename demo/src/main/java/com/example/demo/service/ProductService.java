package com.example.demo.service; // Adjust package name

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring business service
public class ProductService {

    private final ProductRepo productRepository;

    @Autowired
    public ProductService(ProductRepo productRepository) {
        this.productRepository = productRepository;
    }

    // --- CREATE/SAVE OPERATION ---
    public Product saveProduct(Product product) {
        // Here you would add business validation (e.g., check price > 0)
        return productRepository.save(product);
    }

    // --- READ ALL OPERATION ---
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // --- READ BY ID OPERATION ---
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // --- UPDATE OPERATION ---
    public Product updateProduct(Long id, Product productDetails) {
        // 1. Find the existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found for this id :: " + id));

        // 2. Update the fields
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        // ... set other fields

        // 3. Save and return the updated product
        return productRepository.save(product);
    }

    // --- DELETE OPERATION ---
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found for this id :: " + id));

        productRepository.deleteById(id);
    }
}