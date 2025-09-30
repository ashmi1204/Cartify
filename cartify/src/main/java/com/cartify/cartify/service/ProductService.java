package com.cartify.cartify.service;

import com.cartify.cartify.model.Product;
import com.cartify.cartify.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repo;
    public ProductService(ProductRepository repo) { this.repo = repo; }

    public List<Product> getAllProducts() { return repo.findAll(); }
    public Product addProduct(Product p) { return repo.save(p); }
    public void deleteProduct(int id) { repo.deleteById(id); }
    public Product increaseQuantity(int id, int amount) {
        Product p = repo.findById(id).orElseThrow();
        p.setProductQty(p.getProductQty() + amount);
        return repo.save(p);
    }
    public Product decreaseQuantity(int id, int amount) {
        Product p = repo.findById(id).orElseThrow();
        if(p.getProductQty() < amount) throw new RuntimeException("Not enough stock");
        p.setProductQty(p.getProductQty() - amount);
        return repo.save(p);
    }
    public List<Product> getProductsByCategory(String category) { return repo.findByCategory(category); }
    public List<Product> getProductsSortedByPrice(boolean asc) { 
        return asc ? repo.findAllByOrderByProductPriceAsc() : repo.findAllByOrderByProductPriceDesc();
    }
}
