package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//import java.util.Map;

@RestController
@RequestMapping("/api/orders") // Base URL path: /api/orders
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // --- 1. PLACE ORDER (POST /api/orders) ---
    // Takes the Order object (including customer_id) in the request body
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        try {
            Order newOrder = orderService.createOrder(order);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED); // Status 201
        } catch (IllegalArgumentException e) {
            // Handle business logic failure (e.g., missing customer ID)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Status 400
        }
    }

    // --- 2. GET ORDER BY ID (GET /api/orders/{id}) ---
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // --- 3. GET ORDERS BY CUSTOMER (GET /api/orders/customer/{customerId}) ---
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}