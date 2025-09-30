package com.cartify.cartify.controller;

import com.cartify.cartify.model.Product;
import com.cartify.cartify.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    @GetMapping public List<Product> getAll() { return service.getAllProducts(); }
    @PostMapping public Product add(@RequestBody Product p) { return service.addProduct(p); }
    @DeleteMapping("/{id}") public void delete(@PathVariable int id) { service.deleteProduct(id); }
    @PutMapping("/increase/{id}/{amt}") public Product increase(@PathVariable int id,@PathVariable int amt){ return service.increaseQuantity(id,amt);}
    @PutMapping("/decrease/{id}/{amt}") public Product decrease(@PathVariable int id,@PathVariable int amt){ return service.decreaseQuantity(id,amt);}
    @GetMapping("/category/{cat}") public List<Product> byCategory(@PathVariable String cat){ return service.getProductsByCategory(cat);}
    @GetMapping("/price") public List<Product> sortByPrice(@RequestParam boolean ascending){ return service.getProductsSortedByPrice(ascending);}
}
