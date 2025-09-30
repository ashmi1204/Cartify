package com.cartify.cartify.repository;

import com.cartify.cartify.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategory(String category);
    List<Product> findAllByOrderByProductPriceAsc();
    List<Product> findAllByOrderByProductPriceDesc();
}
