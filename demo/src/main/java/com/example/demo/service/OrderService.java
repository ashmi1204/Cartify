package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepo orderRepository;

    @Autowired
    public OrderService(OrderRepo orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Creates a new order, setting the date and initial status.
     */
    public Order createOrder(Order order) {
        // Business logic: Set required defaults
        if (order.getOrder_date() == null) {
            order.setOrder_date(LocalDateTime.now());
        }
        if (order.getOrder_status() == null || order.getOrder_status().isEmpty()) {
            order.setOrder_status("PENDING"); // Set a default status
        }

        // Validation: Ensure a customer ID is present
        if (order.getCustomer_id() == null) {
            throw new IllegalArgumentException("Cannot create an order without a customer ID.");
        }

        return orderRepository.save(order);
    }

    /**
     * Retrieves an order by its unique ID.
     */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Retrieves all orders for a specific customer.
     */
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}