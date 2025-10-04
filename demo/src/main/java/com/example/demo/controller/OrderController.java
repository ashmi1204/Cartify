package com.example.demo.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.model.Order;
import com.example.demo.model.dto.OrderDetailsDTO;
import com.example.demo.model.dto.OrderRequestDTO;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        log.info("Received order request: {}", new Gson().toJson(orderRequest));

        try {
            // Validate request
            if (orderRequest.getCustomerId() == null) {
                log.error("Customer ID is null");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Customer ID is required"));
            }

            if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
                log.error("Order items are empty");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Order must contain at least one item"));
            }

            Order newOrder = orderService.createOrder(orderRequest);
            log.info("Order created successfully with ID: {}", newOrder.getOrder_id());
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(e.getMessage()));

        } catch (IllegalStateException e) {
            log.error("Business logic error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error during checkout: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to process order: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDetailsDTO>> getAllOrders() {
        List<OrderDetailsDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsDTO> getOrderById(@PathVariable Long id) {
        return orderService.getOrderDetailsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id,
                                                  @RequestBody Map<String, String> statusUpdate) {
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