package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.model.dto.OrderDetailsDTO;
import com.example.demo.model.dto.OrderRequestDTO;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // --- Endpoint for CUSTOMERS to place an order ---
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        try {
            Order newOrder = orderService.createOrder(orderRequest);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // --- Endpoint for ADMINS/SELLERS to get ALL orders ---
    @GetMapping("/all")
    public ResponseEntity<List<OrderDetailsDTO>> getAllOrders() {
        List<OrderDetailsDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // --- Endpoint to get a SINGLE order's details ---
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsDTO> getOrderById(@PathVariable Long id) {
        return orderService.getOrderDetailsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Endpoint for ADMINS/SELLERS to update order status ---
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            String newStatus = statusUpdate.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            orderService.updateOrderStatus(id, newStatus.toUpperCase());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}